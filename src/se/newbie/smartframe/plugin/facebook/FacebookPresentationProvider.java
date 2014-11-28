package se.newbie.smartframe.plugin.facebook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import se.newbie.smartframe.SmartFrameApplication;
import se.newbie.smartframe.model.IPresentationProviderState;
import se.newbie.smartframe.model.impl.AbstractPresentationProvider;
import se.newbie.smartframe.model.impl.PresentationDefinitionImpl;
import se.newbie.smartframe.plugin.facebook.model.FacebookModel;
import se.newbie.smartframe.util.HttpUtil;
import android.content.Context;
import android.util.Log;

public class FacebookPresentationProvider extends AbstractPresentationProvider {
	private final static String TAG = FacebookPresentationProvider.class.getCanonicalName();
	private static final String NAME = "FacebookPresentationProvider";
	
	
	private static final String CLIENT_ID = "";
	private static final String CLIENT_SECRET = "";
	private static final String REDIRECT_URI = "http://callback.newbie.se";

	private static final String AUTHORIZE_URL = "https://www.facebook.com/dialog/oauth";
	private static final String TOKEN_URL = "https://graph.facebook.com/oauth/access_token";	
	private static final String API_URL = "https://graph.facebook.com/";
	
	private FacebookSession mFacebookSession = null;
	
	private String mAuthorizeUrl = null;
	private LinkedList<FacebookPresentation> mQueue = new LinkedList<FacebookPresentation>(); 
	private FacebookPresentationProviderThread mThread = null;
	
	private FacebookModel mFacebookModel = null;
	
	
	public interface FacebookRequestListener {
		public void onComplete(JSONObject aResult);
	}
	
	public FacebookPresentationProvider() {
		init(SmartFrameApplication.getInstance().getActivity());
	}
	
	public void init(Context aContext) {
		mFacebookSession = new FacebookSession(aContext);
		//mFacebookSession.reset();		
		mAuthorizeUrl = AUTHORIZE_URL +"?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=token&scope=user_photos";
	}
	
	public void authorize(Context aContext) {
		FacebookAuthorizeDialog dialog = new FacebookAuthorizeDialog(aContext, mAuthorizeUrl, REDIRECT_URI, new FacebookAuthorizeDialog.IOAuthDialogListener() {
			@Override
			public void onError() {
				
			}
			
			@Override
			public void onComplete(String aToken) {
				requestAccessToken(aToken);
			}
		});
		dialog.show();
	}
	

