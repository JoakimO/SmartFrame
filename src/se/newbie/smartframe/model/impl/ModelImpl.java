package se.newbie.smartframe.model.impl;

import java.util.ArrayList;
import java.util.List;

import se.newbie.smartframe.model.IHasPresentationDefinition;
import se.newbie.smartframe.model.IHasPresentationPreload;
import se.newbie.smartframe.model.IModel;
import se.newbie.smartframe.model.IPresentation;
import se.newbie.smartframe.model.IPresentationList;
import android.util.Log;

public class ModelImpl implements IModel{

	private final static long DEFAULT_TIME = 10000;
	private final static long PRESENTATION_LIST_THRESHOLD = 10;
	
	private final static String TAG = ModelImpl.class.getCanonicalName();
	
	private List<IPresentationChangeListener> mPresentationChangeListeners = new ArrayList<IPresentationChangeListener>();
	private List<IModelChangeListener> mModelChangeListeners = new ArrayList<IModelChangeListener>();
	
	private IPresentationList<IPresentation> mPresentationList = null;
	
	private IPresentation mCurrentPresentation = null;
	private long mCurrentPresentationTime = -1;
	
	private ModelThread mThread = null;

	private boolean mIsPendingPresentation = false;
	
	
	public ModelImpl() {
		mPresentationList = new PresentationListImpl<IPresentation>();
	}
		
	@Override
	public void registerPresentationChangeListener(IPresentationChangeListener aListener) {
		mPresentationChangeListeners.add(aListener);
	}


	@Override
	public void removePresentationChangeListener(IPresentationChangeListener aListener) {
		mPresentationChangeListeners.remove(aListener);		
	}


	@Override
	public void registerModelChangeListener(IModelChangeListener aListener) {
		mModelChangeListeners.add(aListener);
		
	}

	@Override
	public void removeModelChangeListener(IModelChangeListener aListener) {
		mModelChangeListeners.remove(aListener);
	}
	
	protected void notifyModelChangeListeners() {
		Log.d(TAG, "Notify model change listeners");
		for (IModelChangeListener listener : mModelChangeListeners) {
			listener.onModelChangeEvent(this);
		}
	}

	protected void notifyPresentationChangeListeners() {
		Log.d(TAG, "Notify presentation change listeners");
		for (IPresentationChangeListener listener : mPresentationChangeListeners) {
			
			listener.onPresentationChangeEvent(this);
		}
	}

	@Override
	public void addPresentation(IPresentation aPresentation) {
		boolean wasEmpty = mPresentationList.isEmpty();
		mPresentationList.add(aPresentation);
		
		if (wasEmpty && mThread != null && getNextPresentationTime() == 0) {
			mThread.interrupt();
		}
		
		notifyModelChangeListeners();
	}

	protected void setCurrentPresentation(IPresentation aPresentation) {
		if (aPresentation != null) {
			mCurrentPresentation = aPresentation;
			mCurrentPresentationTime = System.currentTimeMillis();
			notifyPresentationChangeListeners();
		}
	}
	
	@Override
	public IPresentation getCurrentPresentation() {
		return mCurrentPresentation;
	}
	
	@Override
	public int getPresentationSize() {
		return mPresentationList.size();
	}
	
	protected boolean isNextPresentation() {
		return (getNextPresentationTime() == 0 && !mIsPendingPresentation) ? true : false;
	}
	
	protected long getNextPresentationTime() {
		long time = DEFAULT_TIME;
		long elapsed = System.currentTimeMillis() - mCurrentPresentationTime;
		if (mCurrentPresentation != null && mCurrentPresentation instanceof IHasPresentationDefinition) {
			IHasPresentationDefinition definition = (IHasPresentationDefinition)mCurrentPresentation;
			if (mPresentationList.size() > PRESENTATION_LIST_THRESHOLD) {
				time = definition.getPresentationDefinition().getMinimumVisibleTime() - elapsed;
			} else {
				time =  definition.getPresentationDefinition().getPreferedVisibleTime() - elapsed;
			}
		} else if (mCurrentPresentation != null) {
			time = DEFAULT_TIME - elapsed;
		} else if (mCurrentPresentation == null) {
			time = 0;
		}
		
		if (time < 0) {
			time = 0;
		}
		return time;
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		mThread = new ModelThread();
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
	
	protected class ModelThread extends Thread {
		private boolean mIsRunning = true;
		
		@Override
        public void run() {
			
			while (mIsRunning) {
				try {
					if (isNextPresentation()) {
						if (!mPresentationList.isEmpty()) {
							IPresentation nextPresentation = mPresentationList.remove();
							if (nextPresentation != null && nextPresentation instanceof IHasPresentationPreload) {
								final IHasPresentationPreload presentation = (IHasPresentationPreload)nextPresentation;
								mIsPendingPresentation = true;
								presentation.preload(new IHasPresentationPreload.IPreloadCallback() {
									@Override
									public void onComplete(IPresentation aPresentation) {
										setCurrentPresentation(aPresentation);			
										mIsPendingPresentation = false;
									}
		
									@Override
									public void onError(Throwable aThrowable) {
										Log.v(TAG, "Error preloading presentation", aThrowable);
										mIsPendingPresentation = false; 
									}
		
									@Override
									public void onStart() {
										//Start timeout 
									}
		
								});
							} else if (nextPresentation != null) {
								setCurrentPresentation(nextPresentation);
							}
						}
					}
					
					//New presentation might not be loaded..
					long sleepTime = getNextPresentationTime();
					if (sleepTime == 0 && mIsPendingPresentation) {
						sleepTime = DEFAULT_TIME;
					}
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					Log.v(TAG, "Thread interrupted"); 
				}				
			}
			
		}	
		
		public void setRunning(boolean aRunning) {
			mIsRunning = aRunning;
		}		
	}
}
