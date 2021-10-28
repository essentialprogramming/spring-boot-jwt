package code.project.springbootjwt.security;

public class SecurityConstants {
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = 60000; // 1 minute = 60000 ms
    public static final String LOGIN_URL = "/users/login";
    public static final String TASKS_URL = "/tasks";
    public static final String TOKEN = "token";
}
