package org.mitre.openid.connect.token;

import java.util.HashSet;
import java.util.Set;

import org.mitre.oauth2.model.LaunchContextEntity;
import org.springframework.stereotype.Component;

@Component
public class SmartLaunchContextResolver implements LaunchContextResolver {

	@Override
	public Set<LaunchContextEntity> resolve(Set<String> required, Set<String> optional, String contextId) {
		LaunchContextEntity aFakeParam = new LaunchContextEntity();
		aFakeParam.setName("context_patient");
		aFakeParam.setValue("found from -> " +contextId );

		LaunchContextEntity anotherFakeParam = new LaunchContextEntity();
		anotherFakeParam.setName("context_encounter");
		anotherFakeParam.setValue("found from -> " +contextId);

		HashSet<LaunchContextEntity> params = new HashSet<LaunchContextEntity>();
		params.add(aFakeParam);
		params.add(anotherFakeParam);

		return params;
	}

}
