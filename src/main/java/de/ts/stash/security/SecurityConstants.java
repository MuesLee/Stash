package de.ts.stash.security;

public class SecurityConstants {
	public static final String SECRET = "akjDNAsdnasd,masdnNLNSDna,smdas 123123 kadasdä#aöd,öa";
	public static final long EXPIRATION_TIME = 864_000_000; // 10 days
	public static final long REFRESH_TOKEN_EXPIRATION_TIME = 864_000_000; // 10 days
	public static final String ACCESS_TOKEN_PREFIX = "Bearer ";
	public static final String AUTH_HEADER_STRING = "Authorization";
	public static final String REFRESH_HEADER_STRING = "Refreshtoken";
	public static final String SIGN_UP_URL = "/users/sign-up";
	public static final String LOGIN_URL = "/users/login";
	public static final String REFRESH_URL = "/users/refresh";
}