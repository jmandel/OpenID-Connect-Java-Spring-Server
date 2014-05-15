package org.mitre.openid.connect;

import java.util.Map;
import java.util.Set;

import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableSet;

@Service
public class SmartOAuth2RequestFactory extends ConnectOAuth2RequestFactory {

	private static final Set<String> fields = new ImmutableSet.Builder<String>()
			.add("context_required")
			.add("context_optional")
			.add("context_id")
			.build();


	@Autowired
	public SmartOAuth2RequestFactory(ClientDetailsEntityService clientDetailsService) {
		super(clientDetailsService);
	}
	
	@Override
	public AuthorizationRequest createAuthorizationRequest(Map<String, String> inputParams) {
		AuthorizationRequest ret = super.createAuthorizationRequest(inputParams);

		for (String f : fields){
			if (inputParams.get(f) != null){
				ret.getExtensions().put(f, inputParams.get(f));
			}
		}

		return ret;
	}

}
