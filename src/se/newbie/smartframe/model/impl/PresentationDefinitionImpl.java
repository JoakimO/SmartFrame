package se.newbie.smartframe.model.impl;

import se.newbie.smartframe.model.IPresentationDefinition;
import se.newbie.smartframe.model.PresentationType;

public class PresentationDefinitionImpl implements IPresentationDefinition {

	private long mPreferedVisibleTime;
	private long mMinimumVisibleTime;
	private PresentationType mPresentationType;

	public PresentationDefinitionImpl() {
		mPreferedVisibleTime = 0;
		mMinimumVisibleTime = 0;
		mPresentationType = PresentationType.ARCHIVE;
	}
	
	public PresentationDefinitionImpl(long aPreferedVisibleTime, long aMinimumVisibleTime, PresentationType aPresentationType) {
		mPreferedVisibleTime = aPreferedVisibleTime;	
		mMinimumVisibleTime = aMinimumVisibleTime;
		mPresentationType = aPresentationType;
	}
	
	
	public void setPreferedVisibleTime(long aPreferedVisibleTime) {
		mPreferedVisibleTime = aPreferedVisibleTime;
	}
	
	@Override
	public long getPreferedVisibleTime() {
		return mPreferedVisibleTime;
	}

	
	public void setMinimumVisibleTime(long aMinimumVisibleTime) {
		mMinimumVisibleTime = aMinimumVisibleTime;
	}
	
	@Override
	public long getMinimumVisibleTime() {
		return mMinimumVisibleTime;
	}
	
	public static PresentationDefinitionImpl createDefault() {
		PresentationDefinitionImpl definition = new PresentationDefinitionImpl(DEFAULT_PREFERED_VISIBLE_TIME, DEFAULT_MINUMUM_VISIBLE_TIME, PresentationType.ARCHIVE);
		return definition;
	}

	@Override
	public PresentationType getType() {
		return mPresentationType;
	}

}
