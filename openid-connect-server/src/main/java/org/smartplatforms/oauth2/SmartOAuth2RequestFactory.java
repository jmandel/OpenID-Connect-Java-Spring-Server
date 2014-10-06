package org.smartplatforms.oauth2;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.mitre.openid.connect.ConnectOAuth2RequestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;

import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

@Service
public class SmartOAuth2RequestFactory extends ConnectOAuth2RequestFactory {

	@Autowired
	private LaunchContextResolver launchContextResolver;

	Predicate<String> isLaunchContext = new Predicate<String>() {
		@Override
		public boolean apply(String input) {
			return input.startsWith("launch");
		}
	};

	@Autowired
	public SmartOAuth2RequestFactory(
			ClientDetailsEntityService clientDetailsService) {
		super(clientDetailsService);
	}

	@Override
	public AuthorizationRequest createAuthorizationRequest(Map<String, String> inputParams) {
		AuthorizationRequest ret = super.createAuthorizationRequest(inputParams);

		HashMap<String, String> launchReqs = new HashMap<String, String>();
		
		for (Entry<String, String> e : FluentIterable.from(ret.getScope())
				.filter(isLaunchContext).transform(toMapEntry)) {
			launchReqs.put(e.getKey(), e.getValue());
		}

		boolean requestingLaunch = launchReqs.size() > 0;
		
		String launchId = launchReqs.remove("launch");
		if (launchId != null) {
			try {
				ret.getExtensions().put("launch_context", launchContextResolver.resolve(launchId, launchReqs));
			} catch (NeedUnmetException e1) {
				ret.getExtensions().put("invalid_launch", "Couldn't resolve launch id: " + launchId);
			}
		} else if (requestingLaunch) { // asking for launch, but no launch ID provided
			ret.getExtensions().put("external_launch_required", launchReqs);
		}

		ret.setScope(Sets.difference(ret.getScope(),
				FluentIterable.from(ret.getScope()).filter(isLaunchContext)
						.toSet()));

		if (launchId != null) {
			Set<String> plusLaunch = new HashSet<String>(ret.getScope());
			plusLaunch.add("launch");
			ret.setScope(plusLaunch);
		}
		
		return ret;
	}

	Function<String, String> withRequestedValue = new Function<String, String>() {

		@Override
		public String apply(String input) {
			// TODO Auto-generated method stub
			return null;
		}

	};

	Function<String, Entry<String, String>> toMapEntry = new Function<String, Map.Entry<String, String>>() {
		@Override
		public Entry<String, String> apply(String input) {
			String[] parts = input.split(Pattern.quote(":"), 2);
			if (parts.length == 1) {
				return new AbstractMap.SimpleEntry<String, String>(parts[0],
						null);
			}
			return new AbstractMap.SimpleEntry<String, String>(parts[0],
					parts[1]);

		}
	};

}
