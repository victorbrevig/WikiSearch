package com.searchengine.springboot.searchengine.model;

import java.util.ArrayList;

public class ArrayListC<T> extends ArrayList<T> implements Comparable<ArrayListC<T>> {
	
	ArrayListC() {
		super();
	}

	@Override
	public int compareTo(ArrayListC<T> o) {
		return ((Integer) this.size()).compareTo(o.size());
	}
	
	
	
}