	private void requestAccessToken(final String aAccessToken) {
		Log.v(TAG, "requestExtendedAccessToken for " + aAccessToken);
		new Thread() {
			@Override
			public void run() {
				try {
					URL url = new URL(TOKEN_URL);
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("POST");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);

					OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
					writer.write("client_id=" + CLIENT_ID + 
								"&client_secret=" + CLIENT_SECRET +
								"&redirect_uri=" + REDIRECT_URI +
								"&grant_type=fb_exchange_token" +
								"&fb_exchange_token=" + aAccessToken);
					
					
				    writer.flush();
					String response = readToString(urlConnection.getInputStream());
					Log.v(TAG, "response : " + response);
					 
					Map<String, String> parameters = HttpUtil.parseQueryString(response);

					if (!parameters.containsKey("error")) {
						String accessToken = parameters.get("access_token");
						//requestUserInfo(accessToken);
						mFacebookSession.storeSession(accessToken);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.start();		
		
	}

	private String readToString(InputStream aInputStream) throws IOException {
		String result = null;

		if (aInputStream != null) {
			StringBuilder sb = new StringBuilder();
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(aInputStream));

				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				reader.close();
			} finally {
				aInputStream.close();
			}
			result = sb.toString();
		}
		return result;
	}	
	
	private void requestAlbums(final FacebookRequestListener aFacebookRequestListener) {
		//request(API_URL + "me?fields=id,name,albums.fields(photos)&access_token=" + mFacebookSession.getAccessToken(), aFacebookRequestListener);
		//request(API_URL + "me?fields=id,name,albums.fields(photos.fields(images))&access_token=" + mFacebookSession.getAccessToken(), aFacebookRequestListener);
		request(API_URL + "me?fields=id,name,albums.fields(photos.fields(images),name,id)&access_token=" + mFacebookSession.getAccessToken(), aFacebookRequestListener);
		
		
	}
	
	private void updateAlbums() {
		request(API_URL + "me?fields=id,name,albums.fields(name,id,link)&access_token=" + mFacebookSession.getAccessToken(), new FacebookRequestListener() {
			@Override
			public void onComplete(JSONObject aResult) {
				mFacebookModel.setAlbums(aResult.optJSONObject("albums"));
			}
		});
	}
	
	private void request(final String aUrl, final FacebookRequestListener aFacebookRequestListener) {
		new Thread() {
			@Override
			public void run() {
				try {
					Log.v(TAG, "Request : " + aUrl);
					URL url = new URL(aUrl);
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.connect();
					
					String response = readToString(urlConnection.getInputStream());
					//Log.v(TAG, "response : " + response);
					JSONObject json = (JSONObject) new JSONTokener(response).nextValue();
					aFacebookRequestListener.onComplete(json);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.start();		
	}

	private void add(FacebookPresentation aFacebookPresentation) {
		if (!mQueue.contains(aFacebookPresentation)) {
			mQueue.add(aFacebookPresentation);	
		}
	}	
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void onResume(IPresentationProviderState aPresentationProviderState) {
		Log.d(TAG, "onResume");
		
		mFacebookModel = new FacebookModel();
		
		mThread = new FacebookPresentationProviderThread();
		mThread.start();	
		
		if (!mFacebookSession.hasAccessToken()) {
			authorize(SmartFrameApplication.getInstance().getActivity());
		}
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause");
		if (mThread != null) {
			mThread.setRunning(false);
			mThread.interrupt();
			mThread = null;
		}		
	}
	
	public void updateQueue() {
		requestAlbums(new FacebookRequestListener() {
			@Override
			public void onComplete(JSONObject aResult) {
				try {
					int imageCount = 0;
					JSONObject albums = aResult.optJSONObject("albums");
					JSONArray albumsData = albums.optJSONArray("data");
					for (int i = 0; i < albumsData.length(); i++) {
						JSONObject album = albumsData.optJSONObject(i);
						JSONObject photos = album.optJSONObject("photos");	
						JSONArray photosData = photos.optJSONArray("data");	
						for (int j = 0; j < photosData.length(); j++) {
							JSONObject photo = photosData.optJSONObject(j);
							JSONArray images = photo.optJSONArray("images");
							boolean wasEmpty = mQueue.isEmpty();
							add(new FacebookPresentation(images.optJSONObject(0), PresentationDefinitionImpl.createDefault()));
							if (wasEmpty && imageCount == 0) {
								imageCount++;
								nextPresentation();
							}							
						}
					}
				} catch (Exception e) {
					Log.e(TAG, "Error parsing response", e);
				}
			}
		});		
	}
	
	private void nextPresentation() {
		if (!mQueue.isEmpty()) {
			SmartFrameApplication.getInstance().getModel().addPresentation(mQueue.remove());
		}
	}
	
	
	private class FacebookPresentationProviderThread extends Thread {
		private boolean mIsRunning = true;
		private long mLastUpdateTime = 0;
			
		@Override
        public void run() {
			try {
				while (mIsRunning) {
					if (mFacebookSession != null && mFacebookSession.hasAccessToken()) {
						long l = System.currentTimeMillis() - mLastUpdateTime;
						if (l > (1000 * 60 * 10)) {
							updateQueue();
							mLastUpdateTime = System.currentTimeMillis();
						}	
						nextPresentation();
					}
					Thread.sleep(1000 * 20);					
				}
			} catch (InterruptedException e) {
				Log.v(TAG, "Thread interrupted");
			}
			
		}
		
		public void setRunning(boolean aRunning) {
			mIsRunning = aRunning;
		}
	};	
	
}
