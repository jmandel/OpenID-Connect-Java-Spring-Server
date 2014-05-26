package org.mitre.openid.connect.web;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SessionAttributes("authorizationRequest")
@RequestMapping(value = "/authorize")
public class SmartAuthorizationEndpoint extends AuthorizationEndpoint implements
		InitializingBean {

	public String SMART_SCOPE_PREFIX = "smart:";

	private Predicate<String> isSmartScope = new Predicate<String>() {
		public boolean apply(String string) {
			return string.startsWith(SMART_SCOPE_PREFIX);
		}
	};

	private Function<String, JsonObject> toJson = new Function<String, JsonObject>() {
		public JsonObject apply(String s) {
			return new JsonParser().parse(
					s.substring(SMART_SCOPE_PREFIX.length())).getAsJsonObject();
		}
	};

	private Predicate<JsonObject> needsContext = new Predicate<JsonObject>() {
		public boolean apply(JsonObject e) {
			if (e.get("contextId") == null
					|| e.get("contextId").isJsonPrimitive() == false) {
				return true;
			}
			return false;
		}
	};

	@RequestMapping
	@Override
	public ModelAndView authorize(Map<String, Object> model,
			@RequestParam Map<String, String> parameters,
			SessionStatus sessionStatus, Principal principal) {
		
		 ModelAndView mv  =  super.authorize(model, parameters, sessionStatus, principal);

   		 AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
  
   		
		if (authorizationRequest!= null && authorizationRequest.getExtensions().containsKey("external_launch_required")) {
			return new ModelAndView(new RedirectView(
					"https://fhir.me?" + authorizationRequest.getExtensions().get("external_launch_request").toString()));
		}
		
		 
		 // Plan: for SMART requests that need patient-level context, redirect to a patient-picker, passing CSRF token
		 // For SMART request that don't need patient-level context, redirect to a simplified approval screen
		 // be sure to have automatic approval of the trusted context-picking app, so it always knows the current user.

		 return mv;
	}
	
}
