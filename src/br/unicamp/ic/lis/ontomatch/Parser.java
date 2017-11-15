package br.unicamp.ic.lis.ontomatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//import edu.stanford.nlp.ling.CoreAnnotations;
//import edu.stanford.nlp.pipeline.Annotation;
//import edu.stanford.nlp.pipeline.StanfordCoreNLP;
//import edu.stanford.nlp.semgraph.SemanticGraph;
//import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
//import edu.stanford.nlp.util.CoreMap;

public class Parser {
	
	public static List<String> getDependencyTree(String sentence){
//		Properties props = new Properties();
//		props.put("annotators", "tokenize, ssplit, pos, lemma,ner, parse, dcoref");
//		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);	
//				
//		
//		Annotation document = new Annotation(sentence);
//		pipeline.annotate(document);
//		
//		List<CoreMap> processedSentences = document.get(CoreAnnotations.SentencesAnnotation.class);
//		
//		for (CoreMap processedSentence : processedSentences) {
//			SemanticGraph semanticGraph = processedSentence.get(SemanticGraphCoreAnnotations.AlternativeDependenciesAnnotation.class);			
//			System.out.println(semanticGraph);
//		}
		List<String> terms = new ArrayList<String>();
		return terms;
	}
	
	public static void main(String[] args) {
		getDependencyTree("History of Hypertension");
	}
}
