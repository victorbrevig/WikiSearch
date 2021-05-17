package com.searchengine.springboot.searchengine.model;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

public class WordListPair implements Comparable<WordListPair> {

	int index;
	WordLinkedListDocCount<HashSet<DocumentPair>> wordList;
	
	WordListPair(int i, WordLinkedListDocCount<HashSet<DocumentPair>> w) {
		index = i;
		wordList = w;
	}
	
	// So we can sort on docCount (used in phrase search)
	@Override
	public int compareTo(WordListPair p) {
		return Integer.compare(wordList.docCount, p.wordList.docCount);
	}
	
	
}
