package se.newbie.smartframe.model;

public interface IModel {
	
	public final static String ACTION_CLOSE = "se.newbie.smartframe.action.ACTION_CLOSE";
	public final static String ACTION_START = "se.newbie.smartframe.action.ACTION_START";	
	
	public interface IPresentationChangeListener {
		public void onPresentationChangeEvent(IModel aModel);
	}
	
	public interface IModelChangeListener {
		public void onModelChangeEvent(IModel aModel);
	}

	public void registerModelChangeListener(IModelChangeListener aListener);
	
	public void removeModelChangeListener(IModelChangeListener aListener);	
	
	public void registerPresentationChangeListener(IPresentationChangeListener aListener);
	
	public void removePresentationChangeListener(IPresentationChangeListener aListener);
	
	public void addPresentation(IPresentation aPresentation);
	
	public int getPresentationSize();
	
	public IPresentation getCurrentPresentation();
	
	public void onPause();
	
	public void onResume();
	
}
