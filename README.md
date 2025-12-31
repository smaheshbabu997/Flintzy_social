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


## ⚙️ Setup & Configuration

### 1. Set Environment Variables
Before running the application, ensure the following environment variables are configured in your system or IDE:

| Category | Variable Name | Description |
| :--- | :--- | :--- |
| **Database** | `DB_USERNAME` | Your database username |
| | `DB_PASSWORD` | Your database password |
| **JWT** | `JWT_SECRET` | Secret key for signing tokens |
| **Google** | `GOOGLE_CLIENT_ID` | Client ID from Google Cloud Console |
| | `GOOGLE_CLIENT_SECRET` | Client Secret from Google Cloud Console |
| **Facebook** | `FB_APP_ID` | App ID from Meta for Developers |
| | `FB_APP_SECRET` | App Secret from Meta for Developers |
| | `FB_REDIRECT_URI` | `http://localhost:8080/api/facebook/callback` |

---

### 2. Install Dependencies
Open your terminal in the project root and run:
``bash
mvn clean install

### 3. Run Application

``bash
mvn spring-boot:run
App starts at: http://localhost:8080

### 4. Login Flow (Google)

Visit: http://localhost:8080/oauth2/authorization/google in your browser.

On success, you’ll receive a JSON response containing your JWT.

### 5. Use JWT in API Calls
Add the header below to all secured requests:

Code
Authorization: Bearer <JWT>

## 📍 API Endpoints

### 🔐 Authentication

#### **1. Google Login Initiation**
* **Endpoint:** `GET /api/auth/login/google`
* **Description:** Returns the relative path to initiate the **Google OAuth2** flow.
* **Auth:** **None**
* **Response:** `{ "loginUrl": "/oauth2/authorization/google" }`

#### **2. OAuth2 Callback**
* **Description:** Handled automatically by **Spring Security**.
* **Response:** On a successful login, the server returns `{ "token": "<JWT>" }`.

---

### 📱 Facebook Integration

#### **1. Connect Facebook Account**
* **Endpoint:** `GET /api/facebook/connect`
* **Description:** Returns the **Facebook OAuth URL** with all required permission scopes.
* **Auth:** **JWT Required**
* **Response:** `{ "authUrl": "https://www.facebook.com/..." }`

#### **2. Facebook Callback**
* **Endpoint:** `GET /api/facebook/callback?code=<CODE>`
* **Description:** Exchanges the auth code for an **Access Token**, fetches user pages, and persists them to the database.
* **Auth:** **JWT Recommended** (Ties linked pages to your specific user account).
* **Response:** `{ "linkedPages": <count> }`

#### **3. List Linked Pages**
* **Endpoint:** `GET /api/facebook/pages`
* **Description:** Returns a list of all **Facebook Pages** currently linked to the authenticated user.
* **Auth:** **JWT Required**
* **Response:** `[ { "pageId": "123", "name": "My Page" }, ... ]`

#### **4. Publish Post**
* **Endpoint:** `POST /api/facebook/pages/{pageId}/publish`
* **Description:** Publishes a text-based post directly to the specified **Facebook Page**.
* **Auth:** **JWT Required**
* **Request Body:** ```json
    { "message": "Hello from Flintzy backend!" }
* **Response:** `{ "postId": "<fb-post-id>" }`

## ✅ Quick Start Checklist

Follow these steps to get the application up and running quickly:

1.  **Create Database:**
    Run the following command in your SQL terminal:
    `CREATE DATABASE flintzy_social;`

2.  **Set Environment Variables:**
    Ensure you have configured the **DB**, **JWT**, **Google**, and **Facebook** credentials in your environment.

3.  **Configure Facebook App:**
    In the [Meta for Developers](https://developers.facebook.com/) dashboard, ensure the following are set:
    * **Roles:** Add your account as an Admin/Developer.
    * **Site URL:** Set to `http://localhost:8080`.
    * **Valid OAuth Redirect URIs:** Add `http://localhost:8080/api/facebook/callback`.

4.  **Run the Application:**
    Execute the command:
    ```bash
    mvn spring-boot:run
    ```

5.  **User Authentication (Login):**
    * Visit: **`http://localhost:8080/oauth2/authorization/google`** in your browser.
    * On success, you will receive a **JWT** in the JSON response.

6.  **Connect Facebook Account:**
    * Navigate to: **`/api/facebook/connect`** and authorize the app.
    * The flow will complete via the **`/api/facebook/callback`** endpoint.

7.  **Publish to Facebook Page:**
    * Send a `POST` request to: **`/api/facebook/pages/{pageId}/publish`**
    * Include your **message** in the request body to post to your page.
