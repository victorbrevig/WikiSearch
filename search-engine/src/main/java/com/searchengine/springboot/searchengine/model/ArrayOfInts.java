package com.searchengine.springboot.searchengine.model;

import java.util.ArrayList;

public class ArrayOfInts extends ArrayList<Integer> implements Comparable<ArrayOfInts> {

	@Override
	public int compareTo(ArrayOfInts o) {
		return 0;
	}

	
	
}
