package com.searchengine.springboot.searchengine.model;

public class YarrayResult {

	Integer[] Yarray;
	int startPos;
	int endPos;
	
	YarrayResult(Integer[] Yarray, int startPos, int endPos) {
		this.Yarray = Yarray;
		this.startPos = startPos;
		this.endPos = endPos;
	}
	
	
}
