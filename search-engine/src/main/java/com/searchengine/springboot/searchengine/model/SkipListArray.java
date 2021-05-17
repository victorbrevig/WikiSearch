package com.searchengine.springboot.searchengine.model;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

public class SkipListArray<T extends Comparable<T>> {
	// Decreasing order skip list
	
	
	LinkedList<ArrayListC<DataArrayListPair<T>>> list;
	
	int levelHeight;
	private double probability;
	private Random rand = new Random();
	int size;
	
	
	
	SkipListArray(double probability) {
		list = new LinkedList<ArrayListC<DataArrayListPair<T>>>();
		//start = new SkipListNode<T>(null);
		//startList = new ArrayList<SkipListNode<T>>();
		levelHeight = 0;
		this.probability = probability;
		size = 0;
		// Seed for testing
		rand.setSeed(1);
		ArrayListC<DataArrayListPair<T>> firstColumn = new ArrayListC<DataArrayListPair<T>>();
		firstColumn.add(new DataArrayListPair<T>(null,null));
		
		list.insertFront(firstColumn);
	}
	
	
	
	public void insertFront(T value) {
		// Requires value is greater than the current maximum value
		int nodeLevels = 0;
		ArrayListC<DataArrayListPair<T>> newNode = new ArrayListC<DataArrayListPair<T>>();
		newNode.add(new DataArrayListPair<T>(value,null));
		while(rand.nextDouble() >= probability) {
			newNode.add(new DataArrayListPair<T>(value,null));
			nodeLevels++;
		}

		// Create (nodeLevels-levelHeight) fresh start nodes
		while(nodeLevels > levelHeight) {
			list.head.data.add(new DataArrayListPair<T>(null,newNode));
			levelHeight++;
		}
		// Handle pointers all the way up
		for(int i = 0; i < nodeLevels; i++) {
			if(i > levelHeight) {
				list.head.data.add(new DataArrayListPair<T>(null,newNode));
				levelHeight++;
			}
			else {
				newNode.get(i).next = list.head.data.get(i).next;
				list.head.data.get(i).next = newNode;
			}
		}
		size++;
	}
	
	public void insert(T value) {
		int nodeLevels = 0;
		int oldLevelHeight = levelHeight;
		ArrayListC<DataArrayListPair<T>> newNode = new ArrayListC<DataArrayListPair<T>>();
		newNode.add(new DataArrayListPair<T>(value,null));
		while(rand.nextDouble() >= probability) {
			newNode.add(new DataArrayListPair<T>(value,null));
			nodeLevels++;
		}
		//System.out.println(value + " inserted with lvl: " + nodeLevels);
		// Create (nodeLevels-levelHeight) fresh start nodes
		while(nodeLevels > levelHeight) {
			list.head.data.add(new DataArrayListPair<T>(null,newNode));
			levelHeight++;
		}

		ArrayListC<DataArrayListPair<T>> currentArray = list.head.data;
		int currentLevel = oldLevelHeight;

		// Go left every turn
		while(true) {
			// becomes null to the right
			DataArrayListPair<T> currentPos = currentArray.get(currentLevel);
			
			// Gone to the end or too far to the right (short circuit OR)
			if(currentPos.next == null || currentPos.next.get(currentLevel).data.compareTo(value) < 0) {
				// GOING DOWN
				if(currentLevel != 0 && currentLevel <= nodeLevels) {
					
					newNode.get(currentLevel).next = currentPos.next;
					
					currentArray.get(currentLevel).next = newNode;
					
				}
				// Reach the right position in L0, insert
				if(currentLevel == 0) {
					newNode.get(0).next = currentPos.next;
					currentArray.get(0).next = newNode;
					size++;
					return;
				}
				currentLevel--;
				continue;
			}
			currentArray = currentArray.get(currentLevel).next;
		}
	}
	
	public ArrayListC<DataArrayListPair<T>> search(T value) {
		ArrayListC<DataArrayListPair<T>> currentArray = list.head.data;
		int currentLevel = levelHeight;
		// Go left every turn
		while(true) {
			DataArrayListPair<T> currentPos = currentArray.get(currentLevel);
			// If value is found at any level, go straight down to L0. (short circuit AND)
			if(currentPos.data != null && currentPos.data.compareTo(value) == 0) {
				return currentArray;
			}
			// Gone to the end or too far to the right (short circuit OR)
			if(currentPos.next == null || currentPos.next.get(currentLevel).data.compareTo(value) < 0) {
				// If we have gone too far on L0, the search is unsuccessful
				if(currentLevel == 0) {
					return null;
				}
				currentLevel--;
				continue;
			}
			currentArray = currentArray.get(currentLevel).next;
		}
	}
	
	
	
