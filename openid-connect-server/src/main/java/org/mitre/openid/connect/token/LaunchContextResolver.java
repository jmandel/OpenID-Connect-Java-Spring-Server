package org.mitre.openid.connect.token;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.mitre.oauth2.model.LaunchContextEntity;

public interface LaunchContextResolver {
	public Serializable resolve(String launchId, Map<String,String> needs) throws NeedUnmetException;
}