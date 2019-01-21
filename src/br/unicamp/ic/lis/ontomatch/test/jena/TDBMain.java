//package br.unicamp.ic.lis.ontomatch.test.jena;
//
//import java.util.List;
//
//import org.apache.jena.rdf.model.Statement;
//
//public class TDBMain {
//	public static void main(String[] args) 
//	{
//		TDBConnection tdb = new TDBConnection("resources/ontologies/mesh/jena");
//		
////		String URI = "https://tutorial-academy.com/2015/tdb#";
////		
////		String namedModel1 = "Model_German_Cars";
////		String namedModel2 = "Model_US_Cars";
////		
////		String john = URI + "John";
////		tdb = new TDBConnection("tdb");
////		
////		// null = wildcard search. Matches everything with BMW as object!
////		List<Statement> result = tdb.getStatements( namedModel1, null, null, URI + "BMW");
////		System.out.println( namedModel1 + " size: " + result.size() + "\n\t" + result );
////		
////		// null = wildcard search. Matches everything with john as subject!
////		result = tdb.getStatements( namedModel2, john, null, null);
////		System.out.println( namedModel2 + " size: " + result.size() + "\n\t" + result );
////		
////		result = tdb.getStatements( namedModel1, john, null, null);
////		System.out.println( namedModel1 + " size: " + result.size() + "\n\t" + result );
////		tdb.close();
//
//	}
//
//}