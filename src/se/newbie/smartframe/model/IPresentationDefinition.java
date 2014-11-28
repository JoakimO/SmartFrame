package se.newbie.smartframe.model;

public interface IPresentationDefinition {
	
	public static final long DEFAULT_PREFERED_VISIBLE_TIME = 20000l;
	public static final long DEFAULT_MINUMUM_VISIBLE_TIME = 10000l;
	
	public long getPreferedVisibleTime();
	
	public long getMinimumVisibleTime();
	
	public PresentationType getType();
}
