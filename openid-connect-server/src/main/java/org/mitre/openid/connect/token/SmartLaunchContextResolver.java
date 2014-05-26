package org.mitre.openid.connect.token;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component
public class SmartLaunchContextResolver implements LaunchContextResolver {

	private String fhirEndpoint;
	private String username;
	private String password;

	@Override
	public Serializable resolve(String launchId, Map<String,String> needs) throws NeedUnmetException {
		
		String auth = getUsername() + ":" + getPassword();
		
		String encodedAuth = new String(Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII"))));
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + encodedAuth);		
		HttpEntity<String> reqAuth = new HttpEntity<String>(headers);
		
		String url = fhirEndpoint + "/_services/smart/Launch/" + launchId;
		
	    RestTemplate restTemplate = new RestTemplate();    
	    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, reqAuth, String.class);
	  
	    JsonObject gotParams = ((JsonObject) new JsonParser().parse(response.getBody()))
	    		.get("parameters")
	    		.getAsJsonObject();
	    
		HashMap<String,String> params = new HashMap<String, String>();
		// TODO check for mismatched needs and throw an exception if found
		// TODO verify that the use who created this context is the same as the
		//      currently authenticated user.
		
		for (Entry<String, JsonElement> need : gotParams.entrySet()) {
			String k = need.getKey();
			if (gotParams.get(k) != null){
				params.put(k, gotParams.get(k).getAsString());				
			}
		}

		return params;
	}

	public String getFhirEndpoint() {
		return fhirEndpoint;
	}

	public void setFhirEndpoint(String fhirEndpoint) {
		this.fhirEndpoint = fhirEndpoint;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
