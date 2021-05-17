package com.searchengine.springboot.searchengine.model;

import java.util.Arrays;

class BinarySearchReverseLexicographical {
	
	HashTable<Integer, String> idToRevString;
	
	BinarySearchReverseLexicographical(HashTable<Integer, String> idToRevString) {
		this.idToRevString = idToRevString;
	}
	
	int binarySearchLeftBound(Integer[] A, String str, int left, int right) {
	     if (right >= left) {
	         int middle = left + (right - left) / 2;

	         int compareRes = idToRevString.search(A[middle]).compareTo(str);
	         if (compareRes == 0) {
	        	 return middle;
	         }
	         else if (compareRes > 0) {
	        	 return binarySearchLeftBound(A, str, left, middle - 1);
	         }
	         else {
	        	 return binarySearchLeftBound(A, str, middle + 1, right); 
	         }
	         
	         
	     }
	
	     
	     return left;
	 }
	
	int binarySearchRightBound(Integer[] A, String str, int left, int right) {
	     if (right >= left) {
	         int middle = left + (right - left) / 2;
	         
	         
	         int compareRes = idToRevString.search(A[middle]).compareTo(str);
	         
	         if (compareRes == 0) {
	        	 return middle;
	         }
	         else if (compareRes > 0) {
	        	 return binarySearchRightBound(A, str, left, middle - 1);
	         }
	         else {
	        	 return binarySearchRightBound(A, str, middle + 1, right); 
	         }
	         
	         
	     }
	     return right;
	 }


}