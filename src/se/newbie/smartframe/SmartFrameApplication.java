package se.newbie.smartframe;

import java.util.ArrayList;
import java.util.List;

import se.newbie.smartframe.model.IHasModel;
import se.newbie.smartframe.model.IModel;
import se.newbie.smartframe.model.IPresentationProvider;
import se.newbie.smartframe.model.impl.ModelImpl;
import se.newbie.smartframe.plugin.facebook.FacebookPresentationProvider;
import se.newbie.smartframe.plugin.gallery.GalleryPresentationProvider;
import se.newbie.smartframe.plugin.instagram.InstagramPresentationProvider;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class SmartFrameApplication implements IHasModel {
	private static SmartFrameApplication mSmartFrameApplication = null;
	
	private Point mSize = null;
	private IModel mModel = null;
	private SmartFrameActivity mActivity = null;
	
	private List<IPresentationProvider> mPresentationProviders = null;
	
	public static SmartFrameApplication getInstance() {
		if (mSmartFrameApplication == null) {
			synchronized (SmartFrameApplication.class) {
				if (mSmartFrameApplication == null) {
					mSmartFrameApplication = new SmartFrameApplication();			
				}
			}
		}
		return mSmartFrameApplication;
	}
	
	public void init(SmartFrameActivity aActivity) {
		mActivity = aActivity;
		mModel = new ModelImpl();
		
		mPresentationProviders = new ArrayList<IPresentationProvider>();
		
		IPresentationProvider provider;
		
		provider = new InstagramPresentationProvider();
		provider.setId(0);
		mPresentationProviders.add(provider);
		provider = new GalleryPresentationProvider();
		provider.setId(1);
		mPresentationProviders.add(provider);
		provider = new FacebookPresentationProvider();
		provider.setId(2);
		mPresentationProviders.add(provider);				
	}
	
	public Point getWindowSize() {
		if (mSize == null) {
			WindowManager windowManager = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
			Display display = windowManager.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);	
			mSize = size;
		}
		return mSize;
	}
	
	public List<IPresentationProvider> getPresentationProviders() {
		return mPresentationProviders;
	}
	 

	public SmartFrameActivity getActivity() {
		return mActivity;
	}
	
	@Override
	public IModel getModel() {
		return mModel;
	}
	
	public void onResume() {
		mModel.onResume();
	}
	
	public void onPause() {
		mModel.onPause();
	}
	
	
}
