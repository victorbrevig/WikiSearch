package com.searchengine.springboot.searchengine.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.searchengine.springboot.searchengine.model.HashTable;
import com.searchengine.springboot.searchengine.model.Index3;
import com.searchengine.springboot.searchengine.model.Index4;
import com.searchengine.springboot.searchengine.model.Index5;
import com.searchengine.springboot.searchengine.model.Index6;
import com.searchengine.springboot.searchengine.model.Index7;
import com.searchengine.springboot.searchengine.model.LinkedList;

public class Tester {


	
	public static void main(String[] args) {
		CharStream in;
		ExpLexer lexer;
		ExpParser parser;
		ParseTree tree;
		EvalVisitor visitor;
		Index7 index = new Index7("C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\Wiki_50MB.txt");
		
		//String searchWord = "\"to be\" OR milk";
		//String searchWord = "\"neural development characterized\"";
		String searchWord = "\"ac tius\"";
		
		in = CharStreams.fromString(searchWord);
	    lexer = new ExpLexer(in);
	    parser = new ExpParser(new CommonTokenStream(lexer));
	    tree = parser.parse();
	    visitor = new EvalVisitor(index);
	    
	    HashTable<Integer,HashTable<Integer,Integer>> docs = visitor.visit(tree);
	    
	    docs.printElements();
	    
	    System.out.println(index.idToOrigWord.n);
	    
	}
	
}
