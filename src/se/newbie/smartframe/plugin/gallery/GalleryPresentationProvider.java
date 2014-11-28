package se.newbie.smartframe.plugin.gallery;

import java.util.ArrayList;
import java.util.List;

import se.newbie.smartframe.SmartFrameApplication;
import se.newbie.smartframe.model.IPresentationProviderState;
import se.newbie.smartframe.model.impl.AbstractPresentationProvider;
import se.newbie.smartframe.model.impl.PresentationDefinitionImpl;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

public class GalleryPresentationProvider extends AbstractPresentationProvider {
	private final static String TAG = GalleryPresentationProvider.class.getCanonicalName();
	private static final String NAME = "InstagramPresentationProvider";
	private static final int LOADER_ID = 1;

	private List<Uri> mUriList = new ArrayList<Uri>();
	private int mUriIndex = 0;
	
	private GalleryPresentationProviderThread mThread = null;
	
	@Override
	public String getName() {
		return NAME;
	}

	public GalleryPresentationProvider() {
		loadImages(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		loadImages(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
	}
	
	private void loadImages(final Uri aUri) {
		final String[] projection = { MediaStore.Images.Media.DATA };
		
		
		LoaderManager loaderManager = SmartFrameApplication.getInstance().getActivity().getLoaderManager();
		loaderManager.initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {

			@Override
			public Loader<Cursor> onCreateLoader(int aId, Bundle aBundle) {
				Loader<Cursor> loader = new CursorLoader(
		                SmartFrameApplication.getInstance().getActivity(), 
		                aUri, 
		                projection, 
		                null, 
		                null, 
		                MediaStore.Images.Media._ID + " DESC");
				return loader;
			}

			@Override
			public void onLoadFinished(Loader<Cursor> aLoader, Cursor aCursor) {
			    switch (aLoader.getId()) {
			      case LOADER_ID:
					if(aCursor != null && aCursor.moveToFirst()) {
						for (int i = 0; i < aCursor.getCount(); i++) {
							aCursor.moveToPosition(i);
							int index = aCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
							Uri uri = Uri.parse(aCursor.getString(index));
							//Toast.makeText(SmartFrameApplication.getInstance().getActivity(), uri.getPath(), Toast.LENGTH_SHORT).show();
							mUriList.add(uri);
						}
					}
			        break;
			    }
				
			}

			@Override
			public void onLoaderReset(Loader<Cursor> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	@Override
	public void onResume(IPresentationProviderState aState) {		
		Log.d(TAG, "onResume");
		mThread = new GalleryPresentationProviderThread();
		mThread.start();		
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
	
	protected void nextPresentation() {
		if (mUriList.size() > 0) {
			Uri uri = mUriList.get(mUriIndex % mUriList.size());
			GalleryPresentation presentation = new GalleryPresentation(uri, PresentationDefinitionImpl.createDefault());
			SmartFrameApplication.getInstance().getModel().addPresentation(presentation);
			mUriIndex = mUriIndex + 1;
		}
	}
	
	private class GalleryPresentationProviderThread extends Thread {
		private boolean mIsRunning = true;
			
		@Override
        public void run() {
			try {
				while (mIsRunning) {
					nextPresentation();
					Thread.sleep(1000 * 20);					
				}
			} catch (InterruptedException e) {
				Log.v(TAG, "Thread interrupted");
			}
			
		}
		
		public void setRunning(boolean aRunning) {
			mIsRunning = aRunning;
		}		
	}
}
