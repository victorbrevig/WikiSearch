package com.searchengine.springboot.searchengine.model;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
 
public class Index6 implements Index {
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
    
    public Trie trie;
    public Trie trieR;
    
    @SuppressWarnings("unchecked")
	public Index6(String filename) {
        String word;
        String currentDoc;
        wordTable = new HashTable(LinkedList.class);
        idToDoc = new HashTable(LinkedList.class);
        docIdToTextArray = new HashTable(LinkedList.class);
        idToOrigWord = new HashTable(LinkedList.class);
        origWordToId = new HashTable(LinkedList.class);
        trie = new Trie();
        trieR = new Trie();
        int docIdCount = 0;
        int wordIdCount = 0;
        
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
            
            // Fill trie and trieR
            for(KeyValuePair<String, HashTable<Integer, HashTable<Integer, Integer>>> kvp : wordTable) {
            	word = kvp.key;
            	// Insert new word in trie and trieR
        		StringBuilder strOriginal = new StringBuilder(word);
        		StringBuilder str = new StringBuilder(word);
        		StringBuilder strR = new StringBuilder(str.reverse());
        		strOriginal.append('₿');
        		strR.append('₿');
        		trie.insert(strOriginal.toString(), wordTable.search(word).n);
        		trieR.insert(strR.toString(), wordTable.search(word).n);
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
        Index6 i = new Index6(args[0]);

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