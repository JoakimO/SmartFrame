package se.newbie.smartframe.model.impl;

import se.newbie.smartframe.model.IPresentationProvider;

public abstract class AbstractPresentationProvider implements IPresentationProvider {

	private int mId = -1;
	
	@Override
	public int getId() {
		return mId;
	}
	
	@Override
	public void setId(int aId) {
		mId = aId;
	}
	
}
