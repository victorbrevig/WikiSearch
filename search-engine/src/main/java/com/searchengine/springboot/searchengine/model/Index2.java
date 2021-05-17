package com.searchengine.springboot.searchengine.model;

import java.io.*;
import java.util.Scanner;

 
class Index2 implements Index {
	
    LinkedList<KeyValuePair<String,LinkedList<Integer>>> wordLinkedList;
    // (docId -> doc title) 
    HashTable<Integer,String> idToDoc;
    
    public Index2(String filename) {
        String word;
        String currentDoc;
        wordLinkedList = new LinkedList<KeyValuePair<String,LinkedList<Integer>>>();
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
                	// Update current document
                	currentDoc = IndexTools.getNextDocumentTitle(input);
                	docIdCount++;
                	idToDoc.insert(docIdCount,currentDoc);
                }
                else {
                	LinkedListNode<KeyValuePair<String,LinkedList<Integer>>> wordListItem = wordLinkedList.search(new KeyValuePair<String,LinkedList<Integer>>(word,null));
 
                	// Insert linked list element if word has not yet been seen
                	if(wordListItem == null) {
                		currentDocList = new LinkedList<Integer>();
                		currentDocList.insertFront(docIdCount);
                		wordLinkedList.insertFront(new KeyValuePair<String,LinkedList<Integer>>(word,currentDocList));
                	}
                	else {
                		LinkedList<Integer> docList = wordListItem.data.value;
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
    	LinkedListNode<KeyValuePair<String,LinkedList<Integer>>> wordListItem = wordLinkedList.search(new KeyValuePair<String,LinkedList<Integer>>(searchstr,null));
        boolean match = false;
        
        if(wordListItem != null) {
        	LinkedList<Integer> docList = wordListItem.data.value;
        	LinkedListNode<Integer> current = docList.head;
        	match = true;
    		while (current != null) {
    			System.out.println(idToDoc.search(current.data));
    			current = current.next;
    		}
        }
        return match;
    }
 
    public static void main(String[] args) {
        System.out.println("Preprocessing " + args[0]);
        Index2 i = new Index2(args[0]);
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