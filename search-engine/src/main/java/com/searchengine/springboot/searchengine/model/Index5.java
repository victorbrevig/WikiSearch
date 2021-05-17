package com.searchengine.springboot.searchengine.model;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
 
public class Index5 implements Index {
	// (word -> doc hash table -> position hash table) 
    HashTable<String, HashTable<Integer,HashTable<Integer,Integer>>> wordTable;
    // (docId -> doc title) 
    public HashTable<Integer,String> idToDoc; 
    
    @SuppressWarnings("unchecked")
	public Index5(String filename) {
        String word;
        String currentDoc;
        wordTable = new HashTable(LinkedList.class);
        idToDoc = new HashTable(LinkedList.class);
        int docIdCount = 0;
        
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
            
            
            // Go through the rest of the documents
            while (input.hasNext()) {   // Read all words in input
                word = input.next();
                
                if(word.equals("---END.OF.DOCUMENT---") && input.hasNextLine()) {
                	// CHANGE CURRENT DOC
                	currentDoc = IndexTools.getNextDocumentTitle(input);
                	docIdCount++;
                	idToDoc.insert(docIdCount,currentDoc);
                	positionCounter = -1;
                }
                else {
                	// DO SINGLE WORD
                	HashTable<Integer,HashTable<Integer,Integer>> docTable = wordTable.search(word);
                	
                	// Check if word has been seen before
                	if(docTable == null) {
                		// Insert new word in hash table
                		HashTable<Integer,Integer> posTable = new HashTable(LinkedList.class);
                		posTable.insert(positionCounter,positionCounter);
                		currentDocTable = new HashTable(LinkedList.class);
                		currentDocTable.insert(docIdCount, posTable);
                		wordTable.insert(word, currentDocTable);
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
        Index5 i = new Index5(args[0]);
        
        /*
        int counter = 0;

    	for(WordLinkedList<HashSet<DocumentPair>> h : i.table.table) {
        	if(h != null) {
        		counter++;
        	}
        }
        System.out.println("Counter: " + counter);
        System.out.println("m: " + i.table.m);
        System.out.println("n: " + i.table.n);
        int coll = i.table.n - counter;
        System.out.println("collisions: " + coll);
        
        */
        
        
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