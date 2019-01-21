package br.unicamp.ic.lis.ontomatch.filters;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase2;

import info.debatty.java.stringsimilarity.OptimalStringAlignment;


public class OptimalStringAlignmentFilter extends FunctionBase2 {

	public NodeValue exec(NodeValue string1, NodeValue string2) {

		OptimalStringAlignment algorithm = new OptimalStringAlignment();
		
		double i = algorithm.distance(string1.asString().toLowerCase(), string2.asString().toLowerCase());
		
		return NodeValue.makeDouble(1-i);
	}
}