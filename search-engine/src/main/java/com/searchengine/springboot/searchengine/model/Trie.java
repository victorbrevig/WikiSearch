package com.searchengine.springboot.searchengine.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Trie {

	public TrieNode root;
	HashTable<Integer,String> idToString;
	PriorityQueue<KeyValuePair<String,Integer>> minHeap;

	
	Trie(HashTable<Integer,String> idToString) {
		root = null;

		this.idToString = idToString;
		minHeap = null;
	}
	
	
	public void insert(String s, int rank, int strId) {
		if(root == null) {
			root = new TrieNode(rank);
			root.leftId = strId;
			root.rightId = strId;
			TrieNode newNode = new TrieNode(rank);
			TrieEdgeData edge = new TrieEdgeData(strId,0, s.length()-1, newNode);
			newNode.str = s;
			newNode.leftId = strId;
			newNode.rightId = strId;
			root.table.insert(s.charAt(0), edge);
			

		}
		else {
			insertR(s, root, 0, rank, strId);

		}
	}
	
	private void insertR(String s, TrieNode node, int strIndex, int rank, int strId) {
		// Assumes string has not already been inserted!
		
		if(strIndex == s.length()) {
			return;
		}
		
		Character firstChar = s.charAt(strIndex);
	
		TrieEdgeData edge = node.table.search(firstChar);
		
		
		
		if(edge != null) {

			// either char is alone or a part of a longer string
			String edgeString = idToString.search(edge.stringId) + ' ';
			int stringPos = strIndex;

			while(stringPos <= edge.endPos && ((Character) edgeString.charAt(stringPos)).equals(s.charAt(stringPos))) {
				stringPos++;
			}
			stringPos--;
			
			if(stringPos < edge.endPos) {
				// Need to split
				// Create split node and edge to it
				int middleNodeMaxChildRank = (edge.node.maxChildRank > rank) ? edge.node.maxChildRank : rank;
				
				TrieNode middleNode = new TrieNode(middleNodeMaxChildRank);
				// update parent id range

				int compareLeftId = s.compareTo(idToString.search(node.leftId) + ' ');
				int compareRightId = s.compareTo(idToString.search(node.rightId) + ' ');
				
				if(compareLeftId < 0) {
					node.leftId = strId;
					middleNode.leftId = strId;
					middleNode.rightId = edge.node.rightId;
				}
				else if(compareRightId > 0) {
					node.rightId = strId;
					middleNode.rightId = strId;
					middleNode.leftId = edge.node.leftId;
				}
				else {
					middleNode.leftId = s.compareTo(idToString.search(edge.node.leftId) + ' ') < 0 ? strId : edge.node.leftId;
					middleNode.rightId = s.compareTo(idToString.search(edge.node.rightId) + ' ') > 0 ? strId : edge.node.rightId;
				}
				
				
				
				TrieEdgeData edgeToMiddleNode = new TrieEdgeData(strId, strIndex, stringPos, middleNode);
				node.table.updateValue(firstChar,edgeToMiddleNode);

				// Change starting position of old edge
				edge.startPos = stringPos+1;
				
				// Create node to continue on
				TrieNode contNode = new TrieNode(rank);
				node.str = null;
				contNode.str = s;
				contNode.leftId = strId;
				contNode.rightId = strId;
				
				TrieEdgeData contEdge = new TrieEdgeData(strId, stringPos+1, s.length()-1, contNode);
				
				middleNode.table.insert((idToString.search(edge.stringId) + ' ').charAt(stringPos+1),edge);
				middleNode.table.insert((idToString.search(strId) + ' ').charAt(stringPos+1), contEdge);
				
				
				if(rank > node.maxChildRank) {
					node.maxChildRank = rank;
				}
				return;
			}
			else {
				// Potentially update maxChildRank and left & right Id
				if(rank > node.maxChildRank) {
					node.maxChildRank = rank;
				}
				
				
				// update parent id range
				int compareLeftId = s.compareTo(idToString.search(node.leftId) + ' ');
				int compareRightId = s.compareTo(idToString.search(node.rightId) + ' ');
				
				if(compareLeftId < 0) {
					node.leftId = strId;
				}
				else if(compareRightId > 0) {
					node.rightId = strId;
				}
				
				insertR(s, edge.node, stringPos+1, rank, strId);
				
			}
			
		}
		else {
			// path does not exists
			// Add to existing without splitting
			TrieNode newNode = new TrieNode(rank);
			newNode.leftId = strId;
			newNode.rightId = strId;
			newNode.str = s;
			
			TrieEdgeData newEdge = new TrieEdgeData(strId, strIndex, s.length()-1, newNode);
			node.table.insert(firstChar, newEdge);
			if(rank > node.maxChildRank) {
				node.maxChildRank = rank;
			}
			
			// update parent id range
			int compareLeftId = s.compareTo(idToString.search(node.leftId) + ' ');
			int compareRightId = s.compareTo(idToString.search(node.rightId) + ' ');
			
			if(compareLeftId < 0) {
				node.leftId = strId;
			}
			else if(compareRightId > 0) {
				node.rightId = strId;
			}
			
			return;
		}
	}
	
	
	public HashTable<String,String> prefixFind(String s) {
		HashTable<String,String> res = new HashTable(LinkedList.class);
		
		TrieNode node = search(s, root);
		
		if(node != null) {
			// When node is found, collect strings of all leaves
			// Then insert all in a hash table and return
			LinkedList<KeyValuePair<String,Integer>> strings = collectStrings(node);
			LinkedListNode<KeyValuePair<String,Integer>> current = strings.head;
			while(current != null) {
				res.insert(current.data.key, current.data.key);
				current = current.next;
			}
		}
		return res;
	}
	
	public List<KeyValuePair<String,Integer>> prefixFindList(String s) {
		List<KeyValuePair<String,Integer>> res = new ArrayList<KeyValuePair<String,Integer>>();
		
		TrieNode node = search(s, root);
		
		if(node != null) {
			// When node is found, collect strings of all leaves
			// Then insert all in a hash table and return
			LinkedList<KeyValuePair<String,Integer>> strings = collectStrings(node);
			LinkedListNode<KeyValuePair<String,Integer>> current = strings.head;
			while(current != null) {
				res.add(new KeyValuePair<String,Integer>(current.data.key, current.data.value));
				current = current.next;
			}
		}
		return res;
	}
	
	
	
	public TrieNode search(String s, TrieNode current) {
		
		if(s.equals("")) {
			return current;
		}
		
		Character firstChar = s.charAt(0);
		
		TrieEdgeData edge = current.table.search(firstChar);
		
		if(edge == null) {
			// If no path forward, string does not exist. Return null
			return null;
		}
		else {
			// Check how many characters matches with edge string
			String edgeString = (idToString.search(edge.stringId) + ' ').substring(edge.startPos, edge.endPos+1);

			int stringIndex = 0;
			while(stringIndex < edgeString.length()) {
				if(stringIndex == s.length()) {
					return search(s.substring(stringIndex), edge.node);
				}
				else if(!((Character) s.charAt(stringIndex)).equals(edgeString.charAt(stringIndex))) {
					return null;
				}
				stringIndex++;
			}
			return search(s.substring(stringIndex), edge.node);
		}
		
	}
	
	public LinkedList<KeyValuePair<String,Integer>> collectStrings(TrieNode node) {
		// Base case - node is a leaf if str is not null
		//System.out.println("Node str: " + node.str + "     MaxRank: " + node.maxChildRank);
		if(node.str != null) {
			LinkedList<KeyValuePair<String,Integer>> list = new LinkedList<KeyValuePair<String,Integer>>();
			list.insertBack(new KeyValuePair(node.str, node.maxChildRank));
			return list;
		}
		LinkedList<KeyValuePair<String,Integer>> list = new LinkedList<KeyValuePair<String,Integer>>();
		for(KeyValuePair<Character, TrieEdgeData> kvp : node.table) {
			TrieEdgeData edge = kvp.value;
			
			list.concatListBack(collectStrings(edge.node));
			
		}
		return list;
	}
	
	public void collectStringsPruning(TrieNode node, int k) {
		// Base case - node is a leaf if str is not null
		if(node.str != null) {
			// If heap is full, delete smallest element and add new
			if(minHeap.size() >= k) {
				minHeap.remove();
			}
			minHeap.add(new KeyValuePair<String,Integer>(node.str,node.maxChildRank));
			return;
		}

		for(KeyValuePair<Character, TrieEdgeData> kvp : node.table) {
			TrieEdgeData edge = kvp.value;
			
			// Only walk down path if there exists a string with higher rank than already seen or if there is still space for more elements
			boolean notFullHeap = minHeap.size() < k;
			if(notFullHeap || edge.node.maxChildRank > minHeap.peek().value) {
				collectStringsPruning(edge.node, k);
			}
			
		}
	}
	
	public List<String> prefixTopK(String s, int k) {
		minHeap = new PriorityQueue<>();
		List<String> res = new ArrayList<String>();

		TrieNode node = search(s, root);
		
		if(node != null) {
			// collectStringsPruning changes the state of minHeap
			collectStringsPruning(node,k);
			
			// Heap sort on ranking
			while(minHeap.size() != 0) {
				res.add(minHeap.remove().key);
			}
		}
		minHeap = null;
		Collections.reverse(res);
		return res;
	}
	
	public Pair<Integer,Integer> getRange(String s) {
		
		TrieNode splitNode = search(s,root);
		
		return new Pair<Integer,Integer>(splitNode.leftId,splitNode.rightId);
	}
	
	public static void main(String[] args) {
		
		HashTable<Integer, String> idToWord = new HashTable(LinkedList.class);
		idToWord.insert(1, "dark");
		idToWord.insert(2, "sad");
		idToWord.insert(3, "duck");
		idToWord.insert(4, "red");
		idToWord.insert(5, "dust");
		idToWord.insert(6, "day");
		idToWord.insert(7, "say");
		idToWord.insert(8, "sun");
		idToWord.insert(9, "such");
		idToWord.insert(10, "rent");
		idToWord.insert(11, "so");
		idToWord.insert(12, "an");
		idToWord.insert(13, "an-");
		
		Trie t = new Trie(idToWord);
		/*
		t.insert("so$",78, 11);
		t.insert("dark$",3, 1);
		t.insert("duck$",55, 3);
		t.insert("dust$",26, 5);
		t.insert("rent$",9, 10);
		t.insert("sad$",18, 2);
		t.insert("sun$",33, 8);
		t.insert("red$",44, 4);
		t.insert("day$",17, 6);
		t.insert("say$",6, 7);
		t.insert("such$",29, 9);
		*/
		t.insert("an$", 88, 12);
		t.insert("an-$", 122, 13);
		
		
		Pair<Integer,Integer> res = t.getRange("a");
		

		System.out.println(res.first);
		System.out.println(res.second);

		

		//LinkedList<String> res = t.collectStrings(t.root);
		//res.printElements();
		
		/*
		HashTable<String,String> resT = t.prefixFind("d");
		System.out.println(resT.n);
		resT.printElements();
		*/
		/*
		List<String> res = t.prefixTopK("s",4);
		for(String s : res) {
			System.out.println(s);
		}
		*/
		
		
		/*
		System.out.println(t.root.leftId);
		System.out.println(t.root.rightId);
		*/
	}
	
	
}



class TrieNode {
	HashTable<Character, TrieEdgeData> table;
	int maxChildRank;
	String str;
	
	int leftId;
	int rightId;
	
	TrieNode(int maxChildRank) {
		this.table = new HashTable(LinkedList.class);
		this.maxChildRank = maxChildRank;
	}
}

class TrieEdgeData implements Comparable<TrieEdgeData>{
	
	int stringId;
	int startPos;
	int endPos;
	
	TrieNode node;
	
	TrieEdgeData(int stringId, int startPos, int endPos, TrieNode node) {
		this.stringId = stringId;
		this.startPos = startPos;
		this.endPos = endPos;
		this.node = node;
	}

	@Override
	public int compareTo(TrieEdgeData t) {
		// can change to something meaningful if necessary
		return ((Integer) stringId).compareTo((Integer) t.stringId);
	}
	
}