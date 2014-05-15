package org.mitre.openid.connect.token;

import java.util.Set;

import org.mitre.oauth2.model.LaunchContextEntity;

public interface LaunchContextResolver {
	
	public Set<LaunchContextEntity> resolve(Set<String> required, Set<String> optional, String contextId);

}