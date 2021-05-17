package com.searchengine.springboot.searchengine.model;

public class WordHashTableDocCount<T> {
	// Array of size m with pointers (initially NULL). Linked list with the word (as string) and WikiItem pointer
	int m;
	int n;
	double loadFactorThreshold = 0.75;
	// Make private
	WordLinkedListDocCount<T>[] table;

	@SuppressWarnings("unchecked")
	public WordHashTableDocCount(Class<WordLinkedListDocCount<T>> class1) {
		m = 1;
		n = 0;
		this.table = (WordLinkedListDocCount<T>[]) java.lang.reflect.Array.newInstance(class1, m);
	}
	
	public WordLinkedListDocCount<T> search(String key) {
		// Input: search word (key)
		// Output: LinkedList item with the search word (if it exists - otherwise Null)
		int hashedKey = Math.floorMod(key.hashCode(), m);
		if(table[hashedKey] == null) {
			return null;
		}
		else {
			return table[hashedKey].search(key);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void insert(WordLinkedListDocCount<T> item) {
		// USED AFTER SEARCH! (IT DOES NOT CHECK FOR DUPLICATES)
		n++;
		
		// Check if load factor exceeds 0.75
		if((double) n / m >= loadFactorThreshold) {
			// Double size
			m *= 2;
			WordLinkedListDocCount<T>[] newTable = (WordLinkedListDocCount<T>[]) java.lang.reflect.Array.newInstance(WordLinkedListDocCount.class, m);
			// Rehash and reinsert
			for(WordLinkedListDocCount<T> l : table) {
				WordLinkedListDocCount<T> current = l;
				while(current != null) {
					// Rehash with new m
					int hashedKey = Math.floorMod(current.word.hashCode(), m);
					// Insert into new table
					WordLinkedListDocCount<T> temp = newTable[hashedKey];
					newTable[hashedKey] = current;
					current = current.next;
					newTable[hashedKey].next = temp;
				}
				
			}
			table = newTable;
		}
		
		// Insert at slot, word is key
		int hashedKey = Math.floorMod(item.word.hashCode(), m);
		item.next = table[hashedKey];
		table[hashedKey] = item;

	}
	
}