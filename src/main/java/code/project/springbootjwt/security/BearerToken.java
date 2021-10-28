package code.project.springbootjwt.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class BearerToken extends AbstractAuthenticationToken{

	private static final long serialVersionUID = 1L;
	private final String bearer;
	private boolean isPresentInCookie = false;

	public BearerToken() {
		super(null);
		this.bearer = null;
		setAuthenticated(false);
	}
	
	public BearerToken(String token) {
		super(null);
		this.bearer = token;
	}

	@Override
	public Object getCredentials() {
		return "No password on JWT Authorization Flow";
	}

	@Override
	public Object getPrincipal() {
		return "Principal";
	}

	public boolean isPresentInCookie() {
		return isPresentInCookie;
	}

	public boolean isTokenPresent(){
		return bearer != null;
	}

	public String getJWT(){
		return bearer;
	}

	public void setPresentInCookie(boolean presentInCookie) {
		isPresentInCookie = presentInCookie;
	}
}
