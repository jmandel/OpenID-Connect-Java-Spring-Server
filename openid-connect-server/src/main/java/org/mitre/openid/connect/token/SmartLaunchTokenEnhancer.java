package org.mitre.openid.connect.token;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.mitre.oauth2.model.LaunchContextEntity;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

@Component
public class SmartLaunchTokenEnhancer extends ConnectTokenEnhancer {

	@Autowired
	private LaunchContextResolver launchContextResolver;

	Function<LaunchContextEntity, String> key = new Function<LaunchContextEntity, String>() {
		@Override
		public String apply(LaunchContextEntity input) {
			return input.getName();
		}				
	};


	Function<Entry<String,String>, LaunchContextEntity> toLaunchContextEntity = new Function<Entry<String,String>, LaunchContextEntity>() {
		@Override
		public LaunchContextEntity apply(Entry<String, String> input) {
			LaunchContextEntity e = new LaunchContextEntity();
			e.setName(input.getKey());
			e.setValue(input.getValue());
			return e;
		}	
	};
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,	OAuth2Authentication authentication)  {
		OAuth2AccessTokenEntity ret = (OAuth2AccessTokenEntity) super.enhance(accessToken, authentication);
	
		@SuppressWarnings("unchecked")
		Map<String,String> contextMap = (HashMap<String,String>) authentication.getOAuth2Request().getExtensions().get("launch_context");

		if (contextMap == null) {
		  return ret;
		}
		
		Set<LaunchContextEntity> context = FluentIterable
				.from(contextMap.entrySet())
				.transform(toLaunchContextEntity)
				.toSet();

		ret.setLaunchContext(new HashSet<LaunchContextEntity>(context));
		return ret;
	}

	private Set<String> contextRequirements(OAuth2Authentication authentication) {
		Set<String> contextRequired = new HashSet<String>();
		if (authentication.getOAuth2Request().getExtensions().get("context_required") != null) {
			for (String s : authentication.getOAuth2Request().getExtensions().get("context_required").toString().split(" ")) {
				contextRequired.add("context_"+s);
			}
		}
		return contextRequired;
	}
}
