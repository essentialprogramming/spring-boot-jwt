package code.project.springbootjwt.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class CookieBasedRememberMe implements RememberMeServices {

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        return CookieUtil.extractCookieValueByName(request.getCookies(), AUTHORIZATION)
                .map(BearerToken::new)
                .orElse(new BearerToken());
    }

    @Override
    public void loginFail(HttpServletRequest request, HttpServletResponse response) {
        // do nothing
    }

    @Override
    public void loginSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        // do nothing
    }
}
