package code.project.springbootjwt.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static code.project.springbootjwt.security.SecurityConstants.EXPIRATION_TIME;
import static code.project.springbootjwt.security.SecurityConstants.LOGIN_URL;


public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res,
                                              Authentication auth) throws IOException {
        String token = TokenUtil.createToken(String.valueOf(auth.getPrincipal()), new Date(System.currentTimeMillis() + EXPIRATION_TIME));
        String redirectUri = req.getParameter("redirect-uri");
        if (redirectUri != null && !redirectUri.equals("")) {
            res.sendRedirect(redirectUri + "?token=" + token);
        } else {
            res.sendRedirect(LOGIN_URL);
        }
    }
}