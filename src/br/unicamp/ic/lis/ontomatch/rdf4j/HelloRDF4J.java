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
			System.out.println(result);
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
//			StringBuffer query = new StringBuffer();
//			query.append("PREFIX owl:       <http://www.w3.org/2002/07/owl#> \n");
//			query.append("PREFIX rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
//			query.append("PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#> \n");
//			query.append("PREFIX oboinowl:  <http://www.geneontology.org/formats/oboInOwl#> \n");
//			query.append("PREFIX xsd:       <http://www.w3.org/2001/XMLSchema#> \n");
//			query.append("PREFIX ontomatch: <http://lis.ic.unicamp.br/similatiryfunction/> \n");
//
//			query.append("SELECT DISTINCT ?resource ?label (ontomatch:" + algorithm + "Filter(?label, \"" + text
//					+ "\") as ?similarity) \n");
//			query.append("WHERE{ \n");
//			query.append("                 { ?resource        rdfs:label      ?label                 .   }   \n");
//			query.append("            UNION                                                                  \n");
//			query.append("                 { ?annotation      rdf:type        owl:AnnotationProperty .       \n");
//			query.append("                   ?resource        ?annotation     ?label                 .   }   \n");
//
//			query.append("     } \n");
//			query.append("GROUP BY ?resource ?label\n");
//			query.append("HAVING (?similarity >= " + floor + " && ?similarity <=" + ceiling + ") \n");
//			query.append("ORDER BY " + order + "(?similarity) \n");
//			query.append("LIMIT " + n + " \n");
			
			
//			String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
//					"PREFIX sim: <http://lis.ic.unicamp.br/similatiryfunction/>\n" + 
//					"SELECT ?x ?label\n" + 
//					"WHERE {\n" + 
//					"   ?x rdfs:label ?label .\n" + 
//					"   FILTER(sim:LevenshteinFilter(str(?label)>0.9))\n" + 
//					"}";
//			TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);	
//			TupleQueryResult result = tupleQuery.evaluate();
//			
//			try {
//				List<String> bindingNames = result.getBindingNames();
//				while (result.hasNext()) {
//					BindingSet bindingSet = result.next();
//					Value firstValue = bindingSet.getValue(bindingNames.get(0));
//					Value secondValue = bindingSet.getValue(bindingNames.get(1));
//					System.out.println(firstValue);
//				}
//			} finally {
//				result.close();
//			}
		}
	}
	
	public static void createDB() throws RepositoryException, IOException {
		File dataStore = new File("resources/ontologies/mesh/store");
		
		// É preciso executar o seguinte comando no shell para criar os chunks a partir do .nt gigante
		// split -l 100000 mesh.nt 
		File chunksDir = new File("resources/ontologies/mesh/chunks");

		Repository nativeRep = new SailRepository(new NativeStore(dataStore));
		nativeRep.initialize();
		

		File[] files = chunksDir.listFiles();
		try(RepositoryConnection conn = nativeRep.getConnection()) {
			// create a parser config with preferred settings
			ParserConfig config = new ParserConfig();
			config.set(BasicParserSettings.PRESERVE_BNODE_IDS, true);
			config.set(BasicParserSettings.VERIFY_DATATYPE_VALUES, true);

			// set the parser configuration for our connection
			conn.setParserConfig(config);

			for (File file: files) {
				String fileName = file.getAbsolutePath();
				System.out.println("path/" + fileName);
				try {
					conn.add(file, "file://" + fileName, RDFFormat.NTRIPLES);
				} catch (RDFParseException e) {
					e.printStackTrace();
				}
			}
		}
		
//		try(RepositoryConnection conn = nativeRep.getConnection()) {
//		  conn.add(dataFile, dataDir.getAbsolutePath(), RDFFormat.NTRIPLES);
//		}
		
//		try(RepositoryConnection conn = nativeRep.getConnection()) {
//			  // start an explicit transaction to avoid each individual statement being committed
//			  conn.begin();
//			  String fileName = "resources/ontologies/mesh.nt";
//			  RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES);
//			  // add our own custom RDFHandler to the parser. This handler takes care of adding
//			  // triples to our repository and doing intermittent commits
//			  parser.setRDFHandler(new ChunkCommitter(conn));
//			  
//			  File file = new File(fileName);
//			  FileInputStream is = new FileInputStream(file);
//			  parser.parse(is, "file://" + file.getCanonicalPath());
//			  conn.commit();
//			}
		
		


		// read the file 'example-data-artists.ttl' as an InputStream.
		//		InputStream input = HelloRDF4J.class.getResourceAsStream("/" + filename);
		//System.out.println(input);
		//		// Rio also accepts a java.io.Reader as input for the parser.
		//		Model model = Rio.parse(input, "", RDFFormat.NTRIPLES);
		//		System.out.println(model);
	}
	
	public static void main(String[] args) throws UnsupportedRDFormatException, IOException {
//		createDB();
				simpleSPARQL();
	}
}