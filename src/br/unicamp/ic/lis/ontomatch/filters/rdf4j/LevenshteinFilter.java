package br.unicamp.ic.lis.ontomatch.filters.rdf4j;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.algebra.evaluation.ValueExprEvaluationException;
import org.eclipse.rdf4j.query.algebra.evaluation.function.Function;

import info.debatty.java.stringsimilarity.Levenshtein;

public class LevenshteinFilter implements Function{
	// define a constant for the namespace of our custom function
	public static final String NAMESPACE = "http://lis.ic.unicamp.br/similarityfunction/";
	
	
	@Override
	public String getURI() {
	    return NAMESPACE + "LevenshteinFilter";
	}

	@Override
	public Value evaluate(ValueFactory valueFactory, Value... args) throws ValueExprEvaluationException {
//		System.out.println("[rd4] aquiiiiiiiiii");
		Levenshtein algorithm = new Levenshtein();
		Literal string1 = (Literal) args[0];
//		System.out.println("string1" + string1);
		Literal string2 = (Literal) args[1];
//		System.out.println("string2" + string2);
		double i = algorithm.distance(string1.getLabel().toLowerCase(), string2.getLabel().toLowerCase());
		System.out.println("similarity: " + valueFactory.createLiteral(i));
		return valueFactory.createLiteral(i);
	}

}
