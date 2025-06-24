package com.example.authservice.controller;

import com.example.authservice.dto.AuthRegisterRequestDTO;
import com.example.authservice.dto.JwtResponseDTO;
import com.example.authservice.dto.LoginRequestDTO;
import com.example.authservice.model.Auth;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.authservice.constants.AuthEndpoints.*;

/**
 * Controller responsible for handling authentication-related operations:
 * - Custom login page
 * - Manual registration
 * - Google OAuth2 registration
 */
@Controller
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    /**
     * Handles programmatic login via API credentials.
     *
     * @param request  login credentials (email and password)
     * @param response servlet response used to set cookies
     * @return a redirect URL if login is successful, otherwise 401 Unauthorized
     */
    @PostMapping(API_LOGIN)
    @ResponseBody
    public ResponseEntity<?> apiLogin(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
        Auth userAuth = authService.findByEmail(request.getEmail()).orElse(null);
        if (userAuth == null || !authService.checkPassword(request.getPassword(), userAuth.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenziali non valide"));
        }

        response.addHeader("Set-Cookie", "oauth_user_id=" + userAuth.getId() + "; Path=/; HttpOnly; SameSite=Lax");
        return ResponseEntity.ok().body(Map.of("redirectUrl", "http://localhost:3000/oauth-redirect"));
    }

    /**
     * Handles user registration through API (standard registration).
     *
     * @param request registration data (email, password, etc.)
     * @return a success message or 409 Conflict if email is already in use
     */
    @PostMapping(value = API_REGISTER, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> registerViaApi(
            @RequestPart("request") @Valid AuthRegisterRequestDTO request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            String base64Image = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
                System.out.println(base64Image);
                request.setProfilePicture(base64Image);
            }
            authService.register(request);
            return ResponseEntity.ok("Utente registrato con successo");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email già registrata");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante l'elaborazione dell'immagine");
        }
    }

    /**
     * Handles Google OAuth2 login and registration
     * If the user does not exist, they will be registered using OAuth2 data
     * A secure cookie is set with the user's ID for subsequent JWT generation
     *
     * @param token    OAuth2 token containing authenticated user info
     * @param response HTTP response to set cookies and redirects
     * @throws IOException if redirect fails
     */
    @GetMapping(GOOGLE_REGISTRATION)
    @Transactional
    public void registerGoogleUser(OAuth2AuthenticationToken token, HttpServletResponse response) throws IOException {
        String email = token.getPrincipal().getAttribute("email");
        String name = token.getPrincipal().getAttribute("given_name");
        String surname = token.getPrincipal().getAttribute("family_name");
        String profilePicture = token.getPrincipal().getAttribute("picture");


        authService.registerGoogleUserIfNecessary(email, name, surname, profilePicture);

        Auth userAuth = authService.findByEmail(email).orElse(null);
        if (userAuth == null) {
            response.sendRedirect("http://localhost:3000/login");
            return;
        }

        response.addHeader("Set-Cookie", "oauth_user_id=" + userAuth.getId() + "; Path=/; HttpOnly; SameSite=Lax");
        response.sendRedirect("http://localhost:3000/oauth-redirect");
    }

    /**
     * Issues a JWT token based on a cookie set after OAuth2 login.
     *
     * @param userId user ID from the `oauth_user_id` cookie
     * @return JWT token or appropriate error response
     */
    @GetMapping(API_OAUTH_JWT)
    @ResponseBody
    public ResponseEntity<?> getJwtFromOauthCookie(@CookieValue(value = "oauth_user_id", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Cookie mancante"));
        }

        Long id;
        try {
            id = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "ID utente non valido"));
        }

        Auth userAuth = authService.findById(id).orElse(null);
        if (userAuth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Utente non trovato"));
        }

        String token = jwtService.generateToken(String.valueOf(userAuth.getId()));
        return ResponseEntity.ok(new JwtResponseDTO(token, userAuth.getId()));
    }
}
