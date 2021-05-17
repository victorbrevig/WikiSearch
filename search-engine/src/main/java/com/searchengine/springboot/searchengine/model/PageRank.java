package com.searchengine.springboot.searchengine.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;


public class PageRank {
	HashTable<String, Integer> docToId;
	HashTable<String, String> foundStrInDoc;
	HashTable<Integer, Integer> docOutgoingLinks;
	LinkedList<KeyValuePair<Integer,Integer>> linkQueue;
	LinkedList<KeyValuePair<Double,Integer>>[] M;
	
	StopWatch watch = new StopWatch();
	
	double[] P;
	static String fileName = "C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\Wiki_50MB.txt";
	KMPsentence kmp;
	int numberOfSinkNodes;
	
	@SuppressWarnings("unchecked")
	public PageRank(Class<LinkedList> class1) {
		docToId = getDocTable();
		foundStrInDoc = new HashTable(LinkedList.class);
		docOutgoingLinks = new HashTable(LinkedList.class);
		String word;
		String prevWord = " ";
		String currentDoc;
		int docIdCount = 0;
		linkQueue = new LinkedList<KeyValuePair<Integer,Integer>>();
		
		// Construct NFA
		watch.start();
		kmp = new KMPsentence();
		for(KeyValuePair<String,Integer> kvp : docToId) {
			kmp.insertSentence(kvp.key);
		}
		watch.stop();
		Long preprocessingTime = watch.getTime(TimeUnit.MILLISECONDS);
		watch.reset();
		System.out.println("Pre-processing time: " + preprocessingTime);
		
		watch.start();
		
		try {
            Scanner input = new Scanner(new File(fileName), "UTF-8");
            currentDoc = IndexTools.getNextDocumentTitle(input);
            word = input.next();
            
            int foundCount = 0;
            while (input.hasNext()) {
                word = input.next();
                //String wordCopy = word;
                if(word.equals("---END.OF.DOCUMENT---") && input.hasNextLine()) {
                	// CHANGE CURRENT DOC
                	currentDoc = IndexTools.getNextDocumentTitle(input);
                	//System.out.println(currentDoc);
                	foundStrInDoc = new HashTable(LinkedList.class);
                	kmp.resetState();
                	docIdCount++;
                }
                
                // Dont want strings after a '.'
                if(((Character) prevWord.charAt(prevWord.length()-1)).equals('.')) {
                	kmp.resetState();
                	prevWord = word;
                	continue;
                }
                
                prevWord = word;
                word = IndexTools.trimWord(word);
                String doc = kmp.processWord(word);
                
                if(doc != null) {
                	if(foundStrInDoc.search(doc) == null && !doc.equals(currentDoc)) {
                		// Link to be added
                		linkQueue.insertBack(new KeyValuePair<Integer,Integer>(docIdCount, docToId.search(doc)));
                		Integer outLinks = docOutgoingLinks.search(docIdCount);
            			if(outLinks == null) {
            				docOutgoingLinks.insert(docIdCount, 1);
            			}
            			else {
            				docOutgoingLinks.updateValue(docIdCount, outLinks+1);
            			}
            			//System.out.println("Doc: " + currentDoc + "  :  " + doc);
            			
            			//System.out.println("From: " + docIdCount + "    To: " + docToId.search(doc));
            			
            			foundStrInDoc.insert(doc, doc);
            			foundCount++;
                	}
                }
            }
            input.close();
            
            watch.stop();
            Long findAllLinksTime = watch.getTime(TimeUnit.MILLISECONDS);
            watch.reset();
            System.out.println("Find all links time: " + findAllLinksTime);
            
            
            System.out.println("No. of links: " + foundCount);
            System.out.println("No. of docs: " + docToId.n);
            
            
            this.M = (LinkedList<KeyValuePair<Double,Integer>>[]) java.lang.reflect.Array.newInstance(class1, docToId.n);
            //populateMatrix();
            
            // Find sink nodes
            for(KeyValuePair<String,Integer> kvp : docToId) {
            	Integer nodeID = kvp.value;
            	if(docOutgoingLinks.search(nodeID) == null) {
            		numberOfSinkNodes++;
            	}
            }
            
            
            P = new double[docToId.n];
            //Arrays.fill(P,1.0 / docToId.n);
            
            
            
            //KeyValuePair<LinkedList<KeyValuePair<Double, Integer>>[],HashTable<Integer,Integer>> matrixAndSinkNodes = IndexTools.createMatrixFromFile("C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\Wiki_links_10MB.txt", LinkedList.class, docToId.n);
            
            //LinkedList<KeyValuePair<Double, Integer>>[] M = matrixAndSinkNodes.key;
            //HashTable<Integer,Integer> sinkNodes = matrixAndSinkNodes.value;

            //P = pageRank(P, M, 1000, 0.0001, 0.85);
            //P = IndexTools.pageRank(P, M, sinkNodes, 1000, 0.0001, 0.85);
            
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + fileName);
        }
	}
	
	
	
	
	
	
	private double[] pageRank(double[] P, LinkedList<KeyValuePair<Double, Integer>>[] M, int limitN, double limitErr, double d) {
		
		double sinkNodeSum = numberOfSinkNodes * P[0];
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
				//double updateVal = d*newPVal + ((1-d)/P.length) + (d/P.length)*sinkNodeSum;
				double updateVal = ((1-d+d*sinkNodeSum) / P.length) + d*newPVal;
				//System.out.println((d/P.length)*sinkNodeSum);
				
				double pDiff = updateVal - P[i];
				P[i] = updateVal;
				
				currentSum += updateVal;
				
				currentNorm += pDiff*pDiff;
				
				// Check if sink node
				if(docOutgoingLinks.search(i) == null) {
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

	private static HashTable<String, Integer> getDocTable() {
		String word;
		String prevWord;
		HashTable<String,Integer> docToId = new HashTable(LinkedList.class); 
		
		int docIdCount = 0;
		try {
            Scanner input = new Scanner(new File(fileName), "UTF-8");
            String currentDoc = IndexTools.getNextDocumentTitle(input);
            word = input.next();
            //First doc
            docToId.insert(currentDoc,docIdCount);

            while (input.hasNext()) {   // Read all words in input
                word = input.next();
                if(word.equals("---END.OF.DOCUMENT---") && input.hasNextLine()) {
                	// CHANGE CURRENT DOC
                	currentDoc = IndexTools.getNextDocumentTitle(input);
                	docIdCount++;
                	//System.out.println(currentDoc);
                	docToId.insert(currentDoc,docIdCount);
                } 
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + fileName);
        }
		return docToId;
	}
	
	private void populateMatrix() {
		
		// Loop through links and insert
		for(LinkedListNode<KeyValuePair<Integer,Integer>> n : linkQueue) {
			int from = n.data.key;
			int to = n.data.value;
			
			if(M[to] == null) {
				LinkedList<KeyValuePair<Double,Integer>> newList = new LinkedList<KeyValuePair<Double,Integer>>();
				// Link 1/no of outgoing links and int saying which document matches in P
				newList.insertBack(new KeyValuePair<Double,Integer>(1.0 / docOutgoingLinks.search(from), from));
				M[to] = newList;
			}
			else {
				LinkedList<KeyValuePair<Double,Integer>> currentList = M[to];
				currentList.insertBack(new KeyValuePair<Double,Integer>(1.0 / docOutgoingLinks.search(from), from));
			}
			
		}
	}
	
	public static void createLinkFile(String fileName) {
		
		PageRank pr = new PageRank(LinkedList.class);
		
		for(LinkedListNode<KeyValuePair<Integer, Integer>> kvp : pr.linkQueue) {
			//System.out.println("From: " + kvp.data.key + "  To: " + kvp.data.value);
		}
		
		try {
			Writer w = new FileWriter(fileName);
			
			
			
			int firstFrom = pr.linkQueue.head.data.key;
			w.write(String.valueOf(firstFrom) + " " + pr.docOutgoingLinks.search(firstFrom));
			w.write(System.getProperty( "line.separator" ));
			int prevFrom = firstFrom;
			
			for(LinkedListNode<KeyValuePair<Integer, Integer>> kvp : pr.linkQueue) {
				int from = kvp.data.key;
				int to = kvp.data.value;
				
				if(from != prevFrom && !(from == 0)) {
					w.write(System.getProperty( "line.separator" ));
					w.write("-");
					w.write(System.getProperty( "line.separator" ));
					w.write(String.valueOf(from) + " " + pr.docOutgoingLinks.search(from));
					w.write(System.getProperty( "line.separator" ));
					w.write(String.valueOf(to) + " ");
				}
				else {
					w.write(String.valueOf(to) + " ");
				}
				prevFrom = from;
				
				
				
			}
			
			w.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		
	}
	
	
	public static void main(String[] args) {
		
		PageRank tet = new PageRank(LinkedList.class);

		/*
		for(KeyValuePair<String,Integer> kvp : tet.docToId) {
			System.out.println("Doc: " + kvp.key + "    ID: " + kvp.value);
		}
		*/
		//System.out.println(tet.numberOfSinkNodes);
		
		//createLinkFile("C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\Wiki_links_1KB.txt");
		
		
		//String filename = "C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\writeFileTest.txt";
		//IndexTools.createMatrixFromFile(filename, LinkedList.class, tet.docToId.n);
		/*
		for(int i=0; i<tet.P.length;i++) {
			System.out.println("DOC ID: " + i + "   PAGERANK: " + tet.P[i]);
		}
		*/
		
	}


}
