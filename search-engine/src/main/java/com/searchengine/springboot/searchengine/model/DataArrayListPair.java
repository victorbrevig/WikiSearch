package com.searchengine.springboot.searchengine.model;

public class DataArrayListPair<T> {
	
	T data;
	ArrayListC<DataArrayListPair<T>> next;
	
	DataArrayListPair(T data, ArrayListC<DataArrayListPair<T>> array) {
		this.data = data;
		this.next = array;
	}
	
}
