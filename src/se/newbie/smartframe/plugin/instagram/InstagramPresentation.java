package se.newbie.smartframe.plugin.instagram;

import java.net.URL;

import org.json.JSONObject;

import se.newbie.smartframe.R;
import se.newbie.smartframe.model.IHasPresentationDefinition;
import se.newbie.smartframe.model.IHasPresentationPreload;
import se.newbie.smartframe.model.IPresentation;
import se.newbie.smartframe.model.IPresentationDefinition;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class InstagramPresentation implements IPresentation, IHasPresentationDefinition, IHasPresentationPreload {
	
	private final static String TAG = InstagramPresentation.class.getCanonicalName();
	
	private IPresentationDefinition mPresentationDefinition = null;
	
	private int mWidth = -1;
	private int mHeight = -1;
	private String mUrl = null;
	private Bitmap mNetworkBitmap = null;
	
	private Thread mPreloadThread = null;
	
	public InstagramPresentation(JSONObject aJSONObject, IPresentationDefinition aPresentationDefinition) {
		JSONObject images = aJSONObject.optJSONObject("images");
		if (images != null) {
			
			Log.v(TAG, images.toString());			
			
			JSONObject standardResolution = images.optJSONObject("standard_resolution");
			if (standardResolution != null) {
				mUrl = standardResolution.optString("url");
				mWidth = standardResolution.optInt("width");
				mHeight = standardResolution.optInt("height");
			}
		}
		mPresentationDefinition = aPresentationDefinition;
	}
	
	@Override
	public View createLayout(Context aContext) {
		LayoutInflater inflater = (LayoutInflater)aContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.presentation_instagram, null);
		
		ImageView imageView = (ImageView)view.findViewById(R.id.presentation_instagram_image_view);
		if (mNetworkBitmap != null) {
			imageView.setImageBitmap(mNetworkBitmap);
		}		
		
		view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
			@Override
			public void onViewDetachedFromWindow(View v) {
				if (mNetworkBitmap != null) {
					Log.v(TAG, "Recycle");
					mNetworkBitmap.recycle();
				}
			}
			
			@Override
			public void onViewAttachedToWindow(View v) {
			}
		});
		
		return view;
	}

	@Override
	public IPresentationDefinition getPresentationDefinition() {
		return mPresentationDefinition;
	}

	@Override
	public void preload(final IPreloadCallback aPreloadCallback) {
		Log.i(TAG, "preload : " + mUrl);
		final InstagramPresentation presentation = this; 
		mPreloadThread = new Thread() {
			@Override
			public void run() {
				try {
					aPreloadCallback.onStart();
					URL url = new URL(mUrl);
					mNetworkBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
					aPreloadCallback.onComplete(presentation);					
				} catch (Exception ex) {
					aPreloadCallback.onError(ex);
				}
			}
		};	
		mPreloadThread.start();
	}

	@Override
	public void interruptPreload() {
		if (mPreloadThread != null) {
			mPreloadThread.interrupt();	
		}
	}

}
