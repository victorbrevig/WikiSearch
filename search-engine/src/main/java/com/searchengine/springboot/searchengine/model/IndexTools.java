package com.searchengine.springboot.searchengine.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class IndexTools {
	
	
	public static String getNextDocumentTitle(Scanner input) {
    	String currentDoc;
    	Scanner inputCopy = input;
    	currentDoc = inputCopy.nextLine();
    	while(currentDoc.equals("") && inputCopy.hasNextLine()) {
    		currentDoc = inputCopy.nextLine();
    	}
    	currentDoc = currentDoc.substring(0, currentDoc.length() - 1);
		return currentDoc;
    }
	
	

	public static boolean documentExists(WikiItem start, String doc) {
		WikiItem current = start;
		while(current != null) {
			if(current.str.equals(doc)) {
				return true;
			}
			current = current.next;
		}
		return false;
	}
	
    public static String getDocumentTitle(WikiItem word) {
    	WikiItem current = word;
    	String docTitle = "";
    	
    	while(!current.str.contains(".")) {
			docTitle = docTitle.concat(" " + current.str);
			current = current.next;
		}
    	docTitle = docTitle.concat(" " + current.str);
    	// Remove first char ' ' and last char '.'
    	docTitle = docTitle.substring(1, docTitle.length() - 1);
		return docTitle;
    }
    
    
public static HashTable<Integer,HashTable<Integer,Integer>> phraseSearch(Index7 index, String phrase) {
    	
		HashTable<Integer,HashTable<Integer,Integer>> finalDocs = new HashTable(LinkedList.class);
    	
    	String[] splitPhrase = phrase.split("\\s+");
    	int phraseLength = splitPhrase.length;
    	
    	if(phraseLength == 1) {
    		// If its just _, return empty set. Technically should return all documents and positions
    		if(phrase.equals("_")) {
    			return finalDocs;
    		}
    		// If its only a single word, just search normally
    		return index.searchWord(phrase);
    	}
    	
    	
    	HashTable<String, Integer> wordToPos = new HashTable(LinkedList.class);
    	ArrayList<WordTablePair> pairList = new ArrayList<WordTablePair>();
    	for(int i=0; i<phraseLength; i++) {
    		String word = splitPhrase[i];
    		if(!word.equals("_")) {
    			wordToPos.insert(word,i);
    			HashTable<Integer, HashTable<Integer, Integer>> wordDocs = index.searchWord(word);
    			// If a word does not exist, return emptyo hash table immediately
    			if(wordDocs == null) {
    				return finalDocs;
    			}
    			else {
    				pairList.add(new WordTablePair(word, wordDocs));
    			}
    		}
    	}
    	
    	Collections.sort(pairList);
    	
    	HashTable<Integer,HashTable<Integer,Integer>> candidates = pairList.get(0).table;
    	int s = wordToPos.search(pairList.get(0).word);
    	int newPos;
    	
    	// SELECTION PROCESS
    	for(KeyValuePair<Integer,HashTable<Integer,Integer>> c : candidates) {
    		boolean passedDoc = false;
    		int p = 0;
    		for(KeyValuePair<Integer,Integer> pos : c.value) {
    			boolean passedRound = false;
    			p = pos.value;
    			// Start from second word, since candidates comes from the first
    			for(int i = 1; i< pairList.size(); i++) {
    				
    				int cp = wordToPos.search(pairList.get(i).word);
    				// Calculate new position
        			newPos = p - (s - cp);
        			
        			// If current word contains current doc
        			HashTable<Integer, Integer> currentWordPositions = pairList.get(i).table.search(c.key);
        			if(currentWordPositions != null) {
        				// Passed offset position exists
        				passedRound = currentWordPositions.search(newPos) != null;
        			}
        			if(!passedRound) {
        				break;
        			}
        			
    			}
    			// If all rounds are passed for a position, the doc passes
    			if(passedRound) {
    				passedDoc = true;
    				break;
    			}
    			
    		}
    		if(passedDoc) {
    			// add
    			HashTable<Integer,Integer> newDocTable = new HashTable(LinkedList.class);
    			newDocTable.insert(p-s,(p-s)+pairList.size()-1);
    			finalDocs.insert(c.key,newDocTable);
    		}
    		
    	}
    	return finalDocs;
    	
	}


	public static String removeEndSigns(String s, HashTable<Character, Character> validEndChars) {
		
		
		while(validEndChars.search(s.charAt(s.length()-1)) == null) {
			s = s.substring(0,s.length()-1);
		}
		return s;
	}
	  	

	
	public static boolean badStartPosSymbol(String str, int startPos) {
		boolean res = false;
		switch(str.charAt(startPos)) {
		case '(':
			res = true;
		case '"':
			res = true;
	}
		return res;
	}
	
	public static boolean badEndPosSymbol(String str, int endPos) {
		boolean res = false;
		switch(str.charAt(endPos)) {
			case '₿':
				res = true;
			case '"':
				res = true;
			case ',':
				res = true;
			case '.':
				res = true;
			case '!':
				res = true;
			case '=':
				res = true;
			case '/':
				res = true;
			case ')':
				res = true;
			case ';':
				res = true;
			case '(':
				res = true;
			case ':':
				res = true;
		}
		return res;
	}
	
	
	static public String trimWord(String word) {
		int endPos = word.length()-1;
        int startPos = 0;
        while(IndexTools.badEndPosSymbol(word, endPos) && endPos > 0) {
			endPos--;
		}
        while(IndexTools.badStartPosSymbol(word,startPos) && startPos < endPos) {
        	startPos++;
        }
        word = word.substring(startPos,endPos+1);
        
        return word;
	}
	
	
	public static double norm(double[] vec) {
		double result = 0.0;
		for (int i = 0; i < vec.length; i++) {
			result += Math.pow(vec[i], 2);
		}
		result = Math.sqrt(result);
		return result;
	}
	
	public static double vecSum(double[] vec) {
		double result = 0.0;
		for (int i = 0; i < vec.length; i++) {
			result += vec[i];
		}
		return result;
	}
	
	
	public static KeyValuePair<LinkedList<KeyValuePair<Double, Integer>>[],HashTable<Integer,Integer>> createMatrixFromFile(String filename, Class<LinkedList> class1, int docAmount) {
		LinkedList<KeyValuePair<Double, Integer>>[] M = (LinkedList<KeyValuePair<Double,Integer>>[]) java.lang.reflect.Array.newInstance(class1, docAmount);
		HashTable<Integer,Integer> sinkNodes = new HashTable(LinkedList.class);
		String current;
		try {
			Scanner input = new Scanner(new File(filename), "UTF-8");
			current = input.next();
			
			// Do first from doc
			int fromDoc = Integer.parseInt(current);
			current = input.next();
			int numOfOutLinks = Integer.parseInt(current);
			
			int toDoc;
			
			while(input.hasNext()) {
				
				current = input.next();
				
				
				if(current.equals("-") && input.hasNextLine()) {
					current = input.next();
					int oldFromDoc = fromDoc;
					fromDoc = Integer.parseInt(current);
					current = input.next();
					numOfOutLinks = Integer.parseInt(current);
					
					
					int diffId = fromDoc-oldFromDoc;
					for(int i = oldFromDoc+1; i<fromDoc; i++) {
						sinkNodes.insert(i,i);
					}
					
					continue;
				}
				
				toDoc = Integer.parseInt(current);
				if(M[toDoc] == null) {
					LinkedList<KeyValuePair<Double,Integer>> newList = new LinkedList<KeyValuePair<Double,Integer>>();
					// Link 1/no of outgoing links and int saying which document matches in P
					newList.insertBack(new KeyValuePair<Double,Integer>(1.0 / numOfOutLinks, fromDoc));
					M[toDoc] = newList;
				}
				else {
					LinkedList<KeyValuePair<Double,Integer>> currentList = M[toDoc];
					currentList.insertBack(new KeyValuePair<Double,Integer>(1.0 / numOfOutLinks, fromDoc));
				}
				
			}
			// Insert remaining sink nodes if any
			for(int i = fromDoc+1; i < docAmount; i++) {
				sinkNodes.insert(i,i);
			}

			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new KeyValuePair<LinkedList<KeyValuePair<Double, Integer>>[],HashTable<Integer,Integer>>(M, sinkNodes);
	}
	
	
	
	public static double[] pageRank(double[] P, LinkedList<KeyValuePair<Double, Integer>>[] M, HashTable<Integer,Integer> sinkNodes, int limitN, double limitErr, double d) {
		
		double sinkNodeSum = sinkNodes.n * P[0];
		double newSinkNodeSum = 0;

		double prevSum = 0.0;
		double prevNorm = 0.0;
		
		for(int n = 0; n<limitN; n++) {
			System.out.println("ROUND " + n);
			
			double[] Pcopy = Arrays.copyOf(P, P.length);
			
			double currentNorm = 0.0;
			double currentSum = 0.0;
			
			for(int i = 0; i<P.length; i++) {
				
				double newPVal = 0.0;
				LinkedList<KeyValuePair<Double, Integer>> list = M[i];

				if(list == null) {
					newPVal = 0;
				}
				else {
					for(LinkedListNode<KeyValuePair<Double,Integer>> node : list) {

						newPVal += node.data.key * Pcopy[node.data.value];
					}
				}

				double updateVal = ((1-d+d*sinkNodeSum) / P.length) + d*newPVal;
				
				double pDiff = updateVal - P[i];
				P[i] = updateVal;
				
				currentSum += updateVal;
				currentNorm += pDiff*pDiff;
				
				// Check if sink node
				if(sinkNodes.search(i) != null) {
					newSinkNodeSum += updateVal;
				}
				
			}
			
			currentNorm = Math.sqrt(currentNorm);
			
			// Check for diff in norm etc...
			if(Math.abs(prevNorm-currentNorm) < limitErr) {
				break;
			}
			
			System.out.println("Norm: " + currentNorm);
			System.out.println("Sum: " + currentSum);
			
			sinkNodeSum = newSinkNodeSum;
			newSinkNodeSum = 0.0;
			prevSum = currentSum;
			currentSum = 0.0;
			prevNorm = currentNorm;
			currentNorm = 0.0;
			
		}
		return P;
	}
	
	
	public static String revString(String s) {
		return new StringBuilder(s).reverse().toString();
	}
	
	public static void main(String[] args) throws IOException {
		/*
		String filename = "C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\Wiki_100KB.txt";
		
		Index6 index = new Index6(filename);
		
		System.out.println(index.idToOrigWord.n);
		System.out.println(index.origWordToId.n);
		
		ArrayOfInts doc1Array = index.docIdToTextArray.search(1);
		for(Integer i : doc1Array) {
			System.out.println(index.idToOrigWord.search(i));
		}
		*/
		//String filename = "C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\writeFileTest.txt";
		//createMatrixFromFile(filename, LinkedList.class, 40);
		// ₿
		//System.out.println("a".compareTo("-"));
		//System.out.println("$".compareTo("a"));
		//System.out.println("₿".compareTo("a"));
		
		//System.out.println("".compareTo(" "));
		
		
		System.out.println("an- ".compareTo("an "));
	}


}
