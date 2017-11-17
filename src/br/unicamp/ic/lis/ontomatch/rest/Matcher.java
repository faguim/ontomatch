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
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import br.unicamp.ic.lis.ontomatch.model.Resource;

@Path("")
public class Matcher {

	public static String ontology_dir = "resources/ontologies/";
	public static String pkg;
	public static Model model;
	
	boolean debbug = false;


//	public final static String TAO = "tao";
//	public final static String PATO = "pato";
//	public final static String XAO = "xao";
//	public final static String HFO = "hfo";

	
	//*** remover**//
//	@POST
//	@Path("/resource")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Resource getResource(String params) throws JSONException {
//
//		JSONObject jsonParams = new JSONObject(params);
//
//		String similarity = jsonParams.getString("similarity");
//		String ontology = jsonParams.getString("ontology");
//		String text = jsonParams.getString("text");
//
//		Resource resource = new Resource();
//
//		StringBuffer queryString = new StringBuffer();
//		queryString.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
//				+ "PREFIX oboinowl: <http://www.geneontology.org/formats/oboInOwl#> "
//				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " + "PREFIX f: <java:"
//				+ this.getClass().getPackage().getName() + ".> "
//				+ "SELECT DISTINCT ?resource ?label (f:LevenshteinFilter(?label, \"" + text + "\") as ?similarity)"
//				+ "WHERE { ");
//
//		queryString.append("{?resource rdfs:label ?label . FILTER (f:LevenshteinFilter(?label, \"" + text + "\") >= "
//				+ similarity + ") }");
//
//		queryString.append("UNION {?resource oboinowl:hasExactSynonym ?label . FILTER (f:LevenshteinFilter(?label, \""
//				+ text + "\") >= " + similarity + ")} ");
//		queryString.append("UNION {?resource oboinowl:hasRelatedSynonym ?label . FILTER (f:LevenshteinFilter(?label, \""
//				+ text + "\") >= " + similarity + ")} ");
//		queryString.append("} ORDER BY DESC(f:LevenshteinFilter(?label, \"" + text + "\")) LIMIT 1");
//
//		Query sparql = QueryFactory.create(queryString.toString());
//		QueryExecution qExec = QueryExecutionFactory.create(sparql, getModel(ontology));
//		ResultSet rs = qExec.execSelect();
//		while (rs.hasNext()) {
//			QuerySolution result = rs.nextSolution();
//			resource.setLabel(result.getLiteral("label").getValue().toString());
//			resource.setUri(result.get("resource").toString());
//			resource.setSimilarity(result.getLiteral("similarity").getDouble());
//		}
//
//		return resource;
//	}

	@POST
	@Path("/resources")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getResources(String params) throws JSONException, IOException {
		
		System.out.println("POST /resources");

		JSONObject jsonParams = new JSONObject(params);

		String text = jsonParams.getString("text"); 
		String n = jsonParams.optString("n","10");
		String ontology = jsonParams.optString("ontology", "hfo");
		String algorithm = jsonParams.optString("algorithm", "NormalizedLevenshtein");
		String floor = jsonParams.optString("floor", "0.0");
		String ceiling = jsonParams.optString("ceiling", "1");
		String order = jsonParams.optString("order", "DESC");
		
		if(algorithm.equals("Levenshtein") || algorithm.equals("OptimalStringAlignment"))
			order = "ASC";
			
		StringBuffer query = new StringBuffer();		
		query.append("PREFIX owl:       <http://www.w3.org/2002/07/owl#> \n");
		query.append("PREFIX rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#> \n");
		query.append("PREFIX oboinowl:  <http://www.geneontology.org/formats/oboInOwl#> \n");
		query.append("PREFIX xsd:       <http://www.w3.org/2001/XMLSchema#> \n"); 
		query.append("PREFIX ontomatch: <java:br.unicamp.ic.lis.ontomatch.filters.> \n");
		
		query.append("SELECT DISTINCT ?resource ?label (ontomatch:"+algorithm+"Filter(?label, \"" + text + "\") as ?similarity) \n");
		query.append("WHERE{ \n");
		query.append("                 { ?resource        rdfs:label      ?label                 .   }   \n");
		query.append("            UNION                                                                  \n");
		query.append("                 { ?annotation      rdf:type        owl:AnnotationProperty .       \n");
		query.append("                   ?resource        ?annotation     ?label                 .   }   \n");
 
		query.append("     } \n");
		query.append("HAVING (?similarity >= "+floor+" && ?similarity <="+ceiling+") \n");
		query.append("ORDER BY "+order+"(?similarity) \n");
		query.append("LIMIT " + n+" \n");

		if(debbug)
			System.out.println(query);
		
		
		Query sparql = QueryFactory.create(query.toString());
		model = getModel(ontology);
		QueryExecution qExec = QueryExecutionFactory.create(sparql, getModel(ontology));
		ResultSet rs = qExec.execSelect();

		List<Resource> resources = new ArrayList<>();
		if (null != rs) {
			while (rs.hasNext()) {
				QuerySolution result = rs.nextSolution();
				Resource resource = new Resource();
				resource.setLabel(result.getLiteral("label").getValue().toString());
				resource.setUri(result.get("resource").toString());
				resource.setSimilarity(result.getLiteral("similarity").getDouble());
				
				
				if(debbug)
					System.out.println(resource);
				
				resources.add(resource);
			}
			
			System.out.println(resources);
		}
		GenericEntity<List<Resource>> resourcesReturn = new GenericEntity<List<Resource>>(resources){};
		return Response.ok()
				.entity(resourcesReturn)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		
	}

