package com.searchengine.springboot.searchengine.model;

import java.util.Iterator;

// T extends Comparable<T> ?
public class HashTable<G,T extends Comparable<T>> implements Comparable<HashTable<G,T>>, Iterable<KeyValuePair<G,T>>{
	// T is value type
	int m;
	public int n;
	double loadFactorThreshold = 0.75;
	// Make private
	LinkedList<KeyValuePair<G,T>>[] table;

	@SuppressWarnings("unchecked")
	public HashTable(Class<LinkedList<T>> class1) {
		m = 1;
		n = 0;
		this.table = (LinkedList<KeyValuePair<G,T>>[]) java.lang.reflect.Array.newInstance(class1, m);
	}
	
	public T search(G key) {

		int hashedKey = Math.floorMod(key.hashCode(), m);
		if(table[hashedKey] == null) {
			return null;
		}
		else {
			LinkedListNode<KeyValuePair<G,T>> listSearchResult = table[hashedKey].search(new KeyValuePair<G,T>(key,null));
			if(listSearchResult == null) {
				return null;
			}
			else {
				return listSearchResult.data.value;
			}
		}
	}
	

	public void insert(G key, T value) {
		// USED AFTER SEARCH! (IT DOES NOT CHECK FOR DUPLICATES)
		n++;
		// Check if load factor exceeds 0.75
		if((double) n / m >= loadFactorThreshold) {
			resizeDouble();
		}
		KeyValuePair<G,T> newPair = new KeyValuePair<G,T>(key,value);
		int hashedKey = Math.floorMod(key.hashCode(), m);
		table = tableInsert(newPair, hashedKey, table);
	}
	
	public void updateValue(G key, T newValue) {
		int hashedKey = Math.floorMod(key.hashCode(), m);
		LinkedListNode<KeyValuePair<G,T>> listSearchResult = table[hashedKey].search(new KeyValuePair<G,T>(key,null));
		listSearchResult.data.value = newValue;
	}


	private void resizeDouble() {
		m *=2;
		LinkedList<KeyValuePair<G,T>>[] newTable = (LinkedList<KeyValuePair<G,T>>[]) java.lang.reflect.Array.newInstance(LinkedList.class, m);
		
		// Rehash and reinsert
		for(LinkedList<KeyValuePair<G,T>> l : table) {
			if(l != null) {
				LinkedListNode<KeyValuePair<G,T>> current = l.head;
				while(current != null) {
					// Rehash with new m
					int hashedKey = Math.floorMod(current.data.key.hashCode(), m);
					// Insert into new table
					
					newTable = tableInsert(current.data, hashedKey, newTable);
					current = current.next;
				}
			}
		}
		table = newTable;
	}
	
	private LinkedList<KeyValuePair<G,T>>[] tableInsert(KeyValuePair<G, T> newPair, int hashedKey, LinkedList<KeyValuePair<G,T>>[] newTable) {
		if(newTable[hashedKey] == null) {
			LinkedList<KeyValuePair<G,T>> newList = new LinkedList<KeyValuePair<G,T>>();
			newList.insertFront(newPair);
			newTable[hashedKey] = newList;
		}
		else {
			newTable[hashedKey].insertFront(newPair);
		}
		return newTable;
	}
	
	public void printElements() {
		for(KeyValuePair<G,T> kvp : this) {
			System.out.println("Key: " + kvp.key + "     Value: " + kvp.value);
		}
	}
	
	public HashTable<G,T> intersection(HashTable<G,T> t2) {
		HashTable<G,T> res = new HashTable(LinkedList.class);
		
		for(KeyValuePair<G,T> kvp : this) {
			if(t2.search(kvp.key) != null) {
				res.insert(kvp.key, kvp.value);
			}
		}
		return res;
	}
	
	public HashTable<G,T> union(HashTable<G,T> t2) {
		HashTable<G,T> res = this;
		
		for(KeyValuePair<G,T> kvp : t2) {
			if(this.search(kvp.key) == null) {
				res.insert(kvp.key, kvp.value);
			}
		}
		return res;
	}
	
	public HashTable<G,T> difference(HashTable<G,T> t2) {
		HashTable<G,T> res = new HashTable(LinkedList.class);
		
		for(KeyValuePair<G,T> kvp : this) {
			if(t2.search(kvp.key) == null) {
				res.insert(kvp.key, kvp.value);
			}
		}
		return res;
	}
	
	
	
	public void insertAll(HashTable<G,T> t2) {
		// Inserts all elements from t2 to this hash table 
		// Assumes disjoint sets of elements
		for(KeyValuePair<G,T> kvp : t2) {
			this.insert(kvp.key, kvp.value);
		}
	}
	
	@Override
	public int compareTo(HashTable<G, T> o) {
		return ((Integer) this.n).compareTo(o.n);
	}
	
	@Override
	public Iterator<KeyValuePair<G,T>> iterator()
    {
        return new HashTableIterator<G,T>(this);
    }	
	
	public static void main(String[] args) {
		HashTable<Integer,Integer> t = new HashTable(LinkedList.class);
		t.insert(42, 42);
		System.out.println(t.search(42));
		t.updateValue(42,13);
		System.out.println(t.search(42));
		
	}
	
	
}

class HashTableIterator<G,T extends Comparable<T>> implements Iterator<KeyValuePair<G,T>> {
	
	LinkedListNode<KeyValuePair<G,T>> current;
	LinkedList<KeyValuePair<G, T>> currentL;
	HashTable<G,T> table;
	int currentIndex;
	int count;
	
	public HashTableIterator(HashTable<G,T> table) {
		this.table = table;		
		currentIndex = 0;
		count = 0;
		currentL = null;
		current = null;
		
	}
	
	@Override
	public boolean hasNext() {
		return count < table.n;
	}

	@Override
	public KeyValuePair<G,T> next() {
		// Find first
		if(count == 0) {
			while(table.table[currentIndex] == null) {
				currentIndex++;
			}
			currentL = table.table[currentIndex];
			current = currentL.head;
		}
		else if(current.next == null) {
			currentIndex++;
			
			while(table.table[currentIndex] == null) {
				currentIndex++;
			}
			currentL = table.table[currentIndex];
			current = currentL.head;
		}
		else {
			current = current.next;
		}
		count++;
		return current.data;
		
	}
	
	
	@Override
	public void remove()
    {
        throw new UnsupportedOperationException();
    }
}


