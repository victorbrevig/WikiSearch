package com.searchengine.springboot.searchengine.model;

import java.util.ArrayList;
import java.util.List;

public class SentenceNFA {

	NFAstate start;
	
	NFAstate visitingState;
	
	NFAstate lastAccepting;
	
	SentenceNFA() {
		start = new NFAstate(false);
		visitingState = start;
		lastAccepting = null;
	}
	
	
	
	public void insertSentence(String sentence) {
		insertRec(sentence.split(" "), start, 0);
	}
	
	public void insertRec(String[] sentence, NFAstate currentState, int index) {
		
		if(index == sentence.length) {
			currentState.accepting = true;
			currentState.sentence = String.join(" ", sentence);
			return;
		}
		
		NFAstate nextState = currentState.nextStates.search(sentence[index]);
		if(nextState != null) {
			insertRec(sentence, nextState, index+1);
		}
		else {
			NFAstate newState = null;
			for(int i=index; i < sentence.length; i++) {
				newState = new NFAstate(false);
				currentState.nextStates.insert(sentence[i], newState);
				currentState = newState;
			}
			newState.accepting = true;
			newState.sentence = String.join(" ", sentence);
		}
		
		
	}
	
	
	public String processWord(String word) {
		String res = null;
		NFAstate newState = visitingState.nextStates.search(word);
		
		if(newState != null) {
			visitingState = newState;
			if(visitingState.accepting) {
				lastAccepting = visitingState;
			}
		}
		else {
			visitingState = start;
			if(lastAccepting != null) {
				res = lastAccepting.sentence;
			}
		}
		
		return res;
		
	}
	
	
	public void reset() {
		visitingState = start;
		lastAccepting = null;
	}
	
	public static void main(String[] args) {
		
		SentenceNFA nfa = new SentenceNFA();
		
		
		nfa.insertSentence("Alpha Centauri");
		nfa.insertSentence("Alpha");
		
		String res = nfa.processWord("Alpha");
		
		
		
		String res2 = nfa.processWord("Centauri");
		String res3 = nfa.processWord("test");
		System.out.println(res3);
		
	}
	
	
	
}


class NFAstate implements Comparable<NFAstate>{
	
	
	boolean accepting;
	String sentence;
	
	HashTable<String, NFAstate> nextStates;


	NFAstate(boolean accepting) {
		this.accepting = accepting;
		nextStates = new HashTable(LinkedList.class);
		sentence = null;
	}
	
	
	
	
	
	@Override
	public int compareTo(NFAstate o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}