package se.newbie.smartframe.plugin.instagram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import se.newbie.smartframe.SmartFrameApplication;
import se.newbie.smartframe.model.IPresentationProviderState;
import se.newbie.smartframe.model.impl.AbstractPresentationProvider;
import se.newbie.smartframe.model.impl.PresentationDefinitionImpl;
import android.content.Context;
import android.util.Log;

public class InstagramPresentationProvider extends AbstractPresentationProvider {
	private final static String TAG = InstagramPresentationProvider.class.getCanonicalName();
	private static final String NAME = "InstagramPresentationProvider";
	
	
	private static final String CLIENT_ID = "";
	private static final String CLIENT_SECRET = "";
	private static final String REDIRECT_URI = "instagram://callback";

	private static final String AUTHORIZE_URL = "https://api.instagram.com/oauth/authorize/";
	private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";	
	private static final String API_URL = "https://api.instagram.com/v1/";
	
	private InstagramSession mInstagramSession = null;
	
	private String mAuthorizeUrl = null;
	private LinkedList<InstagramPresentation> mQueue = new LinkedList<InstagramPresentation>(); 
	private InstagramPresentationProviderThread mThread = null;
	
	
	public interface InstagramRequestListener {
		public void onComplete(JSONObject aResult);
	}
	
	public InstagramPresentationProvider() {
		init(SmartFrameApplication.getInstance().getActivity());
	}
	
	public void init(Context aContext) {
		mInstagramSession = new InstagramSession(aContext);
		mAuthorizeUrl = AUTHORIZE_URL +"?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=code&display=touch&scope=likes+comments+relationships";
	}
	
	public void authorize(Context aContext) {
		InstagramAuthorizeDialog dialog = new InstagramAuthorizeDialog(aContext, mAuthorizeUrl, REDIRECT_URI, new InstagramAuthorizeDialog.IOAuthDialogListener() {
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
	
	private void requestAccessToken(final String aCode) {
		Log.v(TAG, "requestAccessToken for " + aCode);
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
								"&grant_type=authorization_code" +
								"&code=" + aCode);
					
					
				    writer.flush();
					String response = readToString(urlConnection.getInputStream());
					Log.v(TAG, "response : " + response);
					JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();

					String accessToken = jsonObj.getString("access_token");
					String id = jsonObj.getJSONObject("user").getString("id");
					String user = jsonObj.getJSONObject("user").getString("username");
					String name = jsonObj.getJSONObject("user").getString("full_name");						
					mInstagramSession.storeSession(accessToken, id, user, name);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
//				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
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
	
	private void requestFeed(final InstagramRequestListener aInstagramRequestListener) {
		request(API_URL + "users/self/feed?access_token=" + mInstagramSession.getAccessToken(), aInstagramRequestListener);
	}
	
	private void request(final String aUrl, final InstagramRequestListener aInstagramRequestListener) {
		new Thread() {
			@Override
			public void run() {
				try {
					URL url = new URL(aUrl);
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.connect();
					
					String response = readToString(urlConnection.getInputStream());
					//Log.v(TAG, "response : " + response);
					JSONObject json = (JSONObject) new JSONTokener(response).nextValue();
					aInstagramRequestListener.onComplete(json);
				} catch (Exception e) {
					Log.e(TAG, "Error parsing response", e);
				}
			}
		}.start();		
	}

	private void add(InstagramPresentation aInstagramPresentation) {
		if (!mQueue.contains(aInstagramPresentation)) {
			mQueue.add(aInstagramPresentation);	
		}
	}	
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void onResume(IPresentationProviderState aPresentationProviderState) {
		Log.d(TAG, "onResume");
		mThread = new InstagramPresentationProviderThread();
		mThread.start();	
		
		if (!mInstagramSession.hasAccessToken()) {
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
		requestFeed(new InstagramRequestListener() {
			@Override
			public void onComplete(JSONObject aResult) {
				JSONArray array = aResult.optJSONArray("data");
				if (array != null) {
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.optJSONObject(i);
						if (object != null) {
							boolean wasEmpty = mQueue.isEmpty();
							add(new InstagramPresentation(object, PresentationDefinitionImpl.createDefault()));
							//Add next presentation if queue was empty and this is the first presentation
							if (wasEmpty && i == 0) {
								nextPresentation();
							}							
						}	
					}
				}
			}
		});		
	}
	
	private void nextPresentation() {
		if (!mQueue.isEmpty()) {
			SmartFrameApplication.getInstance().getModel().addPresentation(mQueue.remove());
		}
	}
	
	private class InstagramPresentationProviderThread extends Thread {
		private boolean mIsRunning = true;
		private long mLastUpdateTime = 0;
			
		@Override
        public void run() {
			try {
				while (mIsRunning) {
					if (mInstagramSession != null && mInstagramSession.hasAccessToken()) {
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
