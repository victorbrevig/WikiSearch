package com.searchengine.springboot.searchengine.model;

public class Pair<G,T> {
	public G first;
	public T second;
	
	Pair(G first, T second) {
		this.first = first;
		this.second = second;
	}
}