package com.searchengine.springboot.searchengine.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

public class PruningTrieTester {
	
	
	public static void main(String[] args) {
		
		StopWatch watch = new StopWatch();
		
		Index6 index = new Index6("C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\Wiki_50MB.txt");
		int res1Size = 0;
		
		int numTimes = 100;
		
		String prefixStr = "a";
		
		Long[] timesNoPruning = new Long[numTimes];
		Long[] timesPruning = new Long[numTimes];
		Long[] timesNoPruningOnlyRet = new Long[numTimes];
		List<String> res1 = new ArrayList<String>();
		for(int i = 0; i < numTimes; i++) {
			watch.start();
			List<KeyValuePair<String,Integer>> resFind1 = index.trie.prefixFindList(prefixStr);
			Collections.sort(resFind1, Collections.reverseOrder());
			res1 = new ArrayList<String>();
			for(int j = 0; j < 10; j++) {
				if(j >= resFind1.size()-1) {
					break;
				}
				res1.add(resFind1.get(j).key);
			}
			watch.stop();
			Long time1 = watch.getTime(TimeUnit.NANOSECONDS);
			watch.reset();
			timesNoPruning[i] = time1;
			res1Size = resFind1.size();
			
			
			watch.start();
			List<String> res2 = index.trie.prefixTopK(prefixStr, 10);
			watch.stop();
			Long time2 = watch.getTime(TimeUnit.NANOSECONDS);
			watch.reset();
			timesPruning[i] = time2;
			
			watch.start();
			List<KeyValuePair<String,Integer>> res3 = index.trie.prefixFindList(prefixStr);
			watch.stop();
			Long time3 = watch.getTime(TimeUnit.NANOSECONDS);
			watch.reset();
			timesNoPruningOnlyRet[i] = time3;
		}
		
		
		System.out.println("No pruning: ");
		for(int i = 0; i < numTimes; i++) {
			System.out.println(timesNoPruning[i]);
		}
		System.out.println("With pruning: ");
		for(int i = 0; i < numTimes; i++) {
			System.out.println(timesPruning[i]);
		}
		System.out.println("No pruning only ret: ");
		for(int i = 0; i < numTimes; i++) {
			System.out.println(timesNoPruningOnlyRet[i]);
		}
		System.out.println("w: " + index.origWordToId.n);
		System.out.println("res1 size: " + res1Size);
		
		
		
		/*
		System.out.println("NO PRUNING: ");
		for(String s : res1) {
			System.out.println(s);
		}
		
		System.out.println("WITH PRUNING: ");
		for(String s : res2) {
			System.out.println(s);
		}
		*/
		
		
	}
	
	
	
}
