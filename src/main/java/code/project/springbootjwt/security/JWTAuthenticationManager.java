package code.project.springbootjwt.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.function.Function;

public class JWTAuthenticationManager implements AuthenticationManager {

	private final Function<Object, Boolean> isTokenValid = (token -> TokenUtil.parse(String.valueOf(token))
			.map(claims -> true) // for now just check if was parsed successfully
			.orElse(false));

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		BearerToken token = (BearerToken) authentication;
		authentication.setAuthenticated(isTokenValid.apply(token.getJWT()));
		return authentication;
	}
}