	@POST
	@Path("/wholeresource")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Resource> getWholeResource(String params) throws JSONException, IOException {

		JSONObject jsonParams = new JSONObject(params); 

		String text = jsonParams.getString("text");
		String n = jsonParams.optString("n","10");
		String ontology = jsonParams.optString("ontology", "hfo");
		String algorithm = jsonParams.optString("algorithm", "NormalizedLevenshtein");
		String floor = jsonParams.optString("floor", "0.0");
		String ceiling = jsonParams.optString("ceiling", "1");
		String order = jsonParams.optString("order", "DESC");
		
		StringBuffer query = new StringBuffer();		
		query.append("PREFIX owl:       <http://www.w3.org/2002/07/owl#> \n");
		query.append("PREFIX rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#> \n");
		query.append("PREFIX oboinowl:  <http://www.geneontology.org/formats/oboInOwl#> \n");
		query.append("PREFIX xsd:       <http://www.w3.org/2001/XMLSchema#> \n"); 
		query.append("PREFIX ontomatch: <java:br.unicamp.ic.lis.ontomatch.filters.> \n");
		
		query.append("SELECT DISTINCT ?resource ?label ?upperClass ?value (ontomatch:"+algorithm+"Filter(?label, \"" + text + "\") as ?similarity) \n");
		query.append("WHERE{ \n");
		query.append("                 { ?resource        rdfs:label      ?label                 .   }   \n");
		query.append("            UNION                                                                  \n");
		query.append("                 { ?annotation      rdf:type        owl:AnnotationProperty .       \n");
		query.append("                   ?resource        ?annotation     ?label                 .   }   \n");
		query.append("			  	   { ?resource        rdfs:subClassOf ?upperClass  . ");
		query.append(" 			   		 ?upperClass rdfs:label ?value }");
		query.append("     } \n");
		query.append("HAVING (?similarity >= "+floor+" && ?similarity <="+ceiling+") \n");
		query.append("ORDER BY "+order+"(?similarity) \n");
		query.append("LIMIT " + n+" \n");
		
//		System.out.println(query);

		Query sparql = QueryFactory.create(query.toString());
		QueryExecution qExec = QueryExecutionFactory.create(sparql, getModel(ontology));
		ResultSet rs = qExec.execSelect();

		List<Resource> resources = new ArrayList<>();
		
		while (rs.hasNext()) {
			QuerySolution result = rs.nextSolution();
//System.out.println(result);
			Resource resource = new Resource();
			resource.setUri(result.get("resource").toString());
			
			if (resources.contains(resource)) {
				int index = resources.indexOf(resource);
				resource = resources.get(index);
//				System.out.println("retornou:  "+resource);
			} else {
				resource.setLabel(result.getLiteral("label").getValue().toString());
				resource.setSimilarity(result.getLiteral("similarity").getDouble());

				resources.add(resource);
			}

			Resource parent = new Resource();
			parent.setLabel(result.getLiteral("value").getValue().toString());
			parent.setUri(result.get("upperClass").toString());

			
			if(!resource.getParents().contains(parent))
				resource.getParents().add(parent);
			
//			System.out.println(resource);
//			
//			System.out.print("Resource: " + result.get("resource"));
//			System.out.print(" | Label: " + result.get("label"));
//
//			System.out.print(" | SubClassOf: " + result.get("upperClass"));
//			System.out.print(" | value: " + result.get("value"));
//
//			System.out.println(" | Similarity: " + result.getLiteral("similarity").getDouble());
			

		}

		return resources;
	}

	private Model getModel(String ontology) throws IOException {
		model = ModelFactory.createDefaultModel();

		FileManager.get().readModel(model, ontology_dir + ontology + ".xrdf");
		return model;
	}
}
