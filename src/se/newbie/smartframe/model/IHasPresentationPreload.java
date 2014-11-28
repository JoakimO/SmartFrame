package se.newbie.smartframe.model;

public interface IHasPresentationPreload {
	public interface IPreloadCallback {
		public void onStart();
		public void onComplete(IPresentation aPresentation);
		public void onError(Throwable aThrowable);
	}
	
	public void preload(final IPreloadCallback aPreloadCallback);
	
	public void interruptPreload();
}
