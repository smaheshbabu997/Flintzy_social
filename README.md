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

## 📋 Prerequisites

- Java 17+
- Maven 3.9+
- MySQL 8+
- Google Cloud OAuth client (Web application credentials)
- Meta for Developers Facebook App with OAuth configured

# Flintzy Social Backend

Spring Boot + MySQL backend for managing social media accounts. Supports Google OAuth2 login, JWT‑secured APIs, and Facebook Page integration via Graph API.

---

## ⚙️ Environment Variables

Set these in your shell or IDE:

- **DB:** `DB_USERNAME`, `DB_PASSWORD`
- **JWT:** `JWT_SECRET`
- **Google:** `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`
- **Facebook:** `FB_APP_ID`, `FB_APP_SECRET`, `FB_REDIRECT_URI`

---

## 🔐 Security

- **OAuth2 login:** Google sign‑in via Spring Security  
- **JWT issuance:** Backend returns JWT after login  
- **Stateless API:** All endpoints require `Authorization: Bearer <JWT>`

Key components: `SecurityConfig`, `OAuth2SuccessHandler`, `JwtAuthFilter`

---

## 📡 Facebook Integration

- **Scopes (dev):** `email`, `public_profile`, `pages_show_list`, `pages_manage_posts`
- **Flow:** Authorize → Token exchange → Fetch `/me/accounts` → Publish via `/{page-id}/feed`
- **Redirect URIs:** Must be whitelisted in Facebook App dashboard  
- **HTTPS:** Required for non‑local domains (use `ngrok` for local HTTPS testing)

---

## 🛠 Facebook App Setup

- **Roles:** Add yourself as Administrator/Developer  
- **Settings → Basic:**  
  - Site URL: `http://localhost:8080/`  
- **Settings → Advanced:**  
  - Valid OAuth Redirect URIs:  
    - `http://localhost:8080/api/facebook/callback`  
    - (Optional) `http://127.0.0.1:8080/api/facebook/callback`  
- Keep app in **Development Mode** for testing restricted scopes

---

## ▶️ Run Locally

1. Create DB:  
   ```sql
   CREATE DATABASE flintzy_social;


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
