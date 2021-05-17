package com.searchengine.springboot.searchengine.model;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;
 
public class Index7 implements Index {
	// (word -> doc hash table -> position hash table) 
    HashTable<String, HashTable<Integer,HashTable<Integer,Integer>>> wordTable;
    // (docId -> doc title) 
    public HashTable<Integer,String> idToDoc; 
    // (docId -> array of wordId)
    public HashTable<Integer, ArrayOfInts> docIdToTextArray;
    // (wordId -> string word)
    public HashTable<Integer, String> idToOrigWord;
    // (string word -> wordId)
    public HashTable<String, Integer> origWordToId;
    
    // (wordId -> trimmed word)
    public HashTable<Integer, String> idToWord;
    // (wordId -> reverse trimmed word)
    public HashTable<Integer, String> idToRevWord;
    
    
    public Trie trie;
    public Trie trieR;
    
    public double[] P;
    
    public RangeTree2D rangeTree2D;
    
    @SuppressWarnings("unchecked")
	public Index7(String filename) {
        String word;
        String currentDoc;
        wordTable = new HashTable(LinkedList.class);
        idToDoc = new HashTable(LinkedList.class);
        docIdToTextArray = new HashTable(LinkedList.class);
        idToOrigWord = new HashTable(LinkedList.class);
        origWordToId = new HashTable(LinkedList.class);
        idToWord = new HashTable(LinkedList.class);
        idToRevWord = new HashTable(LinkedList.class);
        
        int docIdCount = 0;
        int wordIdCount = 0;
        int trimWordIdCount = 0;
        
        try {
            Scanner input = new Scanner(new File(filename), "UTF-8");
            
            // TEST
            int count = 0;
            // Counter to store position in document
            int positionCounter = 0;
            
            // Get first document title
            currentDoc = IndexTools.getNextDocumentTitle(input);

            HashTable<Integer,HashTable<Integer,Integer>> currentDocTable;
            
            idToDoc.insert(docIdCount, currentDoc);
            docIdToTextArray.insert(docIdCount, new ArrayOfInts());
            
            // Go through the rest of the documents
            while (input.hasNext()) {   // Read all words in input
                word = input.next();
                String originalWord = word;
                // Trim word
                word = IndexTools.trimWord(word);
                
                if(word.equals("---END.OF.DOCUMENT---") && input.hasNextLine()) {
                	// CHANGE CURRENT DOC
                	currentDoc = IndexTools.getNextDocumentTitle(input);
                	docIdCount++;
                	idToDoc.insert(docIdCount,currentDoc);
                	docIdToTextArray.insert(docIdCount, new ArrayOfInts());
                	
                	positionCounter = -1;
                }
                else {
                	// DO SINGLE WORD
                	HashTable<Integer,HashTable<Integer,Integer>> docTable = wordTable.search(word);
                	
                	// Check if word has not been seen before
                	if(docTable == null) {
                		
                		// Insert new word in hash table
                		HashTable<Integer,Integer> posTable = new HashTable(LinkedList.class);
                		posTable.insert(positionCounter,positionCounter);
                		currentDocTable = new HashTable(LinkedList.class);
                		currentDocTable.insert(docIdCount, posTable);
                		wordTable.insert(word, currentDocTable);
                		
                		idToWord.insert(trimWordIdCount, word);
                		StringBuilder str = new StringBuilder(word);
                		StringBuilder strR = str.reverse();
                		idToRevWord.insert(trimWordIdCount, strR.toString());
                		trimWordIdCount++;
                	}
                	else {
                		// Insert current doc & position (if its not already there)
                		if(docTable.search(docIdCount) == null) {
                			HashTable<Integer,Integer> currentPosTable = new HashTable(LinkedList.class);
                			currentPosTable.insert(positionCounter,positionCounter);
                			docTable.insert(docIdCount,currentPosTable);
                		}
                		else {
                			docTable.search(docIdCount).insert(positionCounter,positionCounter);
                		}
                		
                	}
                	// Add original word to tables
                	
                	Integer origWordId = origWordToId.search(originalWord);
                	if(origWordId == null) {
                		origWordToId.insert(originalWord,wordIdCount);
                		idToOrigWord.insert(wordIdCount, originalWord);
                		
                		wordIdCount++;
                		docIdToTextArray.search(docIdCount).add(wordIdCount-1);
                	}
                	else {
                		docIdToTextArray.search(docIdCount).add(origWordId);
                	}
                	
                	
                }
                System.out.println(word);
                count++;
                positionCounter++;
                System.out.println(count);
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }
        
        System.out.println(idToWord.n);
        System.out.println(idToRevWord.n);
        System.out.println(wordTable.n);
        // Fill trie and trieR
        trie = new Trie(idToWord);
        trieR = new Trie(idToRevWord);
        
        Integer[] X = new Integer[idToWord.n];
        Integer[] Y = new Integer[idToRevWord.n];
        
        
        
        for(KeyValuePair<Integer, String> kvp : idToWord) {
        	String str = kvp.value;
        	String revStr = idToRevWord.search(kvp.key);
        	
        	trie.insert(str + ' ', wordTable.search(str).n, kvp.key);
        	trieR.insert(revStr + ' ', wordTable.search(str).n, kvp.key);
        	
        	X[kvp.key] = kvp.key;
        	Y[kvp.key] = kvp.key;
        	
        }
        
        
        // sort X in lexicographical order
        Arrays.sort(X, Comparator.comparing(x -> idToWord.search(x)));
        // sort Y in lexicographical order
    	Arrays.sort(Y, Comparator.comparing(y -> idToRevWord.search(y)));
    	
    	rangeTree2D = new RangeTree2D(X, Y, idToWord, idToRevWord);
        
        
        // PAGERANK
        P = new double[idToDoc.n];
        Arrays.fill(P,1.0 / idToDoc.n);
        
        KeyValuePair<LinkedList<KeyValuePair<Double, Integer>>[],HashTable<Integer,Integer>> matrixAndSinkNodes = IndexTools.createMatrixFromFile("C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\Wiki_links_10MB.txt", LinkedList.class, idToDoc.n);
        
        LinkedList<KeyValuePair<Double, Integer>>[] M = matrixAndSinkNodes.key;
        HashTable<Integer,Integer> sinkNodes = matrixAndSinkNodes.value;
        
        P = IndexTools.pageRank(P, M, sinkNodes, 1000, 0.0001, 0.85);
        
    }
 
    
    public boolean search(String searchstr) {

        HashTable<Integer,HashTable<Integer,Integer>> docTable = wordTable.search(searchstr);
        
        if(docTable == null) {
        	return false;
        }
        
        for(LinkedList<KeyValuePair<Integer,HashTable<Integer,Integer>>> ll : docTable.table) {
			if(ll == null) {
				continue;
			}
			LinkedListNode<KeyValuePair<Integer,HashTable<Integer,Integer>>> current = ll.head;
			while(current != null) {
				System.out.println(idToDoc.search(current.data.key));
				current = current.next;
			}
		}
        return (docTable.n > 0);
    }
    
    
    
    public HashTable<Integer,HashTable<Integer,Integer>> searchWord(String word) {
    	return wordTable.search(word);
    }
 
    public static void main(String[] args) {
        System.out.println("Preprocessing " + args[0]);
        Index7 i = new Index7(args[0]);

        Scanner console = new Scanner(System.in);
        
        for (;;) {
            System.out.println("Input search string or type exit to stop");
            String searchstr = console.nextLine();
            if (searchstr.equals("exit")) {
                break;
            }
            
            if (!i.search(searchstr)) {
                System.out.println(searchstr + " does not exist");
            } 
        }
        
        console.close();
        
    }
}
