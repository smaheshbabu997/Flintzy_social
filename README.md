# Flintzy Social — Spring Boot backend for social media management

A production‑minded backend that lets users authenticate via Google, securely link Facebook Pages, and publish posts to those pages via the Facebook Graph API. All APIs are protected with JWT. Designed to be extended to Instagram and YouTube.

---

## ✨ Features

- **Authentication:** Google OAuth2 login; backend issues **JWT** for API access.
- **Facebook connect:** Link Facebook Pages using the **Graph API** and store Page Access Tokens securely.
- **Post publishing:** Publish text posts to Facebook Pages on behalf of the user.
- **Security:** Stateless JWT‑based authorization on all endpoints (except OAuth redirects/callbacks).
- **Database:** MySQL with JPA; migrations via Flyway.
- **Extensible:** Architecture ready to add Instagram and YouTube.

---

## 🛠 Tech stack

- **Backend:** Spring Boot 3, Java 17
- **Security:** Spring Security, OAuth2 Client (Google), JJWT
- **Data:** Spring Data JPA, MySQL 8
- **Migrations:** Flyway
- **HTTP client:** RestTemplate
- **Build:** Maven

# Prerequisites
Java: 17+

Maven: 3.9+

MySQL: 8+

Google Cloud OAuth client: Web application credentials

Meta for Developers: Facebook App with OAuth configured

# Set environment variables in your shell or IDE run configuration:

DB: DB_USERNAME, DB_PASSWORD

JWT: JWT_SECRET

Google: GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET

Facebook: FB_APP_ID, FB_APP_SECRET, FB_REDIRECT_URI

# Security design
OAuth2 login: Spring Security handles Google sign-in.

JWT issuance: On successful OAuth login, the backend returns a JWT (Bearer token).

Stateless API: All protected endpoints require Authorization: Bearer <JWT>.

Key components:

SecurityConfig: Configures stateless security, OAuth2 login, and the JWT filter.

OAuth2SuccessHandler: Creates or updates the user and returns a JWT after Google login.

JwtAuthFilter: Validates JWT from the Authorization header and sets the SecurityContext.

# Facebook integration
Scopes in development: email, public_profile, pages_show_list, pages_manage_posts.

Flow:

Authorize: Redirect user to Facebook OAuth dialog with required scopes.

Token exchange: Exchange code for a User Access Token.

Pages: Call /me/accounts to get managed pages and Page Access Tokens.

Publish: Use POST /{page-id}/feed with message and access_token.

Important:

Redirect URIs: Must be whitelisted under “Valid OAuth Redirect URIs” in the Facebook App dashboard.

HTTPS requirement: For non-local domains, the redirect must use https://. Local http://localhost:8080/... is allowed in development.

# Facebook app dashboard configuration
Add these in the Meta for Developers dashboard:

Roles:

Administrator/Developer for your account to test restricted scopes.

Settings → Basic:

App domains: Leave blank for localhost; set for production domain later.

Site URL: http://localhost:8080/

Settings → Advanced:

Valid OAuth Redirect URIs:

http://localhost:8080/api/facebook/callback

Optional: http://127.0.0.1:8080/api/facebook/callback

Development mode: Keep the app in Development; only admins/devs/testers can test restricted scopes.

If you see “Invalid Scopes,” ensure you’re logged in as a user with a role on the app.

For staging/production:

Use https for redirect URIs (e.g., https://api.yourdomain.com/api/facebook/callback).

Consider using ngrok during development to test HTTPS redirects:

Run ngrok http 8080

Set facebook.redirect-uri to https://<your-ngrok-subdomain>.ngrok.io/api/facebook/callback

Add that HTTPS URL to Valid OAuth Redirect URIs.

# How to run locally
Create database

Command: CREATE DATABASE flintzy_social;

Set environment variables

DB: DB_USERNAME, DB_PASSWORD

JWT: JWT_SECRET

Google: GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET

Facebook: FB_APP_ID, FB_APP_SECRET, FB_REDIRECT_URI

Install dependencies

Command: mvn clean install

Run application

Command: mvn spring-boot:run

App starts at http://localhost:8080

Login flow (Google)

Visit http://localhost:8080/oauth2/authorization/google in a browser.

On success, you’ll receive a JSON response containing your JWT.

Use JWT

Add header Authorization: Bearer <JWT> to all API calls.

# API endpoints
Authentication
GET /api/auth/login/google

Description: Returns the relative path to initiate Google OAuth.

Auth: None.

Response: { "loginUrl": "/oauth2/authorization/google" }

OAuth2 callback (handled by Spring Security)

On success, the server responds with { "token": "<JWT>" }.

Facebook connect
GET /api/facebook/connect

Description: Returns the Facebook OAuth URL with required scopes.

Auth: JWT required.

Response: { "authUrl": "https://www.facebook.com/...&scope=pages_show_list,pages_manage_posts,email,public_profile" }

GET /api/facebook/callback?code=<CODE>

Description: Exchanges code for token, fetches pages, persists account and pages.

Auth: JWT recommended (tie the linked pages to the authenticated user).

Response: { "linkedPages": <count> }

GET /api/facebook/pages

Description: Lists linked pages for the authenticated user.

Auth: JWT required.

Response: [ { "pageId": "123", "name": "My Page" }, ... ]

POST /api/facebook/pages/{pageId}/publish

Description: Publishes a text post to the specified linked page.

Auth: JWT required.

Body: { "message": "Hello from Flintzy backend!" }

Response: { "postId": "<fb-post-id>" }

# 1) Get Google login URL (browser flow)
curl -X GET http://localhost:8080/api/auth/login/google

# 2) Start Facebook connect (returns authUrl)
curl -H "Authorization: Bearer <JWT>" \
  http://localhost:8080/api/facebook/connect

# 3) Facebook redirects here after approval (simulate call)
curl -H "Authorization: Bearer <JWT>" \
  "http://localhost:8080/api/facebook/callback?code=<CODE_FROM_FACEBOOK>"

# 4) List linked Facebook pages
curl -H "Authorization: Bearer <JWT>" \
  http://localhost:8080/api/facebook/pages

# 5) Publish a post to a linked page
curl -X POST -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"message":"Hello from Flintzy backend!"}' \
  http://localhost:8080/api/facebook/pages/<PAGE_ID>/publish
  
# Quick start checklist
Create DB: CREATE DATABASE flintzy_social;

Set env vars: DB, JWT, Google, Facebook.

Configure Facebook App: Roles, Site URL, Valid OAuth Redirect URIs.

Run: mvn spring-boot:run

Login: http://localhost:8080/oauth2/authorization/google → get JWT.

Connect FB: /api/facebook/connect → authorize → /api/facebook/callback.

Publish: /api/facebook/pages/{pageId}/publish with message.
