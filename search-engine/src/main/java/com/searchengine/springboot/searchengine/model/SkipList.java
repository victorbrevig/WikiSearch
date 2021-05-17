package com.searchengine.springboot.searchengine.model;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

public class SkipList<T extends Comparable<? super T>> {
	// Decreasing order skip list
	SkipListNode<T> start;
	int levelHeight;
	private double probability;
	private Random rand = new Random();
	int size;
	ArrayList<SkipListNode<T>> startList;
	
	SkipList(double probability) {
		start = new SkipListNode<T>(null);
		startList = new ArrayList<SkipListNode<T>>();
		levelHeight = 0;
		this.probability = probability;
		size = 0;
		// Seed for testing
		rand.setSeed(1);
		startList.add(start);
	}
	
	
	
	public void insertFront(T value) {
		// Requires value is greater than the current maximum value
		int nodeLevels = 0;
		while(rand.nextDouble() >= probability) {
			nodeLevels++;
		}

		// Create (nodeLevels-levelHeight) fresh start nodes
		while(nodeLevels > levelHeight) {
			SkipListNode<T> newStartNode = new SkipListNode<T>(null);
			newStartNode.down = start;
			start = newStartNode;
			startList.add(start);
			levelHeight++;
		}

		// Base layer
		SkipListNode<T> prevNode = new SkipListNode<T>(value);
		prevNode.next = startList.get(0).next;
		startList.get(0).next = prevNode;

		for(int i = 1; i <= nodeLevels; i++) {
			SkipListNode<T> newNode = new SkipListNode<T>(value);
			newNode.next = startList.get(i).next;
			newNode.down = prevNode;
			startList.get(i).next = newNode;
			prevNode = newNode;
		}
	}
	
	public void insert(T value) {
		int nodeLevels = 0;
		while(rand.nextDouble() >= probability) {
			nodeLevels++;
		}

		SkipListNode<T> insertNodeCurrent = new SkipListNode<T>(value);
		SkipListNode<T> insertNodeNext = new SkipListNode<T>(value); 
		
		// Create (nodeLevels-levelHeight) fresh start nodes
		while(nodeLevels > levelHeight) {
			SkipListNode<T> newStartNode = new SkipListNode<T>(null);
			newStartNode.down = start;
			start = newStartNode;
			startList.add(start);
			levelHeight++;
		}
		
		SkipListNode<T> current = start;
		int currentLevel = levelHeight;
		// Go left every turn
		while(true) {			
			// Gone to the end or too far to the right (short circuit OR)
			if(current.next == null || current.next.value.compareTo(value) < 0) {
				if(currentLevel != 0 && currentLevel <= nodeLevels) {
					insertNodeCurrent.down = insertNodeNext;
					insertNodeCurrent.next = current.next;
					current.next = insertNodeCurrent;
					
					insertNodeCurrent = insertNodeNext;
					insertNodeNext = new SkipListNode<T>(value);
				}
				// Reach the right position in L0, insert
				if(currentLevel == 0) {
					insertNodeCurrent.next = current.next;
					current.next = insertNodeCurrent;
					size++;
					return;
				}
				current = current.down;
				currentLevel--;
				continue;
			}
			
			current = current.next;
		}
	}
	
	public SkipListNode<T> search(T value) {
		SkipListNode<T> current = start;
		int currentLevel = levelHeight;
		// Go left every turn
		while(true) {
			// If value is found at any level, go straight down to L0. (short circuit AND)
			if(current.value != null && current.value.compareTo(value) == 0) {
				while(currentLevel != 0) {
					current = current.down;
					currentLevel--;
				}
				return current;
			}
			// Gone to the end or too far to the right (short circuit OR)
			if(current.next == null || current.next.value.compareTo(value) < 0) {
				// If we have gone too far on L0, the search is unsuccessful
				if(currentLevel == 0) {
					return null;
				}
				current = current.down;
				currentLevel--;
				continue;
			}
			
			current = current.next;
		}
	}
	
	
	
	public static void main(String[] args) {

		SkipList<String> SL = new SkipList<String>(0.5);

		SL.insert("And");
		//SL.insert("Monkey");
		//SL.insert("Cat");
		
		//SL.insertFront("Zebra");
		//SL.insert("Snake");
		//SL.insert("Ladybug");
		//SkipListNode<String> bottomStart = SL.startList.get(0);
		/*
		while(bottomStart != null) {
			System.out.println(bottomStart.value);
			bottomStart = bottomStart.next;
		}
		*/
	}
	
}


class SkipListNode<T extends Comparable<? super T>> {
	T value;
	SkipListNode<T> next;
	SkipListNode<T> down;
	
	SkipListNode(T value) {
		this.value = value;
	}
	
}
