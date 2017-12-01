package br.unicamp.ic.lis.ontomatch.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Resource {
	private String uri;
	private String label;
	private double similarity;
	
	private List<String> matchedWords = new ArrayList<>();
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public double getSimilarity() {
		return similarity;
	}
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	@Override
	public String toString() {
		return "Resource [uri=" + uri + " | similarity=" + similarity + " | label=" + label + "]";
	}
	public List<String> getMatchedWords() {
		return matchedWords;
	}
	public void setMatchedWords(List<String> matchedWords) {
		this.matchedWords = matchedWords;
	}
}
