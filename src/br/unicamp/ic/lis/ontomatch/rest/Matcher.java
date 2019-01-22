package br.unicamp.ic.lis.ontomatch.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

import br.unicamp.ic.lis.ontomatch.model.Resource;
//import gov.nih.nlm.nls.skr.GenericObject;

@Path("")
public class Matcher {

	public static String ontology_dir = "resources/ontologies/";
	public static String pkg;
	public static Model model;

	boolean debbug = true;

	// public final static String TAO = "tao";
	// public final static String PATO = "pato";
	// public final static String XAO = "xao";
	// public final static String HFO = "hfo";

	@POST
	@Path("/resources")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getResources(String params) throws JSONException, IOException {

		System.out.println("POST /resources");

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

		StringBuffer query = new StringBuffer();
		query.append("PREFIX owl:       <http://www.w3.org/2002/07/owl#> \n");
		query.append("PREFIX rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#> \n");
		query.append("PREFIX oboinowl:  <http://www.geneontology.org/formats/oboInOwl#> \n");
		query.append("PREFIX xsd:       <http://www.w3.org/2001/XMLSchema#> \n");
		query.append("PREFIX ontomatch: <java:br.unicamp.ic.lis.ontomatch.filters.> \n");

		query.append("SELECT DISTINCT ?resource ?label (ontomatch:" + algorithm + "Filter(?label, \"" + text
				+ "\") as ?similarity) \n");
		query.append("WHERE{ \n");
		query.append("                 { ?resource        rdfs:label      ?label                 .   }   \n");
		query.append("            UNION                                                                  \n");
		query.append("                 { ?annotation      rdf:type        owl:AnnotationProperty .       \n");
		query.append("                   ?resource        ?annotation     ?label                 .   }   \n");

		query.append("     } \n");
		query.append("HAVING (?similarity >= " + floor + " && ?similarity <=" + ceiling + ") \n");
		query.append("ORDER BY " + order + "(?similarity) \n");
		query.append("LIMIT " + n + " \n");

		if (debbug)
			System.out.println(query);

		Query sparql = QueryFactory.create(query.toString());
		QueryExecution qExec = QueryExecutionFactory.create(sparql, getModel(ontology, "xrdf"));
		ResultSet rs = qExec.execSelect();

		List<Resource> resources = new ArrayList<>();
		if (null != rs) {
			while (rs.hasNext()) {
				QuerySolution result = rs.nextSolution();
				Resource resource = new Resource();
				resource.setLabel(result.getLiteral("label").getValue().toString());
				resource.setUri(result.get("resource").toString());
				resource.setSimilarity(result.getLiteral("similarity").getDouble());

				if (debbug)
					System.out.println(resource);

				resources.add(resource);
			}

			System.out.println(resources);
		}
		GenericEntity<List<Resource>> resourcesReturn = new GenericEntity<List<Resource>>(resources) {
		};
		return Response.ok().entity(resourcesReturn).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();

	}

