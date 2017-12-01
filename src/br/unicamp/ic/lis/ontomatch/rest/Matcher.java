package br.unicamp.ic.lis.ontomatch.rest;

import java.io.IOException;
import java.io.InputStream;
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

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.function.library.print;
import org.apache.jena.util.FileManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import br.unicamp.ic.lis.ontomatch.model.Resource;
import gov.nih.nlm.nls.skr.GenericObject;

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
	@Path("/resource")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Resource getResource(String params) throws JSONException {

		JSONObject jsonParams = new JSONObject(params);

		String similarity = jsonParams.getString("similarity");
		String ontology = jsonParams.getString("ontology");
		String text = jsonParams.getString("text");

		Resource resource = new Resource();

		StringBuffer queryString = new StringBuffer();
		queryString.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX oboinowl: <http://www.geneontology.org/formats/oboInOwl#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " + "PREFIX f: <java:"
				+ this.getClass().getPackage().getName() + ".> "
				+ "SELECT DISTINCT ?resource ?label (f:LevenshteinFilter(?label, \"" + text + "\") as ?similarity)"
				+ "WHERE { ");

		queryString.append("{?resource rdfs:label ?label . FILTER (f:LevenshteinFilter(?label, \"" + text + "\") >= "
				+ similarity + ") }");

		queryString.append("UNION {?resource oboinowl:hasExactSynonym ?label . FILTER (f:LevenshteinFilter(?label, \""
				+ text + "\") >= " + similarity + ")} ");
		queryString.append("UNION {?resource oboinowl:hasRelatedSynonym ?label . FILTER (f:LevenshteinFilter(?label, \""
				+ text + "\") >= " + similarity + ")} ");
		queryString.append("} ORDER BY DESC(f:LevenshteinFilter(?label, \"" + text + "\")) LIMIT 1");

		Query sparql = QueryFactory.create(queryString.toString());
		QueryExecution qExec = QueryExecutionFactory.create(sparql, getModel(ontology));
		ResultSet rs = qExec.execSelect();
		while (rs.hasNext()) {
			QuerySolution result = rs.nextSolution();
			resource.setLabel(result.getLiteral("label").getValue().toString());
			resource.setUri(result.get("resource").toString());
			resource.setSimilarity(result.getLiteral("similarity").getDouble());
		}

		return resource;
	}

	@POST
	@Path("/resources")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Resource> getResources(String params) throws JSONException {

		JSONObject jsonParams = new JSONObject(params);

		String text = jsonParams.getString("text");
		String n = jsonParams.optString("n", "10");
		String ontology = jsonParams.optString("ontology", "hfo");
		String algorithm = jsonParams.optString("algorithm", "NormalizedLevenshtein");

		String order = "DESC";

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
				+ "\") as ?similarity) \n"); // ?altlabel (ontomatch:NormalizedLevenshteinFilter(?altlabel,
												// \""+text+"\") as ?similarityaltlabel) \n");
		query.append("WHERE{ \n");
		query.append("                 { ?resource        rdfs:label      ?label                 .   }   \n");
		query.append("            UNION                                                                  \n");
		query.append("                 { ?annotation      rdf:type        owl:AnnotationProperty .       \n");
		query.append("                   ?resource        ?annotation     ?label                 .   }   \n");

		query.append("     } \n");
		query.append("ORDER BY " + order + "(?similarity) \n");
		query.append("LIMIT " + n);

		if (debbug)
			System.out.println(query);

		Query sparql = QueryFactory.create(query.toString());
		QueryExecution qExec = QueryExecutionFactory.create(sparql, getModel(ontology));
		ResultSet rs = qExec.execSelect();

		List<Resource> resources = new ArrayList<>();

		while (rs.hasNext()) {
			QuerySolution result = rs.nextSolution();
			System.out.println(result.toString());
			Resource resource = new Resource();
			resource.setLabel(result.getLiteral("label").getValue().toString());
			resource.setUri(result.get("resource").toString());
			resource.setSimilarity(result.getLiteral("similarity").getDouble());

			// if(debbug)
			// System.out.println(resource);

			resources.add(resource);
		}
		return resources;
	}

	@POST
	@Path("/metamap/resources")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMetaMapResources(String params) throws JSONException, IOException {
		System.out.println("POST /metamap/resources");
		JSONObject jsonParams = new JSONObject(params);
		String text = jsonParams.getString("text");

		GenericObject myGenericObj = new GenericObject(100, "faguim", "esao3oMu");

		myGenericObj.setField("Email_Address", "pantoja.ti@gmail.com");
		myGenericObj.setField("KSOURCE", "1516");
		myGenericObj.setField("COMMAND_ARGS", "-CIG --JSONn --silent -V USAbase");
		myGenericObj.setField("APIText", text);

		List<Resource> resources = new ArrayList<>();

		try {
			String result = myGenericObj.handleSubmission();
			System.out.println(result.split("\n")[1]);
			JSONObject jsonResult = new JSONObject(result.split("\n")[1]);
			JSONArray allDocuments = (JSONArray) jsonResult.get("AllDocuments");
			for (int i = 0; i < allDocuments.length(); i++) {
				JSONObject document = (JSONObject) ((JSONObject) allDocuments.get(i)).get("Document");
				JSONArray utterances = (JSONArray) document.get("Utterances");

				for (int j = 0; j < utterances.length(); j++) {
					JSONArray phrases = (JSONArray) ((JSONObject) utterances.get(j)).get("Phrases");
					System.out.println(phrases);
					for (int k = 0; k < phrases.length(); k++) {

						JSONArray mappings = (JSONArray) ((JSONObject) phrases.get(k)).get("Mappings");
						for (int l = 0; l < mappings.length(); l++) {
							JSONArray mappingCandidates = (JSONArray) ((JSONObject) mappings.get(l))
									.get("MappingCandidates");
							for (int m = 0; m < mappingCandidates.length(); m++) {
								Resource resource = new Resource();
								resource.setLabel(
										((JSONObject) mappingCandidates.get(m)).get("CandidatePreferred").toString());
								resource.setUri(((JSONObject) mappingCandidates.get(m)).get("CandidateCUI").toString());

								JSONArray matchedWords = (JSONArray) ((JSONObject) mappingCandidates.get(m))
										.get("MatchedWords");
								for (int n = 0; n < matchedWords.length(); n++) {
									resource.getMatchedWords().add(matchedWords.get(n).toString());
								}

								if (!resources.contains(resource))
									resources.add(resource);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		GenericEntity<List<Resource>> resourcesReturn = new GenericEntity<List<Resource>>(resources) {
		};
		return Response.ok().entity(resourcesReturn).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	private Model getModel(String ontology) {
		model = ModelFactory.createDefaultModel();

		InputStream in = FileManager.get().open(ontology_dir + ontology + ".xrdf");
		System.out.println(in);
		model.read(in, null);

		return model;
	}
}
