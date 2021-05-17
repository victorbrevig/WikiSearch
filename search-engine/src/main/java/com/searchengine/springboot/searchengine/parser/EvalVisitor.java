package com.searchengine.springboot.searchengine.parser;
import java.util.ArrayList;
import java.util.Collections;

import org.antlr.v4.runtime.tree.ParseTree;

import com.searchengine.springboot.searchengine.model.HashTable;
import com.searchengine.springboot.searchengine.model.Index7;
import com.searchengine.springboot.searchengine.model.IndexTools;
import com.searchengine.springboot.searchengine.model.KeyValuePair;
import com.searchengine.springboot.searchengine.model.LinkedList;
import com.searchengine.springboot.searchengine.model.Pair;
import com.searchengine.springboot.searchengine.model.YarrayResult;
import com.searchengine.springboot.searchengine.parser.ExpParser.AND_EXPRContext;
import com.searchengine.springboot.searchengine.parser.ExpParser.ExpressionContext;
import com.searchengine.springboot.searchengine.parser.ExpParser.OR_EXPRContext;

//import com.searchengine.springboot.searchengine.model.Index4;

public class EvalVisitor extends ExpBaseVisitor<HashTable<Integer,HashTable<Integer,Integer>>> {

	public Index7 index;
	
	public EvalVisitor(Index7 index) {
		super();
		this.index = index;
	}
	
	
	@Override public HashTable<Integer,HashTable<Integer,Integer>> visitParse(ExpParser.ParseContext ctx) { 
		return visit(ctx.expression());
	}
	
	
	@Override
	public HashTable<Integer,HashTable<Integer,Integer>> visitID_EXPR(ExpParser.ID_EXPRContext ctx) {
		HashTable<Integer,HashTable<Integer,Integer>> res = index.searchWord(ctx.getText());
		if(res == null) {
			return new HashTable(LinkedList.class);
		}
		else {
			return res;
		}
	}
	
	
	@Override 
	public HashTable<Integer,HashTable<Integer,Integer>> visitOR_EXPR(ExpParser.OR_EXPRContext ctx) {
		
		ParseTree leftChild = ctx.expression(0).getChild(1);

		//System.out.println(ctx.expression(0).getText());
		//System.out.println(ctx.expression(1).getText());
		
		if(leftChild != null && leftChild.toString().equals("OR")) {
			// If left child is OR too (chained ORs)
			HashTable<Integer,HashTable<Integer,Integer>> right = visit(ctx.expression(1));
			ArrayList<HashTable<Integer,HashTable<Integer,Integer>>> list = new ArrayList<HashTable<Integer,HashTable<Integer,Integer>>>();
			list.add(right);
			return evaluateLeftOR((OR_EXPRContext) ctx.expression(0),list);
		}
		else {
			ExpressionContext exp = ctx.expression(0);
			HashTable<Integer,HashTable<Integer,Integer>> left = visit(ctx.expression(0));
			HashTable<Integer,HashTable<Integer,Integer>> right = visit(ctx.expression(1));
			if(left.n > right.n) {
				return left.union(right);
			}
			else {
				return right.union(left);
			}
		}
		
	}
	
	
	@Override 
	public HashTable<Integer,HashTable<Integer,Integer>> visitAND_EXPR(ExpParser.AND_EXPRContext ctx) { 
		ParseTree leftChild = ctx.expression(0).getChild(1);

		//System.out.println(ctx.expression(0).getText());
		//System.out.println(ctx.expression(1).getText());
		
		if(leftChild != null && leftChild.toString().equals("AND")) {
			// If left child is AND too (chained ANDs)
			HashTable<Integer,HashTable<Integer,Integer>> right = visit(ctx.expression(1));
			ArrayList<HashTable<Integer,HashTable<Integer,Integer>>> list = new ArrayList<HashTable<Integer,HashTable<Integer,Integer>>>();
			list.add(right);
			return evaluateLeftAND((AND_EXPRContext) ctx.expression(0),list);
		}
		else {
			ExpressionContext exp = ctx.expression(0);
			HashTable<Integer,HashTable<Integer,Integer>> left = visit(ctx.expression(0));
			HashTable<Integer,HashTable<Integer,Integer>> right = visit(ctx.expression(1));
			
			return left.intersection(right); 
		}
	}
	
	@Override public HashTable<Integer,HashTable<Integer,Integer>> visitEXPR(ExpParser.EXPRContext ctx) { 
		return visit(ctx.expression()); 
	}
	
	
	@Override 
	public HashTable<Integer,HashTable<Integer,Integer>> visitPHRASE_EXPR(ExpParser.PHRASE_EXPRContext ctx) {
		String phrase = ctx.getText();
		phrase = phrase.substring(1, phrase.length() - 1);
		return IndexTools.phraseSearch(index,phrase);
	}
	
	
	@Override
	public HashTable<Integer,HashTable<Integer,Integer>> visitAND_NOT_EXPR(ExpParser.AND_NOT_EXPRContext ctx) { 
		
		HashTable<Integer,HashTable<Integer,Integer>> left = visit(ctx.expression(0));
		HashTable<Integer,HashTable<Integer,Integer>> right = visit(ctx.expression(1));

		return left.difference(right); 
		
	}
	
