package com.example.authservice.constants;

/**
 * Contains constants for URL endpoints used in authentication and authorization flows.
 */
public final class UserEndpoints {

    /** URL to redirect users after successful OAuth2 authentication */
    public static final String GOOGLE_REGISTRATION = "/google-registration";

    /** URL to programmatic login  */
    public static final String LOGIN = "/login";

    /** URL to user registration. */
    public static final String REGISTER = "/register";

    /** URL to retrieve a JWT token after OAuth2 login. */
    public static final String OAUTH_JWT = "/oauth-jwt";

    /** URL to get a user profile of a not authenticated user by user ID */
    public static final String GET_USER_BY_ID = "/get/profile/{id}";



    /** URL to retrieve the user's info to send emails */
    public static final String GET_PROFILE_EMAIL = "/get/email/{id}";

    /** URL to update user profile data. */
    public static final String UPDATE_PROFILE = "/my-profile/update";

    /** URL to get a user profile of an authenticated user by user ID */
    public static final String GET_MY_PROFILE = "/my-profile";


    /**
     * Private constructor to prevent instantiation of this utility class
     */
    private UserEndpoints() {}
}

