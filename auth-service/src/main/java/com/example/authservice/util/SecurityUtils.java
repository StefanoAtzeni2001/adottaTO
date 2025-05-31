package com.example.authservice.util;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.security.Principal;

/**
 * Utility class for extracting user information from security principal.
 */
public class SecurityUtils {

    /**
     * Extracts the email address from the given Principal object.
     * <p>
     * - If the Principal is an OAuth2AuthenticationToken, extracts the "email" attribute.
     * - Otherwise, returns the principal's name.
     *
     * @param principal the security principal representing the authenticated user
     * @return the user's email address or username
     */
    public static String extractEmail(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken oauth) {
            return oauth.getPrincipal().getAttribute("email");
        } else {
            return principal.getName();
        }
    }
}

