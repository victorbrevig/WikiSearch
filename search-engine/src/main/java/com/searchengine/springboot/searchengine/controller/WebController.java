package com.searchengine.springboot.searchengine.controller;

import java.io.IOException;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.antlr.v4.runtime.*;

import com.searchengine.springboot.searchengine.model.ArrayOfInts;
import com.searchengine.springboot.searchengine.model.DocPresentation;
import com.searchengine.springboot.searchengine.model.HashTable;
import com.searchengine.springboot.searchengine.model.Index6;
import com.searchengine.springboot.searchengine.model.Index7;
import com.searchengine.springboot.searchengine.model.IndexTools;
import com.searchengine.springboot.searchengine.model.KeyValuePair;
import com.searchengine.springboot.searchengine.model.LinkedList;
import com.searchengine.springboot.searchengine.parser.EvalVisitor;
import com.searchengine.springboot.searchengine.parser.ExpLexer;
import com.searchengine.springboot.searchengine.parser.ExpParser;

@Controller
public class WebController {
	
	Index7 index = new Index7("C:\\Users\\victo\\eclipse-workspace\\search-engine\\src\\main\\resources\\Wiki_10MB.txt");
	StopWatch watch = new StopWatch();
	CharStream in;
	ExpLexer lexer;
	ExpParser parser;
	ParseTree tree;
	EvalVisitor visitor;
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	
	@PostMapping("/search")
	public String Search(HttpServletRequest request, HttpServletResponse response,Model model) throws IOException { 

		String searchWord = request.getParameter("searchword");  

		
		// PARSE INPUT
		in = CharStreams.fromString(searchWord);
	    lexer = new ExpLexer(in);
	    parser = new ExpParser(new CommonTokenStream(lexer));
	    tree = parser.parse();
	    visitor = new EvalVisitor(index);
	    
		
	    // Take time for search
	    watch.start();
	    // WALK PARSE TREE AND SEARCH
	    HashTable<Integer,HashTable<Integer,Integer>> docTable = visitor.visit(tree);
	    watch.stop();
	    Long searchTime = watch.getTime(TimeUnit.MICROSECONDS);
	    String searchTimeString = searchTime.toString();
	    watch.reset();
	    
	    model.addAttribute("searchWord", searchWord);
	    if(docTable == null) {
	    	return "index";
	    }
	    else if(docTable.n != 0) {
	    	
	    	int docsWordAppearsIn = docTable.n;
	    	Double totRankScore = 0.0;
	    	
	    	List<DocPresentation> docs = new ArrayList<DocPresentation>();
	    	for(KeyValuePair<Integer,HashTable<Integer,Integer>> kvp : docTable) {
	    		
	    		// Get a single position as starting position
	    		int startingPos = 0;
	    		int endingPos = 0;
	    		for(KeyValuePair<Integer,Integer> pos : kvp.value) {
	    			startingPos = pos.key;
	    			endingPos = pos.value;
	    			break;
	    		}
	    		
	    		ArrayOfInts docText = index.docIdToTextArray.search(kvp.key);

	    		String currentString = index.idToOrigWord.search(docText.get(startingPos));
	    		String str = createContextString(startingPos, endingPos, docText, currentString);
	    		
	    		DocPresentation doc;
	    		
	    		double pageRankScore = index.P[kvp.key];
	    		
	    		if(!searchWord.contains(" ")) {
	    			// COMPUTE RANK
		    		int termFreq = kvp.value.n;
		    		double termRank = ((double) termFreq) / docsWordAppearsIn;
		    		
		    		termRank *= pageRankScore;
		    		
		    		totRankScore += termRank;
		    		
		    		doc = new DocPresentation(index.idToDoc.search(kvp.key), kvp.key, str, termRank);
		    		
		    		if(index.idToDoc.search(kvp.key).equals("Alphabet")) {
		    			System.out.println("PAGERANK SCORE: " + pageRankScore);
		    			System.out.println("TERM RANK: " + ((double) termFreq) / docsWordAppearsIn);
		    		}
		    		
	    		}
	    		else {
	    			totRankScore += pageRankScore;
	    			doc = new DocPresentation(index.idToDoc.search(kvp.key), kvp.key, str, pageRankScore);
	    		}
	    		
	    		docs.add(doc);
	    	}
	    	// Sort docs after rank
	    	Collections.sort(docs, Collections. reverseOrder());
	    	
		    model.addAttribute("docs", docs);
		    model.addAttribute("searchTime", searchTimeString);
		    model.addAttribute("amountResults", docTable.n);
		    model.addAttribute("totRankScore", totRankScore);
		    return "results";
	    }
	    else {
	    	return "resultsNotFound";
	    }

	}


	private String createContextString(int startingPos, int endingPos, ArrayOfInts docText,
			String currentString) {
		// This method creates the context text around the search term that is shown in the GUI
		StringBuilder str = new StringBuilder();

		int currentPos = startingPos;
		// Go back to beginning of sentence
		
		while(currentString.charAt(currentString.length()-1) != '.' || (currentPos == startingPos)) {
			if(currentPos == 0) {
				currentPos--;
				break;
			}
			currentPos--;
			currentString = index.idToOrigWord.search(docText.get(currentPos));	
		}
 		
		currentPos++;
		currentString = index.idToOrigWord.search(docText.get(currentPos));
		
		if(currentPos == startingPos) {
			str.append("<mark>");
		}
		str.append(currentString);	
  	
		while((currentString.charAt(currentString.length()-1) != '.' || currentPos < endingPos) && currentPos < docText.size()-1) {
			
			if(currentPos == endingPos) {
				str.append("</mark>");
			}	
			
			currentPos++;

			currentString = index.idToOrigWord.search(docText.get(currentPos));
			str.append(" ");
			if(currentPos == startingPos) {
				str.append("<mark>");
			}
			str.append(currentString);
		}
		return str.toString();
	}
	
	
	@GetMapping("/results")
	public String search() {
		return "results";
	}
	
	@RequestMapping(value="/searchAutoComplete")
	@ResponseBody
	public List<String> searchAutoComplete(@RequestParam(value="term", required = false, defaultValue="") String term) {

		List<String> tempStrings = index.trie.prefixTopK(term, 5);
		List<String> suggestions = new ArrayList<String>();
		
		for(String str : tempStrings) {
			int endPos = str.length()-1;
			while(IndexTools.badEndPosSymbol(str, endPos)) {
				endPos--;
			}
			str = str.substring(0,endPos+1);
			suggestions.add(str);
		}
		return suggestions;
	}
	
	@GetMapping("/search/{docID}")
	public String docPage(@PathVariable("docID") int docID,Model model) {
		
		// Build text
		StringBuilder text = new StringBuilder();
		ArrayOfInts strArray = index.docIdToTextArray.search(docID);
		for(int i = 0; i < strArray.size(); i++) {
			text.append(" " + index.idToOrigWord.search(strArray.get(i)));
		}
		model.addAttribute("docTitle", index.idToDoc.search(docID));
		model.addAttribute("text", text.toString());
		return "page";
	}
	
	
	/*
	private List<DocPresentation> computeRanks(String searchStr, ) {
		List<DocPresentation> res = new ArrayList<DocPresentation>();
		if(!searchStr.contains(" ")) {
			// For now only compute ranks on single word queries
			
		}
		else {
			
		}
		
		
		return null;
	}
	*/
	

}