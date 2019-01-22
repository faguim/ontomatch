package br.unicamp.ic.lis.ontomatch.test;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

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
		getMeshTerms();
//		getResources();
//		getMetaMapResource();
	}

	private static void getResource() throws IOException, JSONException{
		String params = "{text:Chest Pain, similarity:0.7, ontology:hfo}";
		JSONObject jsonParams = new JSONObject(params);

//		System.out.println(jsonParams);
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

		Client client = Client.create(clientConfig);
		WebResource webResource = client.resource("http://localhost:8080/ontomatch/rest/resource");
//		WebResource webResource = client.resource("http://ontomatch.lis.ic.unicamp.br/api/rest/resource");

		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).post(ClientResponse.class, jsonParams);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String responseBody = response.getEntity(String.class);

//		System.out.println("Response: " + responseBody);
		JSONObject responseJson = new JSONObject(responseBody);
		System.out.println(responseJson);
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
		WebResource webResource = client.resource("http://localhost:8080/ontomatch/rest/resources");

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
	
	private static void getMeshTerms() throws IOException, JSONException{
		String params = "{text:chest pain, n:5, ontology:mesh, algorithm:Levenshtein}";
		JSONObject jsonParams = new JSONObject(params);

		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

		Client client = Client.create(clientConfig);
		WebResource webResource = client.resource("http://localhost:8080/ontomatch/rest/mesh");

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
	
	private static void getMetaMapResource() throws IOException, JSONException{
		String params = "{text: 'Female patient, 54 years old, with shortness of breath in the last 5 hours'}";
		JSONObject jsonParams = new JSONObject(params);

//		System.out.println(jsonParams);
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

		Client client = Client.create(clientConfig);
		WebResource webResource = client.resource("http://localhost:8080/ontomatch/rest/metamap/resources");
//		WebResource webResource = client.resource("http://ontomatch.lis.ic.unicamp.br/api/rest/resource");

		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).post(ClientResponse.class, jsonParams);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String responseBody = response.getEntity(String.class);
		JSONArray jsonArray = new JSONArray(responseBody);
		
		for (int i = 0; i < jsonArray.length(); i++) {
			System.out.println(jsonArray.get(i));
		}
		
		System.out.println(jsonArray);
	}
}