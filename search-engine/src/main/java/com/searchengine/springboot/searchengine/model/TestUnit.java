package com.searchengine.springboot.searchengine.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

public class TestUnit {
	StopWatch watch = new StopWatch();
	/*
	String[] files = 
		{"C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\Wiki_100KB.txt",
		 "C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\Wiki_1MB.txt",
		 "C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\Wiki_2MB.txt",
		 "C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\Wiki_5MB.txt",
		 "C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\Wiki_10MB.txt"};
	*/
	String[] files = {"C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\Wiki_1MB.txt"};
	
	static String commonWordFilePath = "C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\1-1000_mostCommonWords.txt";
	
	int numFiles = files.length;
	
	public long[] preprocessTest() {
		
		long[] runTimes = new long[numFiles];
		
		for(int i=0; i < numFiles; i++) {
			watch.start();
			Index1 index = new Index1(files[i]);
			watch.stop();
			Long preprocessingTime = watch.getTime(TimeUnit.MILLISECONDS);
			runTimes[i] = preprocessingTime;
			watch.reset();
		}
		
		return runTimes;
		
	}
	
	public void queryTest() {
		Long[] preProcessingTimes = new Long[numFiles];
		Long[] queryTimes = new Long[numFiles];
		String word;
		Scanner input;
		for(int i=0; i < numFiles; i++) {
			
			try {
				input = new Scanner(new File(commonWordFilePath), "UTF-8");
				
				watch.start();
				Index2 index = new Index2(files[i]);
				watch.stop();
				preProcessingTimes[i] = watch.getTime(TimeUnit.MILLISECONDS);
				watch.reset();

				watch.start();
				while(input.hasNext()) {
					word = input.next();
					index.search(word);
				}
				watch.stop();
				Long queryTime = watch.getTime(TimeUnit.MILLISECONDS);
				queryTimes[i] = queryTime;
				watch.reset();
				
				for(Long l : preProcessingTimes) {
					System.out.println("Pre-process: " + l);
				}
				for(Long l : queryTimes) {
					System.out.println("Query: " + l);
				}
				
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	
	public void queryTestWorstCase() {
		Long[] preProcessingTimes = new Long[numFiles];
		Long[] queryTimes = new Long[numFiles];

		for(int i=0; i < numFiles; i++) {
			
			watch.start();
			Index2 index = new Index2(files[i]);
			watch.stop();
			preProcessingTimes[i] = watch.getTime(TimeUnit.MILLISECONDS);
			watch.reset();

			watch.start();
			for(int j = 0; j < 1000; j++) {
				// "" does not exist
				index.search("");
			}
			watch.stop();
			Long queryTime = watch.getTime(TimeUnit.MILLISECONDS);
			queryTimes[i] = queryTime / 100;
			watch.reset();
			
			for(Long l : preProcessingTimes) {
				System.out.println("Pre-process: " + l);
			}
			for(Long l : queryTimes) {
				System.out.println("Query: " + l);
			}
		}
		
	}
	
	public void skipListTest(int numberOfInserts) {
		Scanner input;
		try {
			input = new Scanner(new File(commonWordFilePath), "UTF-8");
			Random rand = new Random();
			rand.setSeed(1);
			for(int i=0; i<10000; i++) {
				continue;
			}
			int[] numOfInserts = {2000,2000,2000,1000,1000,1000,4000,8000,16000,32000,64000};
			for(int ins : numOfInserts) {
				SkipList<Integer> SL = new SkipList<Integer>(0.5);
				
				for(int i=0;i < ins; i++) {
					SL.insert(rand.nextInt(ins));
				}
				watch.start();
				for(int i=0; i<100;i++) {
					SL.insert(rand.nextInt(ins));
				}
				watch.stop();
				System.out.println(ins + " insertions: " + watch.getTime(TimeUnit.NANOSECONDS));
				watch.reset();
				
				watch.start();
				for(int i=0;i < 100; i++) { 
					SL.search(rand.nextInt(1000));
				}
				watch.stop();
				System.out.println(ins + " searchs: " + watch.getTime(TimeUnit.NANOSECONDS));
				watch.reset();
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void testSkipListIntersection() {
		SkipListArray<Integer> SL1 = new SkipListArray<Integer>(0.5);
		SkipListArray<Integer> SL2 = new SkipListArray<Integer>(0.5);

		
		Index3 index = new Index3(files[0]);
		
		LinkedList<Integer> LL1 = index.searchWord("to");
		LinkedList<Integer> LL2 = index.searchWord("become");
		
		System.out.println("to length: " + LL1.size);
		System.out.println("the length: " + LL2.size);
		

		LinkedListNode<Integer> current1 = LL1.head;
		while(current1 != null) {
			SL1.insertFront(current1.data);
			current1 = current1.next;
		}
		LinkedListNode<Integer> current2 = LL2.head;
		while(current2 != null) {
			SL2.insertFront(current2.data);
			current2 = current2.next;
		}
		/*
		ArrayListC<DataArrayListPair<Integer>> current = SL1.list.head.data;
		
		while(current.get(0).next != null) {
			System.out.println(current.get(0).data);
			current = current.get(0).next;
		}
		*/
		
		// Standard linked list intersection time
		watch.start();
		LinkedList<Integer> res1 = LL1.intersection(LL2);
		watch.stop();
		System.out.println("Standard LL: " + watch.getTime(TimeUnit.NANOSECONDS));
		watch.reset();
		// Skip list intersection time
		watch.start();
		LinkedList<Integer> res2 = SL1.intersection(SL2);
		watch.stop();
		System.out.println("Skip LL: " + watch.getTime(TimeUnit.NANOSECONDS));
		watch.reset();
		
		
	}
	
	public static void main(String[] args) {
		
		TestUnit testUnit = new TestUnit();
		
		testUnit.testSkipListIntersection();
		
		/*
		long[] results = testUnit.preprocessTest();
		
		for(long l : results) {
			System.out.println(l);
		}
		*/
		
		//testUnit.skipListTest(1000);
		/*
		SkipList<Integer> SL = new SkipList<Integer>(0.5);
		Random rand = new Random();
		rand.setSeed(1);
		Scanner console = new Scanner(System.in);
		int n = 4000;
		for(int i=0;i < (n/2); i++) {
			SL.insert(rand.nextInt(n));
		}
		StopWatch watch = new StopWatch();
		while(true) {
			String searchstr = console.nextLine();
			int searchNr = Integer.parseInt(searchstr);
			long sum = 0;
			for(int i=0; i<10000; i++) {
				watch.start();
				SkipListNode<Integer> res = SL.search(searchNr);
				watch.stop();
				if(res != null) {
					//System.out.println("Found");
				}
				long timeRec = watch.getTime(TimeUnit.NANOSECONDS);
				watch.reset();
				//System.out.println(timeRec);
				sum += timeRec;
				
			}
			System.out.println("AVG: " + sum/10000);
		}
		*/
		
		
	}
	
}
