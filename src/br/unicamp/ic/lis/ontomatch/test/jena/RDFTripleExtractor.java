package br.unicamp.ic.lis.ontomatch.test.jena;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.TDBLoader;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.tdb.sys.TDBInternal;
import com.hp.hpl.jena.util.FileManager;

public class RDFTripleExtractor {
	private Dataset ds;
	private Model model; // local dataset model
	
	public void TDBloading(){
		System.out.println("loading");
		// create model from tdb
		Dataset dataset = TDBFactory.createDataset("resources/ontologies/mesh/jena");

		// assume we want the default model, or we could get a named model here
		dataset.begin(ReadWrite.READ);
		model = dataset.getDefaultModel();
		dataset.end() ;
		System.out.println("aqui");
		System.out.println(model);
		// if model is null load local dataset into jena TDB
		if(model.isEmpty())
			TDBloading("resources/ontologies/mesh/chunks/xaa");
	}
	
	/**
	 * Load local dataset into jena TDB
	 */
	private void TDBloading(String fileDump){
		System.out.println("depois");
		// create tdb from .nt local file 
		FileManager fm = FileManager.get();
		fm.addLocatorClassLoader(RDFTripleExtractor.class.getClassLoader());
		InputStream in = fm.open(fileDump);

		Location location = new Location("resources/ontologies/mesh/jena");

		// load some initial data
		try{
			TDBLoader.load(TDBInternal.getBaseDatasetGraphTDB(TDBFactory.createDatasetGraph(location)), in, true);
		}
		catch(Exception e){
			System.out.println("TDB loading error: " + e.getMessage());
		}

		//create model from tdb
		Dataset dataset = TDBFactory.createDataset("resources/ontologies/mesh/jena");

		// assume we want the default model, or we could get a named model here
		dataset.begin(ReadWrite.READ) ;
		model = dataset.getDefaultModel();
		System.out.println(model);
		dataset.end();
	}
	
	public static void main(String[] args) throws JSONException {
//		System.out.println("main");
		RDFTripleExtractor rdfTripleExtractor = new RDFTripleExtractor();
//		
//		String tdbDirectory = "resources/ontologies/mesh/jena"; 
//		Model tdbModel = TDBFactory.createModel(tdbDirectory);
//		
//		String dbdump0 = "resources/ontologies/mesh/chunks";
//		
//		File folder = new File(dbdump0);
//		for (final File fileEntry : folder.listFiles()) {
//            System.out.println(fileEntry.getPath());
//            FileManager.get().readModel( tdbModel, fileEntry.getPath(), "N-TRIPLES");
//	    }
//		
//		System.out.println("finished");

		String params = "{text:Chest Pain, n:5, ontology:hfo, algorithm:Levenshtein}";
		rdfTripleExtractor.querying(params);
	}
	
	public void querying(String params) throws JSONException {
		
		JSONObject jsonParams = new JSONObject(params);
		
		String text = jsonParams.getString("text");
		String n = jsonParams.optString("n", "10");
		String ontology = jsonParams.optString("ontology", "hfo");
		String algorithm = jsonParams.optString("algorithm", "NormalizedLevenshtein");
		String floor = jsonParams.optString("floor", "0.0");
		String ceiling = jsonParams.optString("ceiling", "1");
		String order = jsonParams.optString("order", "DESC");
		
		if (algorithm.equals("Levenshtein") || algorithm.equals("OptimalStringAlignment"))
			order = "ASC";
		
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("PREFIX rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		stringBuffer.append("PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#> \n");
		stringBuffer.append("PREFIX ontomatch: <java:br.unicamp.ic.lis.ontomatch.filters.> \n");
		
		stringBuffer.append("SELECT DISTINCT ?resource ?label (ontomatch:" + algorithm + "Filter(?label, \"" + "chest pain"
				+ "\") as ?similarity) \n");
		stringBuffer.append("WHERE{ \n");
		stringBuffer.append("                 { ?resource        rdfs:label      ?label                 .   } \n");
		stringBuffer.append("     } \n");
		stringBuffer.append("HAVING (?similarity >= " + floor + " && ?similarity <=" + ceiling + ") \n");
		stringBuffer.append("ORDER BY " + order + "(?similarity) \n");
		stringBuffer.append("LIMIT " + n + " \n");
		
		Dataset dataset = TDBFactory.createDataset("resources/ontologies/mesh/jena");
		Model tdb = dataset.getDefaultModel();

		Query query = QueryFactory.create(stringBuffer.toString());
		System.out.println(query);
		QueryExecution qexec = QueryExecutionFactory.create(query, tdb);

		/*Execute the Query*/
		ResultSet results = qexec.execSelect();
		System.out.println(results);
		while (results.hasNext()) {
		   System.out.println(results.next());
		}

		qexec.close();
		tdb.close() ;
	}
}
