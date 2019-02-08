package code.project.springbootjwt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

public class TokenUtil {

	public static Optional<Claims> parse(String token) {
		if (token == null) {
			return Optional.empty();
		}
		try {
			return Optional.of(
					Jwts.parser()
							.setSigningKey(Base64.getDecoder().decode(SecurityConstants.SECRET))
							.parseClaimsJws(token).getBody());
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public static String createToken(String subject, Date expirationDate) {
		//The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = Base64.getEncoder().encode(SecurityConstants.SECRET.getBytes());
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		//Let's set the JWT Claims
		return Jwts.builder()
				.setSubject(subject)
				.setIssuedAt(now)
				.setIssuer("issuer")
				.setExpiration(expirationDate)
				.signWith(signatureAlgorithm, signingKey)
				.compact();
	}

}
