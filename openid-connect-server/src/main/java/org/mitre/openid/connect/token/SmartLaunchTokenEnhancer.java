package org.mitre.openid.connect.token;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,	OAuth2Authentication authentication)  {
		OAuth2AccessTokenEntity ret = (OAuth2AccessTokenEntity) super.enhance(accessToken, authentication);

		Serializable contextId = authentication.getOAuth2Request().getExtensions().get("context_id");
		Set<String> requirements = contextRequirements(authentication);
		Set<LaunchContextEntity> context  = new HashSet<LaunchContextEntity>();
		
		if (contextId != null) {
			context = launchContextResolver.resolve(null, null, contextId.toString());	
		}
		
		Set<String> available = FluentIterable
				.from(context)
				.transform(key)
				.toSet();

		// TODO Would like to throw an exception, but interface doesn't allow.
		// What's the right way to approach this?
		Set<String> missing = Sets.difference(requirements, available);
		if (missing.size() > 0) {
			return null;
		}

		ret.setLaunchContext(context);
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
