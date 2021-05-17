package com.searchengine.springboot.searchengine.model;

public class DocPresentation implements Comparable<DocPresentation> {

	public String title;
	public String context;
	public Double rankScore;
	public int id;
	
	public DocPresentation(String title, int id, String context, Double rankScore) {
		this.title = title;
		this.context = context;
		this.rankScore = rankScore;
		this.id = id;
	}

	@Override
	public int compareTo(DocPresentation o) {
		// For sorting on rank
		return ((Double) rankScore).compareTo(o.rankScore);
	}
	
}
