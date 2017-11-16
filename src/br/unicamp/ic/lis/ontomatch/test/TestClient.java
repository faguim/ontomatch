package br.unicamp.ic.lis.ontomatch.test;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.jena.atlas.json.JsonObject;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

public class TestClient {
	public static void main(String[] args) throws IOException, JSONException {
//		getResource();
//		getResources();
		getWholeResource();
	}

	private static void getResource() throws IOException, JSONException{
		String params = "{text:Chest Pain, similarity:0.7, ontology:hfo}";
		JSONObject jsonParams = new JSONObject(params);

//		System.out.println(jsonParams);
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

		Client client = Client.create(clientConfig);
		WebResource webResource = client.resource("http://localhost:8080/OntoMatch/rest/resource");
//		WebResource webResource = client.resource("http://ontomatch.lis.ic.unicamp.br/api/rest/resource");

		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).post(ClientResponse.class, jsonParams);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String responseBody = response.getEntity(String.class);

//		System.out.println("Response: " + responseBody);
		JSONObject responseJson = new JSONObject(responseBody);
//		System.out.println("Response Json: "+ responseJson);

//		JSONObject resourceJSON = responseJson.getJSONObject("entity");
//
//		System.out.println("Resource: " + resourceJSON);
//		System.out.println("URI: " + resourceJSON.get("uri"));
	}
	
	private static void getResources() throws IOException, JSONException{
		String params = "{text:Chest Pain, n:5, ontology:hfo}";
		JSONObject jsonParams = new JSONObject(params);

//		System.out.println(jsonParams);
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

		Client client = Client.create(clientConfig);
		WebResource webResource = client.resource("http://localhost:8080/OntoMatch/rest/resources");

		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).post(ClientResponse.class, jsonParams);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String responseBody = response.getEntity(String.class);

//		System.out.println("Response: " + responseBody);
//		JSONObject responseJson = new JSONObject(responseBody);
//		System.out.println("Response Json: "+ responseJson);
//
//		JSONArray resourcesJSON = responseJson.getJSONArray("entity");
//
//		System.out.println("Resources: " + resourcesJSON);
//		
//		for (int i = 0; i < resourcesJSON.length(); i++) {
//			System.out.println("label: " + ((JSONObject)resourcesJSON.get(i)).get("label"));
//		}
//		
	}
	
	private static void getWholeResource() throws IOException, JSONException{
		String params = "{text:Chest Pain, similarity:0.7, ontology:hfo, algorithm:Cosine}";
		JSONObject jsonParams = new JSONObject(params);

//		System.out.println(jsonParams);
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

		Client client = Client.create(clientConfig);
		WebResource webResource = client.resource("http://localhost:8080/OntoMatch/rest/wholeresource");
//		WebResource webResource = client.resource("http://ontomatch.lis.ic.unicamp.br/api/rest/resource");

		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).post(ClientResponse.class, jsonParams);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String responseBody = response.getEntity(String.class);
//System.out.println(responseBody);
//		System.out.println("Response: " + responseBody);
		JSONArray jsonArray = new JSONArray(responseBody);
		
		for (int i = 0; i < jsonArray.length(); i++) {
			System.out.println(jsonArray.get(i));
		}
		
		System.out.println(jsonArray);
//		System.out.println("Response Json: "+ responseJson);

//		JSONObject resourceJSON = responseJson.getJSONObject("entity");
//
//		System.out.println("Resource: " + resourceJSON);
//		System.out.println("URI: " + resourceJSON.get("uri"));
	}
}