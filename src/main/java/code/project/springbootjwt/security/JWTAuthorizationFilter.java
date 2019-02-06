package code.project.springbootjwt.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static code.project.springbootjwt.security.SecurityConstants.TOKEN;

public class JWTAuthorizationFilter extends AbstractAuthenticationProcessingFilter {

	private AuthenticationManager authenticateManager;
	private String authRedirectUrl;

	public JWTAuthorizationFilter(
			AuthenticationManager authManager,
			String defaultFilterProcessesUrl,
			String authRedirectUrl) {
		super(defaultFilterProcessesUrl);
		this.authenticateManager = authManager;
		this.authRedirectUrl = authRedirectUrl;
	}

	@Override public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		BearerToken bearerToken = (BearerToken) SecurityContextHolder.getContext().getAuthentication();
		if (bearerToken.isCookieAuthentified()) {
			return bearerToken;
		} else {
			String token = request.getParameter(TOKEN);
			String code = request.getParameter("code");

			// TODO: the following logic is just experimental
			if (token == null) {
				if (code != null) {
					String tok = getToken(code);
					if (tok != null) {
						try {
							response.sendRedirect("http://localhost:8081/tasks"  + "?token=" + tok);
							return null;
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						throw new RememberMeAuthenticationException("No token");
					}
				} else {
					throw new RememberMeAuthenticationException("No token");
				}
			}
			return authenticateManager.authenticate(new BearerToken(token));
		}
	}

	private String getToken(String code) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<TokenRequest> request = new HttpEntity<>(new TokenRequest("authorization_code", "P00ww7P2MD0Ir9hCYPWmYa0QmJNsOh5l", "LU8GC2Ae1dAV25N_Ttitj_VkWRDTBbid3F3q6lP8sD_gQ9QMenxpm1tY_KG0bLWS", code, "http://localhost:8081/tasks"));
		TokenResponse tokenResponse = restTemplate.postForObject("https://ovidiu-lapusan.eu.auth0.com/oauth/token", request, TokenResponse.class);
		return tokenResponse.getAccess_token();
	}

	@Override protected void unsuccessfulAuthentication(
			HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
			throws IOException {
		SecurityContextHolder.getContext().setAuthentication(new BearerToken());
		String redirectUri = request.getParameter("code");

		// not needed now
    	/*if (redirectUri != null) {
			response.sendRedirect(authRedirectUrl + "?redirect-uri=" + request.getRequestURI());
		} else {
			response.sendRedirect(authRedirectUrl);
		}*/
		//auth0.getDomain();

    	response.sendRedirect(getRedirectUrl(request));
	}

	@Override protected final void successfulAuthentication(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain,
			Authentication authResult)
			throws IOException, ServletException {
		response.addCookie(CookieUtil.createHTTPOnlyCookie(HttpHeaders.AUTHORIZATION, request.getParameter(TOKEN)));
		SecurityContextHolder.getContext().setAuthentication(authResult);
		chain.doFilter(request, response);
	}

	@Override public void doFilter(
			ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		BearerToken bearerToken = (BearerToken) SecurityContextHolder.getContext().getAuthentication();

		if (!requiresAuthentication(request, response) || bearerToken.isCookieAuthentified()) {
			chain.doFilter(request, response);
			return;
		}

		Authentication authResult;
		try {
			authResult = attemptAuthentication(request, response);
			if (authResult == null) {
				return;
			} else if (authResult.isAuthenticated() == false) {
				throw new AuthenticationServiceException("Not authenticated");
			} else {
				successfulAuthentication(request, response, chain, authResult);
			}
		} catch (InternalAuthenticationServiceException failed) {
			logger.error("An internal error occurred while trying to authenticate the user.", failed);
			unsuccessfulAuthentication(request, response, failed);
			return;
		} catch (AuthenticationException failed) {
			unsuccessfulAuthentication(request, response, failed);
		}
	}

	private String getRedirectUrl(HttpServletRequest request) {
		String redirectUri =
				request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/tasks";

		String authUrl = "https://" + "ovidiu-lapusan.eu.auth0.com/" + "authorize?";

		Map<String, String> parameters = new HashMap<>();
		parameters.put("response_type", "code");
		parameters.put("client_id", "P00ww7P2MD0Ir9hCYPWmYa0QmJNsOh5l");
		parameters.put("client_secret", "LU8GC2Ae1dAV25N_Ttitj_VkWRDTBbid3F3q6lP8sD_gQ9QMenxpm1tY_KG0bLWS");
		parameters.put("redirect_uri", redirectUri);

		String params = getParamsString(parameters);
		return authUrl.concat(params);
	}

	private String getRedirectUrl2(HttpServletRequest request, String code) {
		String redirectUri =
				request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/tasks";

		String authUrl = "https://" + "ovidiu-lapusan.eu.auth0.com/" + "oauth/token?";

		Map<String, String> parameters = new HashMap<>();
		parameters.put("code", code);
		parameters.put("client_id", "P00ww7P2MD0Ir9hCYPWmYa0QmJNsOh5l");
		parameters.put("client_secret", "LU8GC2Ae1dAV25N_Ttitj_VkWRDTBbid3F3q6lP8sD_gQ9QMenxpm1tY_KG0bLWS");
		parameters.put("redirect_uri", redirectUri);
		parameters.put("grant_type", "authorization_code");

		String params = getParamsString(parameters);
		return authUrl.concat(params);
	}

	private String getParamsString(Map<String, String> params) {
		StringBuilder result = new StringBuilder();

		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				result.append("=");

				result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
				result.append("&");
			}
			//result.setLength(result.length() - 1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String resultString = result.toString();
		return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
	}

}