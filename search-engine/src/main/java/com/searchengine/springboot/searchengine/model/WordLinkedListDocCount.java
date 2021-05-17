package com.searchengine.springboot.searchengine.model;
// SINGLY LINKED LIST
public class WordLinkedListDocCount<T> {
	
	String word;
	// Should be generic, so it can be WikiItem or HashSet
	T documents;
	int docCount;
	WordLinkedListDocCount<T> next;
	
	WordLinkedListDocCount(String w, T d, int startCount, WordLinkedListDocCount<T> n) {
		word = w;
		documents = d;
		next = n;
		docCount = startCount;
	}
	
	
	public WordLinkedListDocCount<T> insert(WordLinkedListDocCount<T> x) {
		// Insert linked list element at the beginning
		x.next = this;
		return x;

	}
	
	public WordLinkedListDocCount<T> search(String word) {
		WordLinkedListDocCount<T> current = this;
		while(current != null) {
			if(current.word.equals(word)) {
				return current;
			}
			current = current.next;
		}
		return null;
	}
	
}
