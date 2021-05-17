package com.searchengine.springboot.searchengine.model;

import java.io.*;
import java.util.Scanner;

 
class Index1 implements Index {
 
    WikiItem start;
       
    public Index1(String filename) {
        String word;
        WikiItem current, tmp;
        try {
            Scanner input = new Scanner(new File(filename), "UTF-8");
            word = input.next();
            start = new WikiItem(word, null);
            current = start;
            // counter
            int count = 0;
            while (input.hasNext()) {   // Read all words in input
                word = input.next();
                System.out.println(word);
                tmp = new WikiItem(word, null);
                current.next = tmp;
                current = tmp;
                count++;
                System.out.println(count);
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }
    }
 
    
    public boolean search(String searchstr) {
        WikiItem current = start;
        String documentTitle = IndexTools.getDocumentTitle(current);
        boolean searchMatch = false;
        boolean documentMatch = false;
        
        while (current != null) {
        	
        	if (current.str.equals("---END.OF.DOCUMENT---")) {
        		// If not at end of file
        		if (current.next != null) {
        			documentTitle = IndexTools.getDocumentTitle(current.next);
        			documentMatch = false;
        		}
        	}
            if (current.str.equals(searchstr) && !documentMatch) {
                System.out.println(documentTitle);
                searchMatch = true;
                documentMatch = true;
            }
            current = current.next;  
        }
        return searchMatch;
    }
 
    public static void main(String[] args) {
        System.out.println("Preprocessing " + args[0]);
        Index1 i = new Index1(args[0]);
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