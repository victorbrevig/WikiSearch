package com.searchengine.springboot.searchengine.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

public class RangeTree2DTester {

	
	// (wordId -> trimmed word)
    public HashTable<Integer, String> idToWord;
    // (wordId -> reverse trimmed word)
    public HashTable<Integer, String> idToRevWord;
    
    public HashTable<String,String> wordsFound;
    
    public Trie trie;
    public Trie trieR;
    
    public RangeTree2D rangeTree2D;
	
	RangeTree2DTester(String filename) {
		String word;
		
		int trimWordIdCount = 0;
		
		idToWord = new HashTable(LinkedList.class);
        idToRevWord = new HashTable(LinkedList.class);
        wordsFound = new HashTable(LinkedList.class);
		
		try {
			Scanner input = new Scanner(new File(filename), "UTF-8");
			
			word = input.next();
			word = IndexTools.trimWord(word);
			idToWord.insert(trimWordIdCount,word);
			StringBuilder str = new StringBuilder(word);
    		StringBuilder strR = str.reverse();
    		idToRevWord.insert(trimWordIdCount, strR.toString());
			wordsFound.insert(word,word);
			trimWordIdCount++;
			
			while (input.hasNext()) {   // Read all words in input
                word = input.next();
                word = IndexTools.trimWord(word);
                System.out.println(word);
                
                if(wordsFound.search(word) == null) {
                	idToWord.insert(trimWordIdCount,word);
                	str = new StringBuilder(word);
            		strR = str.reverse();
            		idToRevWord.insert(trimWordIdCount, strR.toString());
        			wordsFound.insert(word,word);
        			trimWordIdCount++;
                }
                
                
            }
            input.close();
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println(idToWord.n);
		System.out.println(idToRevWord.n);
		
		trie = new Trie(idToWord);
        trieR = new Trie(idToRevWord);
        
        
        StopWatch watch = new StopWatch();
        
        watch.start();
        
        Integer[] X = new Integer[idToWord.n];
        Integer[] Y = new Integer[idToRevWord.n];
        
        for(KeyValuePair<Integer, String> kvp : idToWord) {
        	String str = kvp.value;
        	String revStr = idToRevWord.search(kvp.key);
        	
        	// rank does not matter here
        	trie.insert(str + ' ', 100, kvp.key);
        	trieR.insert(revStr + ' ', 100, kvp.key);
        	
        	X[kvp.key] = kvp.key;
        	Y[kvp.key] = kvp.key;
        	
        }
        
        // sort X in lexicographical order
        Arrays.sort(X, Comparator.comparing(x -> idToWord.search(x)));
        // sort Y in lexicographical order
    	Arrays.sort(Y, Comparator.comparing(y -> idToRevWord.search(y)));
    	
    	rangeTree2D = new RangeTree2D(X, Y, idToWord, idToRevWord);
		
    	watch.stop();
    	System.out.println("PRE-PROCESSING TIME(MS): " + watch.getTime(TimeUnit.MILLISECONDS));
    	watch.reset();
	}
	
	public boolean search(String prefix, String suffix) {
		
		StringBuilder rightStr = new StringBuilder(suffix);
		rightStr.reverse();
		String left = prefix;
		String right = rightStr.toString();
		
		Pair<Integer,Integer> xRange = trie.getRange(left);
		Pair<Integer,Integer> yRange = trieR.getRange(right);
		
		Pair<ArrayList<Integer>, ArrayList<YarrayResult>> resImplicit = rangeTree2D.rangeSearch2D(xRange.first, xRange.second, yRange.first, yRange.second);
		
		if(resImplicit.first.size() != 0 || resImplicit.second.size() != 0) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
	public static void main(String[] args) {
		StopWatch watch = new StopWatch();
		
        System.out.println("Preprocessing " + args[0]);
        RangeTree2DTester RT = new RangeTree2DTester(args[0]);
        Scanner console = new Scanner(System.in);
        for (;;) {
            System.out.println("Input search string or type exit to stop");
            System.out.println("Prefix:");
            String prefix = console.nextLine();
            System.out.println("Suffix:");
            String suffix = console.nextLine();
            if (prefix.equals("exit")) {
                break;
            }
            watch.start();
            boolean status = RT.search(prefix,suffix);
            watch.stop();
            System.out.println("TIME(NS): " + watch.getTime(TimeUnit.NANOSECONDS));
			watch.reset();
            
            if (!status) {
                System.out.println("does not exist");
            } 
        }
        console.close();
    }
	
	
}