	@POST
	@Path("/mesh")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMeshTerms(String params) throws JSONException, IOException, URISyntaxException {
//		
//		Model tdbModel = TDBFactory.createModel(path.toString());

//		java.nio.file.Path path = Paths.get(getClass().getClassLoader().getResource("resources/ontologies/mesh/chunks").toURI());
//		System.out.println("Chunks: "+path.toString());
////
		java.nio.file.Path path2 = Paths.get(getClass().getClassLoader().getResource("resources/ontologies/mesh/jena").toURI());
		System.out.println("Jena: "+path2.toString());
//
//		Model tdbModel = TDBFactory.createModel(path2.toString());
//		
//		File folder = new File(path.toString());
//		for (final File fileEntry : folder.listFiles()) {
//			System.out.println(fileEntry.getPath());
//			FileManager.get().readModel( tdbModel, fileEntry.getPath(), "N-TRIPLES");
//		}
//
//		System.out.println("finished");






		// assume we want the default model, or we could get a named model here

				Dataset dataset = TDBFactory.createDataset(path2.toString());
				dataset.begin(ReadWrite.READ);
				Model tdb = dataset.getDefaultModel();
				
				System.out.println("Empty? "+tdb.isEmpty());
		
				
				System.out.println("POST /mesh");
		
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
				
				StringBuffer stringQuery = new StringBuffer();
				stringQuery.append("PREFIX owl:       <http://www.w3.org/2002/07/owl#> \n");
				stringQuery.append("PREFIX rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
				stringQuery.append("PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#> \n");
				stringQuery.append("PREFIX ontomatch: <java:br.unicamp.ic.lis.ontomatch.filters.> \n");
				
				stringQuery.append("SELECT DISTINCT ?resource ?label (ontomatch:" + algorithm + "Filter(?label, \"" + text
						+ "\") as ?similarity) \n");
				stringQuery.append("WHERE{ \n");
				stringQuery.append("                 { ?resource        rdfs:label      ?label                 .   }   \n");
				stringQuery.append("            UNION                                                                  \n");
				stringQuery.append("                 { ?annotation      rdf:type        owl:AnnotationProperty .       \n");
				stringQuery.append("                   ?resource        ?annotation     ?label                 .   }   \n");

				stringQuery.append("     } \n");
				stringQuery.append("HAVING (?similarity >= " + floor + " && ?similarity <=" + ceiling + ") \n");
				stringQuery.append("ORDER BY " + order + "(?similarity) \n");
				stringQuery.append("LIMIT " + n + " \n");
				
				Query query = QueryFactory.create(stringQuery.toString());
				System.out.println(query);
				QueryExecution qexec = QueryExecutionFactory.create(query, tdb);
		
				/*Execute the Query*/
				ResultSet rs = qexec.execSelect();
//		
		List<Resource> resources = new ArrayList<>();
				if (null != rs) {
					while (rs.hasNext()) {
						System.out.println(rs.next());
						QuerySolution result = rs.nextSolution();
						Resource resource = new Resource();
						resource.setLabel(result.getLiteral("label").getValue().toString());
						resource.setUri(result.get("resource").toString());
						resource.setSimilarity(result.getLiteral("similarity").getDouble());
		
						if (debbug)
							System.out.println(resource);
		
						resources.add(resource);
					}
		
					System.out.println(resources);
				}
				
				dataset.end();
				qexec.close();
				tdb.close() ;

		GenericEntity<List<Resource>> resourcesReturn = new GenericEntity<List<Resource>>(resources) {
		};
		return Response.ok().entity(resourcesReturn).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	private Model getModel(String ontology, String extension) {
		model = ModelFactory.createDefaultModel();

		InputStream in = FileManager.get().open(ontology_dir + ontology + "."+ extension);

		System.out.println(in);
		model.read(in, null, extension);

		System.out.println("loaded");
		System.out.println(model);
		return model;
	}

	private static Matcher instance;

	//	public static synchronized Matcher getInstance() {
	//		if (instance == null) {
	//			instance = new Matcher();
	//		} return instance;
	//	}



	//	@POST
	//	@Path("/metamap/resources")
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public Response getMetaMapResources(String params) throws JSONException, IOException {
	//		System.out.println("POST /metamap/resources");
	//		JSONObject jsonParams = new JSONObject(params);
	//		String text = jsonParams.getString("text");
	//
	//		GenericObject myGenericObj = new GenericObject(100, "faguim", "esao3oMu");
	//
	//		myGenericObj.setField("Email_Address", "pantoja.ti@gmail.com");
	//		myGenericObj.setField("KSOURCE", "1516");
	//		myGenericObj.setField("COMMAND_ARGS", "-CIG --JSONn --silent -V USAbase");
	//		myGenericObj.setField("APIText", text);
	//
	//		List<Resource> resources = new ArrayList<>();
	//
	//		try {
	//			String result = myGenericObj.handleSubmission();
	//			System.out.println(result.split("\n")[1]);
	//			JSONObject jsonResult = new JSONObject(result.split("\n")[1]);
	//			JSONArray allDocuments = (JSONArray) jsonResult.get("AllDocuments");
	//			for (int i = 0; i < allDocuments.length(); i++) {
	//				JSONObject document = (JSONObject) ((JSONObject) allDocuments.get(i)).get("Document");
	//				JSONArray utterances = (JSONArray) document.get("Utterances");
	//
	//				for (int j = 0; j < utterances.length(); j++) {
	//					JSONArray phrases = (JSONArray) ((JSONObject) utterances.get(j)).get("Phrases");
	//					System.out.println(phrases);
	//					for (int k = 0; k < phrases.length(); k++) {
	//
	//						JSONArray mappings = (JSONArray) ((JSONObject) phrases.get(k)).get("Mappings");
	//						for (int l = 0; l < mappings.length(); l++) {
	//							JSONArray mappingCandidates = (JSONArray) ((JSONObject) mappings.get(l))
	//									.get("MappingCandidates");
	//							for (int m = 0; m < mappingCandidates.length(); m++) {
	//								Resource resource = new Resource();
	//								resource.setLabel(
	//										((JSONObject) mappingCandidates.get(m)).get("CandidatePreferred").toString());
	//								resource.setUri(((JSONObject) mappingCandidates.get(m)).get("CandidateCUI").toString());
	//
	//								JSONArray matchedWords = (JSONArray) ((JSONObject) mappingCandidates.get(m))
	//										.get("MatchedWords");
	//								for (int n = 0; n < matchedWords.length(); n++) {
	//									resource.getMatchedWords().add(matchedWords.get(n).toString());
	//								}
	//
	//								if (!resources.contains(resource))
	//									resources.add(resource);
	//							}
	//						}
	//					}
	//				}
	//			}
	//
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//
	//		GenericEntity<List<Resource>> resourcesReturn = new GenericEntity<List<Resource>>(resources) {
	//		};
	//		return Response.ok().entity(resourcesReturn).header("Access-Control-Allow-Origin", "*")
	//				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	//	}
}
