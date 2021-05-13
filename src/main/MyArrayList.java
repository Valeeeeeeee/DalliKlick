package main;

import java.util.ArrayList;

public class MyArrayList<E> extends ArrayList<E> {

	private static final long serialVersionUID = 7942710125007852475L;
	
	public E get(int index) {
		return super.get((index + size()) % size());
	}
}
