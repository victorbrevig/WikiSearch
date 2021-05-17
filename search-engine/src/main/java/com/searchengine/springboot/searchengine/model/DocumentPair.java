package com.searchengine.springboot.searchengine.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class DocumentPair {

	String documentTitle;
	int position;
	
	DocumentPair(String d, int p) {
		documentTitle = d;
		position = p;
	}
	
	@Override
	public boolean equals(Object o) {
		// Two DocumentPair's are equal if their documentTitle and position are equal
		
		if (o == this) {
			return true;
		}
		if (!(o instanceof DocumentPair)) {
			return false;
		}
		
		DocumentPair docPair = (DocumentPair) o;

		return documentTitle.equals(docPair.documentTitle) && (position == docPair.position);
		
	}
	
	
    @Override
    public int hashCode() {
    	// IDEA: https://medium.com/codelog/overriding-hashcode-method-effective-java-notes-723c1fedf51c
        int result = 17;
        result = 31 * result + documentTitle.hashCode();
        result = 31 * result + position;
        return result;
    }
    
    
    
    	

   	public LinkedList<Integer> nearOp(int r, LinkedList<Integer> left, LinkedList<Integer> right) {
   		int leftSize = left.size;
   		int rightSize = right.size;
   		LinkedListNode<Integer> current;
   		if(leftSize < rightSize) {
   			// Choose right NEAR(r) left
   			current = left.head;
   			while(current != null) {
   				
   				
   				current = current.next;
   			}
   			
   			
   			
   		}
   		else {
   			// Choose left NEAR(r) right
   		}
   		
   		return null;
   		
   	}
    
    
    public static void main(String[] args) {

    	Index5 index = new Index5("C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\Wiki_100KB.txt");
    	// if _ is the first word, the position gets shifted to the right
    	//HashSet<DocumentPair> results = phraseSearch2(index, "Since the _ from");
    	/*
		for(DocumentPair p : results) {
    		System.out.println("Document Title: " + p.documentTitle + "     Position: " + p.position);
    	}
    	*/
    	
    	
    	
    }
    
    
}
