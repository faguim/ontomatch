package br.unicamp.ic.lis.ontomatch.test;

import info.debatty.java.stringsimilarity.NormalizedLevenshtein;

public class Testezim {
	public static void main(String[] args) {

		// StringMetric metric = with(new
		// CosineSimilarity<String>()).tokenize(Tokenizers.whitespace()).build();
		// Cosine l = new Cosine();
		NormalizedLevenshtein l = new NormalizedLevenshtein();

		System.out.println(1 - l.distance("Pulse deficit", "Pulse rate"));
		System.out.println(1 - l.distance("Pulses deficit", "Pulse kkk"));
		System.out.println(1 - l.distance("My string", "My String"));
	}
}
