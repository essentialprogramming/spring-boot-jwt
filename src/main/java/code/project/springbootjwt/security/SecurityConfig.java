package code.project.springbootjwt.security;

import code.project.springbootjwt.model.ApplicationUser;
import code.project.springbootjwt.repository.ApplicationUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;

import static code.project.springbootjwt.security.SecurityConstants.LOGIN_URL;
import static code.project.springbootjwt.security.SecurityConstants.TASKS_URL;

public class SecurityConfig {

	@Configuration
	@Order(1)
	public static class JWTConfig extends WebSecurityConfigurerAdapter {

		private final BCryptPasswordEncoder bCryptPasswordEncoder;
		private final ApplicationUserRepository applicationUserRepository;

		public JWTConfig(BCryptPasswordEncoder bCryptPasswordEncoder, ApplicationUserRepository applicationUserRepository) {
			this.applicationUserRepository = applicationUserRepository;
			this.bCryptPasswordEncoder = bCryptPasswordEncoder;
			initUserRepository();
		}

		void initUserRepository() {
			ApplicationUser user = new ApplicationUser();
			user.setUsername("test");
			user.setPassword(bCryptPasswordEncoder.encode("test"));
			applicationUserRepository.save(user);
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.antMatcher(TASKS_URL)
					.cors().disable()
					.addFilter(new CookieAuthorizationFilter(getAuthenticationManager(), getRememberMe()))
					.addFilterAfter(new JWTAuthorizationFilter(getAuthenticationManager(), TASKS_URL, LOGIN_URL), CookieAuthorizationFilter.class)
					.rememberMe().alwaysRemember(true);
		}

		@Bean
		public AuthenticationManager getAuthenticationManager() {
			return new TokenVerifier();
		}

		@Bean
		public RememberMeServices getRememberMe() {
			return new CookieBasedRememberMe();
		}
	}

	@Configuration
	@Order(2)
	public static class AuthenticatorConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.httpBasic()
					.and()
					.authorizeRequests().antMatchers(LOGIN_URL).authenticated()
					.and()
					.addFilter(new JWTAuthenticationFilter(authenticationManager()))
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		}
	}
}
