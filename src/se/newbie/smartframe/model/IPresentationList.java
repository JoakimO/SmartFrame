package se.newbie.smartframe.model;

public interface IPresentationList<T> {
	
	public T remove();
	
	public T peek();
	
	public void add(T aObject);
	
	public boolean isEmpty();
	
	public int size();
}
