package com.example.authservice.util;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.security.Principal;

public class SecurityUtils {

    public static String extractEmail(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken oauth) {
            return oauth.getPrincipal().getAttribute("email");
        } else {
            return principal.getName();
        }
    }

    public static String extractName(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken oauth) {
            return oauth.getPrincipal().getAttribute("given_name");
        }
        return null;
    }

    public static String extractSurname(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken oauth) {
            return oauth.getPrincipal().getAttribute("family_name");
        }
        return null;
    }
}
