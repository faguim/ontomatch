package br.unicamp.ic.lis.ontomatch.rest;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.CosineSimilarity;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;

import static org.simmetrics.builders.StringMetricBuilder.with;

import java.util.Locale;

public class LevenshteinFilter extends FunctionBase2 {

	public NodeValue exec(NodeValue value1, NodeValue value2) {
		StringMetric metric =
				with(new CosineSimilarity<String>())
				.simplify(Simplifiers.toLowerCase(Locale.ENGLISH))
				.simplify(Simplifiers.replaceNonWord())
				.tokenize(Tokenizers.whitespace())
				.build();
		
		float i = metric.compare(value1.asString().split("\\^\\^")[0], value2.asString());
		return NodeValue.makeFloat(i);
	}
}
	