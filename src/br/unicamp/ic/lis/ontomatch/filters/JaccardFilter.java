package br.unicamp.ic.lis.ontomatch.filters;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;

import info.debatty.java.stringsimilarity.Jaccard;


public class JaccardFilter extends FunctionBase2 {

	public NodeValue exec(NodeValue string1, NodeValue string2) {

		Jaccard algorithm = new Jaccard();
		
		double i = algorithm.distance(string1.asString().toLowerCase(), string2.asString().toLowerCase());
		
		return NodeValue.makeDouble(1-i);
	}
}