	public HashTable<Integer,HashTable<Integer,Integer>> evaluateLeftOR(ExpParser.OR_EXPRContext ctx, ArrayList<HashTable<Integer,HashTable<Integer,Integer>>> list) {
		
		ParseTree leftChild = ctx.expression(0).getChild(1);
		
		if(leftChild != null && leftChild.toString().equals("OR")) {
			HashTable<Integer,HashTable<Integer,Integer>> right = visit(ctx.expression(1));
			list.add(right);
			return evaluateLeftOR((OR_EXPRContext) ctx.expression(0),list);
		}
		else {
			HashTable<Integer,HashTable<Integer,Integer>> left = visit(ctx.expression(0));
			HashTable<Integer,HashTable<Integer,Integer>> right = visit(ctx.expression(1));
			list.add(left);
			list.add(right);
			Collections.sort(list);
			HashTable<Integer,HashTable<Integer,Integer>> res = list.get(list.size()-1);
			for(int i = 0; i < list.size()-1; i++) {
				res = res.union(list.get(i));
			}
			return res;
		}
	}
	
	
	public HashTable<Integer,HashTable<Integer,Integer>> evaluateLeftAND(ExpParser.AND_EXPRContext ctx, ArrayList<HashTable<Integer,HashTable<Integer,Integer>>> list) {
		
		ParseTree leftChild = ctx.expression(0).getChild(1);
		
		if(leftChild != null && leftChild.toString().equals("AND")) {
			HashTable<Integer,HashTable<Integer,Integer>> right = visit(ctx.expression(1));
			list.add(right);
			return evaluateLeftAND((AND_EXPRContext) ctx.expression(0),list);
		}
		else {
			HashTable<Integer,HashTable<Integer,Integer>> left = visit(ctx.expression(0));
			HashTable<Integer,HashTable<Integer,Integer>> right = visit(ctx.expression(1));
			list.add(left);
			list.add(right);
			Collections.sort(list);
			HashTable<Integer,HashTable<Integer,Integer>> res = list.get(0);
			for(int i = 1; i < list.size(); i++) {
				res = res.intersection(list.get(i));
			}
			return res;
		}
	}
	

	@Override 
	public HashTable<Integer,HashTable<Integer,Integer>> visitNEAR_EXPR(ExpParser.NEAR_EXPRContext ctx) { 

		int radius = Integer.parseInt(ctx.getChild(2).toString());
		System.out.println("RADIUS = " + radius);
		// visit id expr

		
		return new HashTable(LinkedList.class);
	}
	
	
	@Override
	public HashTable<Integer,HashTable<Integer,Integer>> visitMID_EXPR(ExpParser.MID_EXPRContext ctx) { 

		HashTable<Integer,HashTable<Integer,Integer>> res = new HashTable(LinkedList.class);
		
		StringBuilder rightStr = new StringBuilder(ctx.getChild(2).getText());
		rightStr.reverse();
		String left = ctx.getChild(0).getText();
		String right = rightStr.toString();
		
		Pair<Integer,Integer> xRange = index.trie.getRange(left);
		Pair<Integer,Integer> yRange = index.trieR.getRange(right);
		
		
		Pair<ArrayList<Integer>, ArrayList<YarrayResult>> resImplicit = index.rangeTree2D.rangeSearch2D(xRange.first, xRange.second, yRange.first, yRange.second);
		ArrayList<Integer> resExplicit = index.rangeTree2D.getExplicitResult(resImplicit, index.idToRevWord.search(yRange.first));
		

		ArrayList<HashTable<Integer,HashTable<Integer,Integer>>> list = new ArrayList<HashTable<Integer,HashTable<Integer,Integer>>>();

		for(Integer wordId : resExplicit) {
			String str = index.idToWord.search(wordId);

			list.add(index.searchWord(str));
		}
		// Sort and union
		Collections.sort(list);
		if(list.size() > 0) {
			res = list.get(0);
			for(int i = 1; i < list.size(); i++) {
				res = res.union(list.get(i));
			}
		}
		return res;
	}
	
	@Override
	public HashTable<Integer,HashTable<Integer,Integer>> visitPREFIX_EXPR(ExpParser.PREFIX_EXPRContext ctx) { 
		HashTable<Integer,HashTable<Integer,Integer>> res = new HashTable(LinkedList.class);
		HashTable<String,String> strings = index.trie.prefixFind(ctx.getChild(0).getText());
		
		
		ArrayList<HashTable<Integer,HashTable<Integer,Integer>>> list = new ArrayList<HashTable<Integer,HashTable<Integer,Integer>>>();
		for(KeyValuePair<String,String> kvp : strings) {
			//System.out.println(kvp.value);
			list.add(index.searchWord(kvp.value.substring(0,kvp.value.length()-1)));
		}
		// Sort and union
		if(list.size() > 0) {
			res = list.get(0);
			for(int i = 1; i < list.size(); i++) {
				res = res.union(list.get(i));
			}
		}
		return res;
	}
	
	@Override
	public HashTable<Integer,HashTable<Integer,Integer>> visitSUFFIX_EXPR(ExpParser.SUFFIX_EXPRContext ctx) { 
		HashTable<Integer,HashTable<Integer,Integer>> res = new HashTable(LinkedList.class);
		StringBuilder searchStr = new StringBuilder(ctx.getChild(1).getText());
		searchStr.reverse();

		HashTable<String,String> strings = index.trieR.prefixFind(searchStr.toString());
		
		ArrayList<HashTable<Integer,HashTable<Integer,Integer>>> list = new ArrayList<HashTable<Integer,HashTable<Integer,Integer>>>();
		for(KeyValuePair<String,String> kvp : strings) {
			StringBuilder str = new StringBuilder(kvp.value);
			str.reverse();
			str.deleteCharAt(0);
			list.add(index.searchWord(str.toString()));
		}
		// Sort and union
		Collections.sort(list);
		if(list.size() > 0) {
			res = list.get(0);
			for(int i = 1; i < list.size(); i++) {
				res = res.union(list.get(i));
			}
		}
		
		return res;
	}
}
