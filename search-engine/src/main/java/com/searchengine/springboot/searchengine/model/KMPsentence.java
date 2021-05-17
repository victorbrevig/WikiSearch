package com.searchengine.springboot.searchengine.model;

import java.util.ArrayList;
import java.util.List;

public class KMPsentence {

	KMPstate start;
	
	KMPstate visitingState;
	
	KMPstate lastAccepting;
	int strIdCount;
	boolean visitedStartState;
	
	HashTable<String, Integer> stringToId;
	
	
	KMPsentence() {
		strIdCount = 0;
		start = new KMPstate(true, null);
		start.failureLink = start;
		visitingState = start;
		lastAccepting = null;
		visitedStartState = false;
		stringToId = new HashTable(LinkedList.class);
	}
	
	
	
	public void insertSentence(String sentence) {
		String[] strList = sentence.split(" ");
		for(String s : strList) {
			if(stringToId.search(s) == null) {
				stringToId.insert(s, strIdCount);
				strIdCount++;
			}
		}
		insertRec(sentence.split(" "), start, 0);
	}
	
	public void insertRec(String[] sentence, KMPstate currentState, int index) {
		
		if(index == sentence.length) {
			currentState.sentence = String.join(" ", sentence);
			return;
		}

		KMPstate nextState = currentState.nextStates.search(stringToId.search(sentence[index]));
		if(nextState != null) {
			insertRec(sentence, nextState, index+1);
		}
		else {
			KMPstate newState = null;
			for(int i=index; i < sentence.length; i++) {
				String currentWord = sentence[i];
				Integer strId = stringToId.search(currentWord);
				
				newState = new KMPstate(false, findFailureLink(currentWord, currentState));
				currentState.nextStates.insert(strId, newState);
				currentState = newState;
			}
			newState.sentence = String.join(" ", sentence);
		}
		
		
	}
	
	
	private KMPstate findFailureLink(String str, KMPstate prevState) {
		if(prevState.isStartState) {
			// First state after start always points to start
			return start;
		}
		else {
			KMPstate toState = moveForward(str, prevState);
			visitedStartState = false;
			return toState;
		}

		

	}
	
	private KMPstate moveForward(String word, KMPstate fromState) {
		Integer stringId = stringToId.search(word);
		if(stringId != null && fromState.nextStates.search(stringId) != null) {
			if(fromState.isStartState) {
				visitedStartState = true;
			}
			return fromState.nextStates.search(stringId);
		}
		else if (fromState.isStartState) {
			visitedStartState = true;
			return fromState;
		}
		else {
			//System.out.println("Failure link: " + word);
			return moveForward(word, fromState.failureLink);
		}
	}


	/*
	public String processWord(String word) {
		String res = null;
		Integer stringId = stringToId.search(word);
		if(strId == null || visitingState.nextStates.search(strId) == null) {
			visitingState = moveForward(word, visitingState.failureLink);
			
			if(visitedStartState && lastAccepting != null) {
				res = lastAccepting.sentence;
				lastAccepting = null;
			}
			visitedStartState = false;
		}
		else {
			visitingState = visitingState.nextStates.search(strId);
			if(visitingState.sentence != null) {
				lastAccepting = visitingState;
			}
		}
		
		return res;
		
	}
	*/
	
	public String processWord(String word) {
		String res = null;
		
		Integer stringId = stringToId.search(word);
		if(stringId != null && visitingState.nextStates.search(stringId) != null) {
			visitingState = visitingState.nextStates.search(stringId);
			if(visitingState.sentence != null) {
				lastAccepting = visitingState;
			}
		}
		else {
			visitingState = moveForward(word, visitingState.failureLink);
			
			if(visitedStartState && lastAccepting != null) {
				res = lastAccepting.sentence;
				lastAccepting = null;
			}
			visitedStartState = false;
		}
		
		return res;
		
	}
	
	
	public void resetState() {
		visitingState = start;
		lastAccepting = null;
		visitedStartState = false;
	}
	
	public static void main(String[] args) {
		
		KMPsentence kmp = new KMPsentence();
		
		
		kmp.insertSentence("To Be To Be Dense");
		kmp.insertSentence("To Be To Be");
		kmp.insertSentence("To Be");
		kmp.insertSentence("To Be Another Way");
		
		
		/*
		KMPstate c = kmp.start;
		c = c.nextStates.search("To");
		System.out.println(c.failureLink.isStartState);
		c = c.nextStates.search("Be");
		System.out.println(c.failureLink.isStartState);
		//System.out.println(c.sentence);
		c = c.nextStates.search("To");
		System.out.println(c.failureLink.isStartState);
		c = c.nextStates.search("Be");
		System.out.println(c.failureLink.isStartState);
		//System.out.println(c.sentence);
		c = c.nextStates.search("Dense");
		System.out.println(c.failureLink.isStartState);
		*/
		
		
		String res1 = kmp.processWord("To");
		String res2 = kmp.processWord("Be");
		String res3 = kmp.processWord("Ta");
		String res4 = kmp.processWord("Be");
		String res5 = kmp.processWord("To");
		String res6 = kmp.processWord("Be");
		kmp.processWord("Another");
		kmp.processWord("Way");
		String res7 = kmp.processWord("Dense");
		String res8 = kmp.processWord(" ");
		
		
		System.out.println(res1);
		System.out.println(res2);
		System.out.println(res3);
		System.out.println(res4);
		System.out.println(res5);
		System.out.println(res6);

		System.out.println(res7);
		System.out.println(res8);
		
		
		/*

		KMPstate current = kmp.start;
		current = kmp.moveForward("To", current);
		System.out.println(current.id);
		current = kmp.moveForward("Be", current);
		System.out.println(current.id);
		current = kmp.moveForward("To", current);
		System.out.println(current.id);
		//System.out.println("Fail id: " + current.failureLink.id);
		//System.out.println(current.sentence);
		current = kmp.moveForward("Be", current);
		System.out.println(current.id);
		//System.out.println("Fail id: " + current.failureLink.id);
		//System.out.println(current.sentence);
		current = kmp.moveForward("To", current);
		System.out.println(current.id);
		//System.out.println(current.sentence);
		current = kmp.moveForward("Ba", current);
		System.out.println(current.id);
		*/
		
	}
	
	
	
}


class KMPstate implements Comparable<KMPstate>{

	boolean isStartState;
	// is accepting if sentence is not null
	String sentence;
	KMPstate failureLink;
	HashTable<Integer, KMPstate> nextStates;
	

	KMPstate(boolean isStartState, KMPstate failureLink) {
		nextStates = new HashTable(LinkedList.class);
		sentence = null;
		this.failureLink = failureLink;
		this.isStartState = isStartState;
	}
	
	
	
	
	
	@Override
	public int compareTo(KMPstate o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}