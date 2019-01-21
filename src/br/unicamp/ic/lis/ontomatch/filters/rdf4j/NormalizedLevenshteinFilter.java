package br.unicamp.ic.lis.ontomatch.filters.rdf4j;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.algebra.evaluation.ValueExprEvaluationException;
import org.eclipse.rdf4j.query.algebra.evaluation.function.Function;

import info.debatty.java.stringsimilarity.NormalizedLevenshtein;

public class NormalizedLevenshteinFilter implements Function {
	// define a constant for the namespace of our custom function
	public static final String NAMESPACE = "http://lis.ic.unicamp.br/similarityfunction/";
	
	
	@Override
	public String getURI() {
	    return NAMESPACE + "NormalizedLevenshteinFilter";
	}

	@Override
	public Value evaluate(ValueFactory valueFactory, Value... args) throws ValueExprEvaluationException {
		System.out.println("Normalized");
		NormalizedLevenshtein algorithm = new NormalizedLevenshtein();
		Literal string1 = (Literal) args[0];
		Literal string2 = (Literal) args[1];

		double i = algorithm.distance(string1.getLabel().toLowerCase(), string2.getLabel().toLowerCase());

		return valueFactory.createLiteral(i);
	}

}