	public LinkedList<T> intersection(SkipListArray<T> l2 ) {
		LinkedList<T> res = new LinkedList<T>();
		
		ArrayListC<DataArrayListPair<T>> cA1 = this.list.head.data;
		ArrayListC<DataArrayListPair<T>> cA2 = l2.list.head.data;
		
		cA1 = cA1.get(0).next;
		cA2 = cA2.get(0).next;
		
		DataArrayListPair<T> c1 = cA1.get(cA1.size()-1);
		DataArrayListPair<T> c2 = cA2.get(cA2.size()-1);
		
		int testCount = 0;
		
		while(c1 != null && c2 != null) {
			testCount++;
			System.out.println(testCount);
			System.out.println("c1 data: " + c1.data);
			System.out.println("c2 data: " + c2.data);
			if(c1.data.compareTo(c2.data) == 0) {
				//System.out.println("c1 == c2");
				res.insertBack(c1.data);
				// Currently if match, just move 1 forward on both (thats why get(0))
				cA1 = cA1.get(0).next;
				cA2 = cA2.get(0).next;
				if(cA1 == null || cA2 == null) {
					break;
				}
				c1 = cA1.get(cA1.size()-1);
				c2 = cA2.get(cA2.size()-1);
			}
			else if(c1.data.compareTo(c2.data) > 0) {
				//System.out.println("c1 > c2");
				int currentIndex = cA1.size()-1;
				// Move down array until next is larger than c2
				while(currentIndex > 0 && (c1.next == null || c1.next.get(0).data.compareTo(c2.data) < 0)) {
					currentIndex--;
					c1 = cA1.get(currentIndex);
				}
				cA1 = c1.next;
				if(cA1 == null) {
					break;
				}
				c1 = cA1.get(cA1.size()-1);
			}
			else {
				//System.out.println("c1 < c2");
				int currentIndex = cA2.size()-1;
				// Move down array until next is larger than c1
				while(currentIndex > 0 && (c2.next == null || c2.next.get(0).data.compareTo(c1.data) < 0)) {
					currentIndex--;
					c2 = cA2.get(currentIndex);
				}
				cA2 = c2.next;
				if(cA2 == null) {
					break;
				}
				c2 = cA2.get(cA2.size()-1);
			}
		}
		
		return res;
	}
	
	
	public void printElements() {
		ArrayListC<DataArrayListPair<T>> current = list.head.data.get(0).next;
		while(current.get(0).next != null) {
			System.out.println(current.get(0).data);
			current = current.get(0).next;
		}
	}
	
	
	
	public static void main(String[] args) {
		
		SkipListArray<Integer> SL = new SkipListArray<Integer>(0.5);
		SkipListArray<Integer> SL2 = new SkipListArray<Integer>(0.5);

		LinkedList<Integer> ll1 = new LinkedList<Integer>();
		LinkedList<Integer> ll2 = new LinkedList<Integer>();
		
		Random rand = new Random();
		rand.setSeed(1);
		
		for(int i = 0; i < 1000; i++) {
			if(rand.nextDouble() >= 0.9) {
				SL.insertFront(i);
				ll1.insertFront(i);
			}
		}
		for(int i = 0; i < 1000; i++) {
			if(rand.nextDouble() >= 0.9) {
				SL2.insertFront(i);
				ll2.insertFront(i);
			}
		}
		
		System.out.println("Length SL&LL1: " + ll1.size);
		System.out.println("Length SL2&LL2: " + ll2.size);
		
		/*
		ArrayListC<DataArrayListPair<Integer>> current = SL.list.head.data;
		while(current.get(0).next != null) {
			//System.out.println(current.get(0).data);
			if(current.get(0).data != null) {
				ll1.insertFront(current.get(0).data);
			}
			current = current.get(0).next;
			
		}
		//System.out.println(current.get(0).data);
		ll1.insertFront(current.get(0).data);
		*/
		SL2.printElements();
		//ll2.printElements();
		
		StopWatch watch = new StopWatch();
		watch.start();
		//LinkedList<Integer> res = SL.intersection(SL2);
		//res.printElements();
		watch.stop();
		System.out.println("SL: " + watch.getTime(TimeUnit.NANOSECONDS));
		watch.reset();
		
		watch.start();
		//LinkedList<Integer> res2 = ll1.intersection(ll2);
		watch.stop();
		System.out.println("LL: " + watch.getTime(TimeUnit.NANOSECONDS));
		watch.reset();
		

		
	}
	
}
	
