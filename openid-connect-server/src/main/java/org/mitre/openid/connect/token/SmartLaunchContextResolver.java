package org.mitre.openid.connect.token;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bouncycastle.jcajce.provider.digest.GOST3411.HashMac;
import org.mitre.oauth2.model.LaunchContextEntity;
import org.springframework.stereotype.Component;

@Component
public class SmartLaunchContextResolver implements LaunchContextResolver {


	@Override
	public Serializable resolve(String launchId, Map<String,String> needs) throws NeedUnmetException {
		
		HashMap<String,String> params = new HashMap<String,String>();
		
		// TODO check for mismatched needs and throw an exception if found
		// TODO verify that the use who created this context is the same as the
		//      currently authenticated user.
		if (needs.containsKey("launch/patient")){
			params.put("launch_patient", "patient found from -> " +launchId );
		}

		if (needs.containsKey("launch/encounter")){
			params.put("launch_encounter", "encounter found from -> " +launchId );
		}


		return params;
	}

}
