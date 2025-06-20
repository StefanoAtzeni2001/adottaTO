package com.example.authservice.constants;

/**
 * Contiene le costanti delle URL utilizzate nei flussi di autenticazione e autorizzazione.
 * <p>
 * Questa classe centralizza i percorsi delle principali rotte coinvolte in:
 * - login tramite form e OAuth2
 * - registrazione utente
 * - accesso post-login
 * - logout
 */
public final class AuthEndpoints {

    /** URL della pagina di login per utenti e OAuth2 */
    public static final String LOGIN_PAGE = "/login";

    /** URL di reindirizzamento dopo autenticazione OAuth2 (es. Google) */
    public static final String GOOGLE_REGISTRATION = "/google-registration";

    public static final String API_LOGIN = "/api/login";

    public static final String API_REGISTER = "/api/register";

    public static final String PROFILE = "/profile";

    public static final String PROFILE_EMAIL = "/profile-email";

    public static final String API_PROFILE_UPDATE = "/api/profile/update";

    public static final String API_OAUTH_JWT = "/api/oauth-jwt";

    /**
     * Costruttore privato per evitare l'istanziazione della classe utility.
     */
    private AuthEndpoints() {}
}

