package code.project.springbootjwt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TokenUtil {

	private static final Logger logger = Logger.getLogger(TokenUtil.class.getName());

	public static Optional<Claims> parse(String token) {
		String certificate =
				"-----BEGIN CERTIFICATE-----\n" + "MIIDETCCAfmgAwIBAgIJEoDPHJOeLJMgMA0GCSqGSIb3DQEBCwUAMCYxJDAiBgNV\n"
						+ "BAMTG292aWRpdS1sYXB1c2FuLmV1LmF1dGgwLmNvbTAeFw0xOTAxMDkwODM3NDha\n"
						+ "Fw0zMjA5MTcwODM3NDhaMCYxJDAiBgNVBAMTG292aWRpdS1sYXB1c2FuLmV1LmF1\n"
						+ "dGgwLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMxu3cKxjNF+\n"
						+ "rxN1XKv7h62h08z5wYuNIJGok9ksJwVZ6ZHfSxQcs1tb4GJyyrTSnnkokA0/krZP\n"
						+ "5UzedF0Z9n9t9dk6cPde5d23pECeo+0nw74qp5yVo4YYrcBhaC8BtZizFIE51d/+\n"
						+ "0KtCKu1mAkydOa0V5eROGZHvxdV3B941iQy3g67wJ+tjikc4YfOMtY6sRJRAD+nW\n"
						+ "1wJQo7wEkcF9oHRf6CBqnA8vUpR5t3Too7yviDIq0SLptjxpe08j9JJT4/xKuZwB\n"
						+ "xFNj09i6wJ8udaXaWRWj6gtoDy7bwDD4ge8Wf9UEU+sf2+CBw0J19NmwzkcyDZxo\n"
						+ "e2HIUMyihQkCAwEAAaNCMEAwDwYDVR0TAQH/BAUwAwEB/zAdBgNVHQ4EFgQUdQLJ\n"
						+ "q35xhltU7Ex7J/tN4XU1PXIwDgYDVR0PAQH/BAQDAgKEMA0GCSqGSIb3DQEBCwUA\n"
						+ "A4IBAQBde7/HulClB1vwyMqtcsMXi57h1SDbGdQ2ULJlSaScyfGUpvBDSre7STif\n"
						+ "KyqWfco+tLd1zTR04uezFFBkV6R9Zu6wGKIl0sZ5s5aoXFE1o0emrN77kx7TVIDq\n"
						+ "7vQZOdMwJQ2v8SCyqIszu61UXuZ1LCzES4nA1+8ME2ht/S7yntCE7z2g5vZbmQ9D\n"
						+ "f63ObuHCzmL2xS6BNBGvvD4IAyOQm4fyRuu71KY1YylVh5J0Lt+C972mBL6pg4ue\n"
						+ "3nsZy1ArlFnwdO807dZHrxOrJ6l1TM33/MXVvQsxHAp+QSq0YRe7O3+kUqTdSldI\n"
						+ "eXmLTqIbQqiApfRy0+B4Zh15hI+2\n" + "-----END CERTIFICATE-----\n";
		PublicKey publicKey = getKey(certificate);
		if (token == null) {
			return Optional.empty();
		}
		try {
			return Optional.of(Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody());
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public static PublicKey getKey(String key) {
		try {
			CertificateFactory fact = CertificateFactory.getInstance("X.509");
			InputStream is = new ByteArrayInputStream(key.getBytes(StandardCharsets.UTF_8));
			X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
			PublicKey pKey = cer.getPublicKey();
			return pKey;
		} catch (Exception e) {
			logger.log(Level.SEVERE,
					"An internal error occurred when trying to parse public certificate from authorization server");
		}

		return null;
	}

	public static String createToken(String subject, Date expirationDate) {
		//The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		//We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SecurityConstants.SECRET);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		//Let's set the JWT Claims
		return Jwts.builder().setSubject(subject).setIssuedAt(now).setIssuer("issuer").setExpiration(expirationDate)
				.signWith(signatureAlgorithm, signingKey).compact();
	}

}
