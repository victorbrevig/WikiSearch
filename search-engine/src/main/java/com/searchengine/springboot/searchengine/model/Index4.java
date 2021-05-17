package com.searchengine.springboot.searchengine.model;

import java.io.*;
import java.util.Scanner;
 
public class Index4 implements Index {
	// (word -> doc hash table) 
    HashTable<String, HashTable<Integer,Integer>> wordTable;
    // (docId -> doc title) 
    HashTable<Integer,String> idToDoc;
    
    @SuppressWarnings("unchecked")
	public Index4(String filename) {
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
            
            HashTable<Integer,Integer> currentDocTable;
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
                	HashTable<Integer,Integer> docTable = wordTable.search(word);
                	
                	if(docTable == null) {
                		// Insert new word in hash table
                		currentDocTable = new HashTable(LinkedList.class);
                		currentDocTable.insert(docIdCount, docIdCount);
                		wordTable.insert(word, currentDocTable);
                	}
                	else {
                		// Need to check first doc id.. if the same word appears twice in document
                		if(docTable.search(docIdCount) == null) {
                			docTable.insert(docIdCount,docIdCount);
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

        HashTable<Integer,Integer> docTable = wordTable.search(searchstr);
        
		for(LinkedList<KeyValuePair<Integer,Integer>> ll : docTable.table) {
			if(ll == null) {
				continue;
			}
			LinkedListNode<KeyValuePair<Integer,Integer>> current = ll.head;
			while(current != null) {
				System.out.println(idToDoc.search(current.data.key));
				current = current.next;
			}
		}
        return (docTable.n > 0);
    }
    
    
    public HashTable<Integer,Integer> searchWord(String word) {
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