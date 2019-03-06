package de.ts.stash.security;

public class SecurityConstants {
	public static final String SECRET = "akjDNAsdnasd,masdnNLNSDna,smdas 123123 kadasdä#aöd,öa";
	public static final long ACCESS_TOKEN_EXPIRATION_IN_MINUTES = 10;
	public static final int REFRESH_TOKEN_EXPIRATION_IN_DAYS = 10;
	public static final int REFRESH_TOKEN_EXPIRATION_IN_SECONDS = REFRESH_TOKEN_EXPIRATION_IN_DAYS * 24 * 60 * 60;
	public static final String ACCESS_TOKEN_PREFIX = "Bearer ";
	public static final String AUTH_HEADER_STRING = "Authorization";
	public static final String REFRESH_COOKIE_NAME = "Refreshtoken";
	public static final String SIGN_UP_URL = "/users/sign-up";
	public static final String LOGIN_URL = "/users/login";
	public static final String REFRESH_URL = "/users/refresh";
}