package com.searchengine.springboot.searchengine.model;

import java.util.HashSet;

public class WordTablePair implements Comparable<WordTablePair> {
	String word;
	HashTable<Integer,HashTable<Integer,Integer>> table;
	
	
	WordTablePair(String w, HashTable<Integer,HashTable<Integer,Integer>> t) {
		word = w;
		table = t;
	}
	
	// So we can sort on frequency (used in phrase search)
	@Override
	public int compareTo(WordTablePair p) {
		return Integer.compare(table.n, p.table.n);
	}
	
}
