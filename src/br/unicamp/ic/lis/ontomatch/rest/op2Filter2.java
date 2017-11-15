package br.unicamp.ic.lis.ontomatch.rest;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;

import info.debatty.java.stringsimilarity.Cosine;
import info.debatty.java.stringsimilarity.Levenshtein;



public class op2Filter2 extends FunctionBase2 {

	public NodeValue exec(NodeValue value1, NodeValue value2) {
//		StringMetric metric = StringMetrics.cosineSimilarity();
		Cosine l = new Cosine();
		
		double i = l.distance(value1.asString().split("\\^\\^")[0], value2.asString());
		return NodeValue.makeDouble(i);
	}
}