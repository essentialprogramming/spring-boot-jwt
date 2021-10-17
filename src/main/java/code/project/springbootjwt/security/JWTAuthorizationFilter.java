package code.project.springbootjwt.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static code.project.springbootjwt.security.SecurityConstants.TOKEN;

public class JWTAuthorizationFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthenticationManager authenticateManager;
    private final String authRedirectUrl;

    public JWTAuthorizationFilter(AuthenticationManager authManager, String defaultFilterProcessesUrl, String authRedirectUrl) {
        super(defaultFilterProcessesUrl);
        this.authenticateManager = authManager;
        this.authRedirectUrl = authRedirectUrl;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        BearerToken bearerToken = (BearerToken) SecurityContextHolder.getContext().getAuthentication();
        if (bearerToken.isCookieAuthentified()) {
            return bearerToken;
        } else {
            String token = request.getParameter(TOKEN);
            if (token == null) {
                throw new RememberMeAuthenticationException("No token");
            }
            return authenticateManager.authenticate(new BearerToken(token));
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        SecurityContextHolder.getContext().setAuthentication(new BearerToken());
        String redirectUri = request.getRequestURI();
        if (redirectUri != null) {
            response.sendRedirect(authRedirectUrl + "?redirect-uri=" + request.getRequestURI());
        } else {
            response.sendRedirect(authRedirectUrl);
        }
    }

    @Override
    protected final void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain chain, Authentication authResult) throws IOException, ServletException {
        response.addCookie(CookieUtil.createHTTPOnlyCookie(HttpHeaders.AUTHORIZATION, request.getParameter(TOKEN)));
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
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
            } else {
                successfulAuthentication(request, response, chain, authResult);
            }
        } catch (InternalAuthenticationServiceException failed) {
            logger.error("An internal error occurred while trying to authenticate the user.", failed);
            unsuccessfulAuthentication(request, response, failed);

        } catch (AuthenticationException failed) {
            unsuccessfulAuthentication(request, response, failed);
        }
    }
}