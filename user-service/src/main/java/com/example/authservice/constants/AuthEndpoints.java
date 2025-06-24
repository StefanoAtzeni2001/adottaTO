package com.example.authservice.constants;

/**
 * Contains constants for URL endpoints used in authentication and authorization flows.
 */
public final class AuthEndpoints {

    /** URL to redirect users after successful OAuth2 authentication */
    public static final String GOOGLE_REGISTRATION = "/google-registration";

    /** URL to programmatic login  */
    public static final String API_LOGIN = "/api/login";

    /** URL to user registration. */
    public static final String API_REGISTER = "/api/register";

    /** URL to retrieve the current user's profile information. */
    public static final String PROFILE = "/profile";

    /** URL to retrieve the user's info to send emails */
    public static final String PROFILE_EMAIL = "/profile-email";

    /** URL to update user profile data. */
    public static final String API_PROFILE_UPDATE = "/api/profile-update";

    /** URL to get a user profile of a not authenticated user by user ID */
    public static final String GET_USER_BY_ID = "/api/profile/{id}";

    /** URL to retrieve a JWT token after OAuth2 login. */
    public static final String API_OAUTH_JWT = "/api/oauth-jwt";

    /**
     * Private constructor to prevent instantiation of this utility class
     */
    private AuthEndpoints() {}
}

