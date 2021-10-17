package code.project.springbootjwt.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CookieAuthorizationFilter extends RememberMeAuthenticationFilter {

	private final RememberMeServices rememberMeServices;
	private final AuthenticationManager authenticationManager;

	public CookieAuthorizationFilter(AuthenticationManager authenticationManager,
									 RememberMeServices rememberMeServices) {
		super(authenticationManager, rememberMeServices);
		this.rememberMeServices = rememberMeServices;
		this.authenticationManager = authenticationManager;
	}

	@Override public void doFilter(
			ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		Authentication rememberMeAuth = rememberMeServices.autoLogin(request, response);
		SecurityContextHolder.getContext().setAuthentication(rememberMeAuth);

		if (rememberMeAuth.getPrincipal() != null) {
			Authentication authentication = authenticationManager.authenticate(rememberMeAuth);
			if (authentication.isAuthenticated()) {
				((BearerToken) authentication).setCookieAuthentified(true);
			}
		}

		chain.doFilter(req, res);
	}
}
