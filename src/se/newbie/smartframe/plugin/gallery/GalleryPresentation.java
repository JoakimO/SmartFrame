package se.newbie.smartframe.plugin.gallery;

import se.newbie.smartframe.R;
import se.newbie.smartframe.SmartFrameApplication;
import se.newbie.smartframe.model.IHasPresentationDefinition;
import se.newbie.smartframe.model.IHasPresentationPreload;
import se.newbie.smartframe.model.IPresentation;
import se.newbie.smartframe.model.IPresentationDefinition;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class GalleryPresentation implements IPresentation, IHasPresentationDefinition, IHasPresentationPreload {
	
	private final static String TAG = GalleryPresentation.class.getCanonicalName();
	
	private IPresentationDefinition mPresentationDefinition = null;

	public interface IGalleryPresentationListener {
		public void onComplete(GalleryPresentation aGalleryPresentation);
	}
	
	private int mWidth = -1;
	private int mHeight = -1;
	private Uri mUri = null;
	private Bitmap mBitmap = null;
	private Thread mPreloadThread = null;
	
	public GalleryPresentation(Uri aUri, IPresentationDefinition aPresentationDefinition) {
		mUri = aUri;
		mPresentationDefinition = aPresentationDefinition;
	}
	
	@Override
	public View createLayout(Context aContext) {
		LayoutInflater inflater = (LayoutInflater)aContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.presentation_gallery, null);
		
		ImageView imageView = (ImageView)view.findViewById(R.id.presentation_gallery_image_view);
		if (mBitmap != null) {
			imageView.setImageBitmap(mBitmap);
		}
		
		view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
			@Override
			public void onViewDetachedFromWindow(View v) {
				if (mBitmap != null) {
					mBitmap.recycle();
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
		Log.i(TAG, "preload : " + mUri);
		final GalleryPresentation presentation = this; 
		mPreloadThread = new Thread() {
			@Override
			public void run() {
				try {
					aPreloadCallback.onStart();
				    BitmapFactory.Options optionsIn = new BitmapFactory.Options();
				    optionsIn.inJustDecodeBounds = true;
				    BitmapFactory.decodeFile(mUri.getPath(), optionsIn);
					    
				    BitmapFactory.Options optionsOut = new BitmapFactory.Options();
				    int requiredWidth = SmartFrameApplication.getInstance().getWindowSize().x;
					    
				    float bitmapWidth = optionsIn.outWidth;
				    int scale = Math.round(bitmapWidth / requiredWidth);
				    optionsOut.inSampleSize = scale;
				    optionsOut.inPurgeable = true;					    
						
					mBitmap = BitmapFactory.decodeFile(mUri.getPath(), optionsOut);

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
