package com.searchengine.springboot.searchengine.model;

import java.util.Iterator;

// SINGLY LINKED LIST
public class LinkedList<T extends Comparable<T>> implements Comparable<LinkedList<T>>, Iterable<LinkedListNode<T>>{
	LinkedListNode<T> head;
	LinkedListNode<T> tail;
	public int size;
	
	
	public LinkedList() {
		head = null;
		tail = null;
		size = 0;
	}
	
	public LinkedList(LinkedListNode<T> head, LinkedListNode<T> tail, int size) {
		// Overloaded constructor, if head, tail and size is already known
		this.head = head;
		this.tail = tail;
		this.size = size;
	}
	
	public void insertFront(T data) {
		LinkedListNode<T> newNode = new LinkedListNode<T>(data, head);
		head = newNode;
		if(size == 0) {
			// if list is empty
			tail = head;
		}
		size++;
	}
	
	public void insertBack(T data) {
		LinkedListNode<T> newNode = new LinkedListNode<T>(data,null);
		if(size == 0) {
			// if list is empty
			head = newNode;
			tail = head;
		}
		else {
			tail.next = newNode;
			tail = tail.next;
		}
		size++;
	}
	
	
	public void concatListBack(LinkedList<T> l2) {
		
		if(size == 0) {
			// if list is empty - change completely to l2
			head = l2.head;
			tail = l2.tail;
			size = l2.size;
		}
		else {
			tail.next = l2.head;
			tail = l2.tail;
			size += l2.size;
		}

	}
	
	
	public LinkedListNode<T> search(T data) {
		LinkedListNode<T> current = head;
		while(current != null) {
			if(current.data.equals(data)) {
				return current;
			}
			current = current.next;
		}
		return null;
	}
	
	
	public void printElements() {
		LinkedListNode<T> current = head;
		while(current != null) {
			System.out.println(current.data);
			current = current.next;
		}
	}

	@Override
	public int compareTo(LinkedList<T> o) {
		return ((Integer) this.size).compareTo(o.size);
	}
	
	
	public LinkedList<T> union(LinkedList<T> l2) {
		// Only works for descending ordered linked lists
    	LinkedList<T> res = new LinkedList<T>();
    	
    	LinkedListNode<T> c1 = head;
    	LinkedListNode<T> c2 = l2.head;
    	int itemsLeftL1 = size;
    	int itemsLeftL2 = l2.size;
    	
    	while(c1 != null && c2 != null) {
    		if(c1.data.compareTo(c2.data) > 0) {
    			res.insertBack(c1.data);
    			c1 = c1.next;
    			itemsLeftL1--;
    		}
    		else if(c1.data.compareTo(c2.data) < 0) {
    			res.insertBack(c2.data);
    			c2 = c2.next;
    			itemsLeftL2--;
    		}
    		else {
    			res.insertBack(c1.data);
    			c1 = c1.next;
    			c2 = c2.next;
    			itemsLeftL1--;
    			itemsLeftL2--;
    		}
    	}
    	
    	if(c1 == null) {
    		LinkedList<T> toConcat = new LinkedList<T>(c2,l2.tail,itemsLeftL2);
    		res.concatListBack(toConcat);
    	}
    	else if(c2 == null) {
    		LinkedList<T> toConcat = new LinkedList<T>(c1,tail,itemsLeftL1);
    		res.concatListBack(toConcat);
    	}
    	
    	return res;
    }
	
    public LinkedList<T> intersection(LinkedList<T> l2) {
		// Only works for descending ordered linked lists with unique elements
    	LinkedList<T> res = new LinkedList<T>();
    	
    	LinkedListNode<T> c1 = head;
    	LinkedListNode<T> c2 = l2.head;
    	
    	while(c1 != null && c2 != null) {
    		if(c1.data.compareTo(c2.data) > 0) {
    			c1 = c1.next;
    		}
    		else if(c1.data.compareTo(c2.data) < 0) {
    			c2 = c2.next;
    		}
    		else {
    			res.insertBack(c1.data);
    			c1 = c1.next;
    			c2 = c2.next;
    		}
    	}
    	return res;
    }
    
    public LinkedList<T> difference(LinkedList<T> l2) {
    	// Only works for descending ordered linked lists
    	LinkedList<T> res = new LinkedList<T>();
    	
    	LinkedListNode<T> c1 = head;
    	LinkedListNode<T> c2 = l2.head;
    	int itemsLeftL1 = size;
    	
    	while(c1 != null && c2 != null) {
    		if(c1.data.compareTo(c2.data) > 0) {
    			res.insertBack(c1.data);
    			c1 = c1.next;
    			itemsLeftL1--;
    		}
    		else if(c1.data.compareTo(c2.data) < 0) {
    			c2 = c2.next;
    		}
    		else {
    			c1 = c1.next;
    			c2 = c2.next;
    			itemsLeftL1--;
    		}
    	}
    	if(c2 == null) {
    		LinkedList<T> toConcat = new LinkedList<T>(c1,tail,itemsLeftL1);
    		res.concatListBack(toConcat);
    	}
    	return res;
    }
    
    
    public static void main(String[] args) {
    	LinkedList<Integer> l1 = new LinkedList<Integer>();
		
		
		l1.insertFront(2);
		/*
		l1.insertFront(4);
		l1.insertFront(15);
		l1.insertFront(17);
		l1.insertFront(32);
		l1.insertFront(43);
		*/
		for(LinkedListNode<Integer> n : l1) {
			System.out.println(n.data);
		}
		
	}

	@Override
	public Iterator<LinkedListNode<T>> iterator() {
		return new LinkedListIterator<T>(this);
	}
	
}


class LinkedListNode<T extends Comparable<T>> implements Comparable<LinkedListNode<T>>{
	// Data must override equals() for search to be meaningful
	T data;
	LinkedListNode<T> next;
	LinkedListNode(T d, LinkedListNode<T> n) {
		data = d;
		next = n;
	}
	
	
	public int compareTo(LinkedListNode<T> o) {
		
		return data.compareTo(o.data);
	}
	
}

class LinkedListIterator<T extends Comparable<T>> implements Iterator<LinkedListNode<T>> {
	LinkedListNode<T> current;
	int count = 0;
	public LinkedListIterator(LinkedList<T> list) {
		current = list.head;
	}
	
	@Override
	public boolean hasNext() {
		return current.next != null || count == 0;
	}

	@Override
	public LinkedListNode<T> next() {
		if(count == 0) {
			count++;
			return current;
		}
		else {
			current = current.next;
			count++;
			return current;
		}
	}
	
}
