package br.unicamp.ic.lis.ontomatch.rdf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.ValueFactoryImpl;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.ParserConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;

import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

public class HelloRDF4J {

	public static void simpleSPARQL() {
		File dataStore = new File("resources/ontologies/mesh/store");
		Repository nativeRep = new SailRepository(new NativeStore(dataStore));
		nativeRep.initialize();
		
		String text = "chest pain";
		String n = "10";
		String ontology = "mesh";
		String algorithm = "Levenshtein";
		String floor = "0.0";
		String ceiling = "1";
		String order = "DESC";
		
		if (algorithm.equals("Levenshtein") || algorithm.equals("OptimalStringAlignment"))
			order = "ASC";
		
		try(RepositoryConnection conn = nativeRep.getConnection()) {
			StringBuffer query = new StringBuffer();
			query.append("PREFIX owl:       <http://www.w3.org/2002/07/owl#> \n");
			query.append("PREFIX rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
			query.append("PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#> \n");
			query.append("PREFIX oboinowl:  <http://www.geneontology.org/formats/oboInOwl#> \n");
			query.append("PREFIX xsd:       <http://www.w3.org/2001/XMLSchema#> \n");
			query.append("PREFIX ontomatch: <http://lis.ic.unicamp.br/similarityfunction/> \n");

			query.append("SELECT DISTINCT ?resource ?label (ontomatch:" + algorithm + "Filter(?label, \"" + text
					+ "\") as ?similarity) \n");
			query.append("WHERE{ \n");
			query.append("                 { ?resource        rdfs:label      ?label                 .   }   \n");
			query.append("            UNION                                                                  \n");
			query.append("                 { ?annotation      rdf:type        owl:AnnotationProperty .       \n");
			query.append("                   ?resource        ?annotation     ?label                 .   }   \n");

			query.append("     } \n");
			query.append("GROUP BY	 ?resource  ?label \n");
//			query.append("HAVING (?similarity >= " + floor + " && ?similarity <=" + ceiling + ") \n");

			query.append("ORDER BY " + order + "(?similarity) \n");
			query.append("LIMIT " + n + " \n");
			System.out.println(query.toString());
			TupleQuery tupleQuery = conn.prepareTupleQuery(query.toString());
			TupleQueryResult result = tupleQuery.evaluate();
			try {
				List<String> bindingNames = result.getBindingNames();
				System.out.println(bindingNames);
				while (result.hasNext()) {
					System.out.println("tem");
					BindingSet bindingSet = result.next();
					Value firstValue = bindingSet.getValue(bindingNames.get(0));
					Value secondValue = bindingSet.getValue(bindingNames.get(1));
					Value thirdValue = bindingSet.getValue(bindingNames.get(2));

					System.out.println(firstValue);
					System.out.println(secondValue);
					System.out.println("sim: "+thirdValue);

				}
			} finally {
				result.close();
			}
		} finally {
		    // Before our program exits, make sure the database is properly shut down.
			nativeRep.shutDown();
		}

	}
	
	public static void main(String[] args) throws UnsupportedRDFormatException, IOException {
		simpleSPARQL();
	}
}