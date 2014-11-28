package se.newbie.smartframe.model.impl;

import java.util.LinkedList;

import se.newbie.smartframe.model.IPresentationList;

public class PresentationListImpl<T> implements IPresentationList<T>{

	private LinkedList<T> mList = null;
	
	
	public PresentationListImpl() {
		mList = new LinkedList<T>();
	}
	
	@Override
	public T remove() {
		return mList.remove();
	}

	@Override
	public T peek() {
		return mList.peek();
	}

	@Override
	public void add(T aObject) {
		mList.add(aObject);
	}

	@Override
	public boolean isEmpty() {
		return mList.isEmpty();
	}
	
	@Override
	public int size() {
		return mList.size();
	}
}
