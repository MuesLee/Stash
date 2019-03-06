package de.ts.stash.security;

import static de.ts.stash.security.SecurityConstants.LOGIN_URL;
import static de.ts.stash.security.SecurityConstants.SIGN_UP_URL;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import de.ts.stash.auth.user.UserDetailsServiceImpl;
import de.ts.stash.security.api.AuthTokenProvider;
import de.ts.stash.security.api.AuthTokenReader;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
	private final UserDetailsServiceImpl userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public WebSecurity(final UserDetailsServiceImpl userDetailsService,
			final BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Bean
	AuthTokenProvider authTokenProvider() {
		return new JwtAuthTokenProvider();
	}

	@Bean
	AuthTokenReader authTokenReader() {
		return new JwtAuthTokenReader();
	}

	@Override
	public void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.userDetailsService).passwordEncoder(this.bCryptPasswordEncoder);
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http
				.cors()
				.and()
				.csrf()
				.disable()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeRequests()
				.antMatchers(HttpMethod.POST, SIGN_UP_URL)
				.permitAll()
				.and()
				.authorizeRequests()
				.antMatchers(HttpMethod.POST, LOGIN_URL)
				.permitAll()
				.and()
				.authorizeRequests()
				.antMatchers(HttpMethod.POST, SecurityConstants.REFRESH_URL)
				.permitAll()
				.anyRequest()
				.authenticated()
				.and()
				.addFilter(new JwtAuthorizationFilter(authenticationManager()));
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Collections.singletonList("*"));
		configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
		// setAllowCredentials(true) is important, otherwise:
		// The value of the 'Access-Control-Allow-Origin' header in the response must
		// not be the wildcard '*' when the request's credentials mode is 'include'.
		configuration.setAllowCredentials(true);
		// setAllowedHeaders is important! Without it, OPTIONS preflight request
		// will fail with 403 Invalid CORS request
		configuration
				.setAllowedHeaders(Arrays
						.asList(SecurityConstants.AUTH_HEADER_STRING, SecurityConstants.REFRESH_HEADER_STRING,
								"Cache-Control", "Content-Type"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}