package br.unicamp.ic.lis.ontomatch.filters;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase2;

import info.debatty.java.stringsimilarity.Levenshtein;


public class LevenshteinFilter extends FunctionBase2 {

	public NodeValue exec(NodeValue string1, NodeValue string2) {
		Levenshtein algorithm = new Levenshtein();
		
		double i = algorithm.distance(string1.asString().toLowerCase(), string2.asString().toLowerCase());
		
		return NodeValue.makeDouble(i);
	}
}