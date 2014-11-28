package se.newbie.smartframe.model;

public interface IPresentationProvider {
	public int getId();
	
	public void setId(int aId);
	
	public String getName();
	
	public void onResume(IPresentationProviderState aState);
	
	public void onPause();
}
