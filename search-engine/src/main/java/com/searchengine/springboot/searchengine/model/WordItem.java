package com.searchengine.springboot.searchengine.model;

import java.util.HashSet;

public class WordItem implements Comparable<WordItem> {

	HashSet<DocumentPair> documents;
	int frequency;
	
	WordItem() {
		frequency = 0;
		documents = new HashSet<DocumentPair>();
	}
	
	
	// So we can sort on frequency (used in phrase search)
	@Override
	public int compareTo(WordItem w) {
		return Integer.compare(frequency, w.frequency);
	}
	
}


