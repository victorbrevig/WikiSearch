package com.searchengine.springboot.searchengine.model;

public class DocLinkedList {
	
	long docID;
	DocLinkedList next;
	
	DocLinkedList(long id, DocLinkedList n) {
		docID = id;
		next = n;
	}
	
	
}
