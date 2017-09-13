package br.unicamp.ic.lis.ontomatch.rest;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
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

import com.sun.jersey.api.json.JSONWithPadding;

import br.unicamp.ic.lis.ontomatch.model.Resource;


@Path("/intern")
public class Matcher {
	
	public static String ontology_dir = "resources/ontologies/";
	public static String pkg; 
	public static Model model;

	public final static String TAO = "tao";
	public final static String PATO = "pato";
	public final static String XAO = "xao";
	public final static String HFO = "hfo";
	
	//curl -i -X POST -H 'Content-Type: application/json' http://localhost:8080/OntoMatch/rest/intern/resource/{chest}/{0.3}/{hfo}
	
	@POST
	@Path("/resource/{text}/{similarity}/{ontology}")
	@Produces({ "application/x-javascript", MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public JSONWithPadding getRotaDaLinha(@QueryParam("callback") String callback, 
			@PathParam("text") String text,
			@PathParam("similarity") String similarity,
			@PathParam("ontology") String ontology) {
	
		System.out.println(text);
		System.out.println(similarity);
		Resource resource = new Resource();
		System.out.println(ontology);

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
		System.out.println(resource);
		
		return new JSONWithPadding(new GenericEntity<Resource>(resource) {}, callback);
	}
	
	private Model getModel(String ontology) {
		model = ModelFactory.createDefaultModel();

		InputStream in = FileManager.get().open(ontology_dir + ontology + ".xrdf");
		System.out.println(in);
		model.read(in, null);
		
		return model;
	}
}
