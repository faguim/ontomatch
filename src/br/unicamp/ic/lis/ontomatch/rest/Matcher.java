package br.unicamp.ic.lis.ontomatch.rest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import br.unicamp.ic.lis.ontomatch.model.Resource;


@Path("")
public class Matcher {
	
	public static String ontology_dir = "resources/ontologies/";
	public static String pkg; 
	public static Model model;

	public final static String TAO = "tao";
	public final static String PATO = "pato";
	public final static String XAO = "xao";
	public final static String HFO = "hfo";
	
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
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX f: <java:"+this.getClass().getPackage().getName()+".> "
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
		List<Resource> resources = new ArrayList<>();
		
		JSONObject jsonParams = new JSONObject(params);

		String text = jsonParams.getString("text");
		String n = jsonParams.getString("n");
		String ontology = jsonParams.getString("ontology");
		
		StringBuffer queryString = new StringBuffer();
		queryString.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX oboinowl: <http://www.geneontology.org/formats/oboInOwl#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX f: <java:"+this.getClass().getPackage().getName()+".> "
				+ "SELECT DISTINCT ?resource ?label (f:LevenshteinFilter(?label, \"" + text + "\") as ?similarity) "
				+ "WHERE { "
				+ " { ?resource rdfs:label ?label} "

//				+ "UNION {?resource oboinowl:hasExactSynonym ?label . } "
//				+ "UNION {?resource oboinowl:hasRelatedSynonym ?label . } "

				+ "}"
				+ "ORDER BY DESC(?similarity) LIMIT "+n);

		Query sparql = QueryFactory.create(queryString.toString());
		System.out.println(queryString);
		QueryExecution qExec = QueryExecutionFactory.create(sparql, getModel(ontology));
		ResultSet rs = qExec.execSelect();

		while (rs.hasNext()) {
			QuerySolution result = rs.nextSolution();
			System.out.println(result);
			Resource resource = new Resource();
			resource.setLabel(result.getLiteral("label").getValue().toString());
			resource.setUri(result.get("resource").toString());
			resource.setSimilarity(result.getLiteral("similarity").getDouble());
			
			resources.add(resource);
		}
		return resources;
	}
	
	private Model getModel(String ontology) {
		model = ModelFactory.createDefaultModel();

		InputStream in = FileManager.get().open(ontology_dir + ontology + ".xrdf");
		System.out.println(in);
		model.read(in, null);
		
		return model;
	}
}
