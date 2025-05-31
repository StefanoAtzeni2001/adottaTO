# Used Modules:
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL Driver
- Spring Boot DevTools
- Lombok

---
# UserService Project

A Spring Boot authentication service implementing user registration, login, OAuth2 (Google) login, user profile management, and JWT token generation.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Class and Method Summary](#class-and-method-summary)
- [Endpoints and URLs](#endpoints-and-urls)
- [Ports and Access](#ports-and-access)

---

## Project Overview

This project provides a simple authentication and user profile management system with support for:

- Local user registration and login
- OAuth2 login using Google
- JWT token generation and validation
- User profile viewing and editing
- Secure access to pages based on authentication

---

## Class and Method Summary

### 1. `com.example.authservice.controller.AuthController`

Handles web requests for authentication-related pages.

- `login()`  
  Returns the custom login page view.

- `registerForm()`  
  Returns the registration form page view.

- `register(email, password, name, surname)`  
  Handles user registration; creates a user account and redirects accordingly.

- `registerGoogleUser(token)`  
  Handles OAuth2 Google login and registers user if necessary.

---

### 2. `com.example.authservice.controller.UserProfileController`

Manages user profile related web requests.

- `userPage(model, principal)`  
  Displays the user's profile page, redirecting to log in if not authenticated.

- `editProfile(model, principal)`  
  Returns the profile edit form populated with current user data.

- `updateProfile(id, name, surname)`  
  Updates user's profile data and redirects back to user page.

- `getUserProfileFromPrincipal(principal)`  
  Helper method to retrieve UserProfile from the authenticated principal.

---

### 3. `com.example.authservice.config.SecurityConfig`

Configures Spring Security for the application.

- `configureSecurity(HttpSecurity http)`  
  Main security filter chain configuration allowing public access to log in and registration, requiring authentication elsewhere, enabling form login, OAuth2 login, logout, and disabling CSRF.

- `configureFormLogin(HttpSecurity http)`  
  Configures form-based login page, success, and failure URLs.

- `configureOAuth2Login(HttpSecurity http)`  
  Configures OAuth2 login with Google and success redirects URL.

- `configureLogout(HttpSecurity http)`  
  Configures logout URL and post-logout redirection.

- `passwordEncoder()`  
  Provides a BCrypt password encoder bean.

---

### 4. `com.example.authservice.dto.AuthRegisterRequestDTO`

Data Transfer Object for user registration requests.

- Fields: `email`, `password`, `name`, `surname`

---

### 5. `com.example.authservice.dto.AuthRegisterResponseDTO`

Data Transfer Object for responses after user registration.

- Fields: `id`, `email`

---

### 6. `com.example.authservice.model.Auth`

Entity representing user authentication credentials.

- Fields:
    - `id`
    - `email` (validated as email, unique, not null)
    - `password` (not null)
    - `provider` (e.g. `"local"`, `"google"`)

---

### 7. `com.example.authservice.model.UserProfile`

Entity representing user profile information.

- Fields:
    - `id` (shared a primary key with Auth)
    - `auth` (linked Auth entity)
    - `email` (unique, not null)
    - `name` (not null)
    - `surname` (not null)

---

### 8. `com.example.authservice.repository.AuthRepository`

JPA repository interface for `Auth` entity.

- `findByEmail(String email)`  
  Finds user authentication by email.

---

### 9. `com.example.authservice.repository.UserProfileRepository`

JPA repository interface for `UserProfile` entity.

- `findByEmail(String email)`  
  Finds user profile by email.

---

### 10. `com.example.authservice.service.JwtService`

Manages JSON Web Token (JWT) generation and validation.

- `generateToken(String email)`  
  Creates a signed JWT with 1 day expiration.

- `extractEmail(String token)`  
  Extracts email (subject) from JWT token.

- `isTokenValid(String token, String email)`  
  Validates token for email and expiration.

- `isTokenExpired(String token)`  
  Checks if the token is expired.

---

### 11. `com.example.authservice.service.AuthService`

Service managing authentication, user registration, and user profile retrieval.

- `loadUserByUsername(String email)`  
  Loads user details for Spring Security authentication.

- `register(AuthRegisterRequestDTO request)`  
  Registers a new local user, saves credentials and profile.

- `findOrCreateAuthByEmail(String email)`  
  Finds or creates a Google OAuth user.

- `findByEmail(String email)`  
  Finds user authentication by email.

- `registerGoogleUserIfNecessary(String email, String name, String surname)`  
  Registers Google user profile if missing.

- `getUserProfileByEmail(String email)`  
  Retrieves user profile by email.

---

### 12. `com.example.authservice.util.SecurityUtils`

Utility class for extracting information from Spring Security principals.

- `extractEmail(Principal principal)`  
  Extracts email from `OAuth2AuthenticationToken` or fallback to principal name.

---

## Endpoints and URLs

| Endpoint URL       | HTTP Method | Description                         | Access                |
|--------------------|-------------|-------------------------------------|-----------------------|
| `/login`           | GET         | Custom login page                   | Public                |
| `/register`        | GET         | Registration form page              | Public                |
| `/register`        | POST        | Handle user registration            | Public                |
| `/register/google` | GET         | OAuth2 Google registration endpoint | Requires OAuth2 login |
| `/user`            | GET         | User profile page                   | Authenticated users   |
| `/edit-profile`    | GET         | User profile edit form              | Authenticated users   |
| `/update-profile`  | POST        | Submit profile updates              | Authenticated users   |
| `/logout`          | POST/GET*   | Log out the user                    | Authenticated users   |

_\* Spring Security supports GET logout with AntPathRequestMatcher_

---

## Ports and Access

- Default HTTP Port: **8083**
- Base URL: `http://localhost:8083`

Example full URLs:

- Login page: `http://localhost:8083/login`
- Registration page: `http://localhost:8083/register`
- User profile page: `http://localhost:8083/user`
- Edit profile page: `http://localhost:8083/edit-profile`

---
