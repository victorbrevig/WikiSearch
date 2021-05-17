package com.searchengine.springboot.searchengine.model;

import java.io.*;
import java.util.Scanner;
 
public class Index3 implements Index {
	// (word -> doc list) 
    HashTable<String, LinkedList<Integer>> wordTable;
    // (docId -> doc title) 
    HashTable<Integer,String> idToDoc;
    
    @SuppressWarnings("unchecked")
	public Index3(String filename) {
        String word;
        String currentDoc;
        wordTable = new HashTable(LinkedList.class);
        idToDoc = new HashTable(LinkedList.class);
        int docIdCount = 0;
        
        try {
            Scanner input = new Scanner(new File(filename), "UTF-8");
            
            // TEST
            int count = 0;
            
            // Get first document title
            currentDoc = IndexTools.getNextDocumentTitle(input);
            
            LinkedList<Integer> currentDocList;
            
            idToDoc.insert(docIdCount, currentDoc);
            
            // Go through the rest of the documents
            while (input.hasNext()) {   // Read all words in input
                word = input.next();
                
                if(word.equals("---END.OF.DOCUMENT---") && input.hasNextLine()) {
                	// Change currentDoc
                	currentDoc = IndexTools.getNextDocumentTitle(input);
                	docIdCount++;
                	idToDoc.insert(docIdCount,currentDoc);
                	
                }
                else {
                	LinkedList<Integer> docList = wordTable.search(word);
                	
                	if(docList == null) {
                		// Insert new word in hash table
                		currentDocList = new LinkedList<Integer>();
                		currentDocList.insertFront(docIdCount);
                		wordTable.insert(word, currentDocList);
                	}
                	else {
                		// Need to check first doc id.. if the same word appears twice in document
                		if(docList.head.data != docIdCount) {
                			docList.insertFront(docIdCount);
                		}
                	}
                }
                System.out.println(word);
                count++;
                System.out.println(count);
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }
    }
 
    
    public boolean search(String searchstr) {

        boolean match = false;

        LinkedList<Integer> wordList = wordTable.search(searchstr);
        
        if(wordList != null) {
        	// Print all documents containing search word
        	LinkedListNode<Integer> current = wordList.head;
        	match = true;
    		while (current != null) {
    			System.out.println(idToDoc.search(current.data));
    			current = current.next;
    		}
        }
        return match;
    }
    
    
    public LinkedList<Integer> searchWord(String word) {
    	return wordTable.search(word);
    }
 
    public static void main(String[] args) {
        System.out.println("Preprocessing " + args[0]);
        Index3 i = new Index3(args[0]);
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