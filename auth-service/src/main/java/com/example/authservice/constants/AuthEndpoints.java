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

    /** URL per eseguire il logout dell'utente */
    public static final String LOGOUT_PAGE = "/logout";

    /** URL della pagina di registrazione per nuovi utenti */
    public static final String REGISTER_PAGE = "/register";

    /** URL della pagina utente dopo l'accesso riuscito con form login */
    public static final String USER_PAGE = "/userpage";

    /** URL di reindirizzamento dopo autenticazione OAuth2 (es. Google) */
    public static final String GOOGLE_REGISTRATION = "/googleRegistration";

    /**
     * Costruttore privato per evitare l'istanziazione della classe utility.
     */
    private AuthEndpoints() {}
}

