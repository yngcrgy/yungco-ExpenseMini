# ExpenseMini Mobile: Phase 2 Summary

## How Registration Works
The mobile registration flow begins at the `RegisterActivity`, presenting users with inputs for First Name, Last Name, Email, Password, and Password Confirmation. When the user taps the “Create Account” button, the UI applies front-end validation to ensure all fields are populated, the password exceeds 6 characters, and the password matches its confirmation field.

If validation passes, a Kotlin Coroutine is launched on the `Dispatchers.IO` thread to prevent freezing the main UI. The system packages the inputs into a Kotlin `RegisterRequest` data class and executes an asynchronous POST request using Retrofit off the main thread. Upon receiving a `200 OK` response from the backend API, the application displays a success `Toast` notification and seamlessly directs the user back to the `LoginActivity`. 

## How Login Works
The login process occurs in `LoginActivity`, requiring only an Email and Password. Upon tapping “Login”, identical UI safeguards prevent network spam by validating non-empty fields and temporarily disabling the submission button. 

The inputs are mapped to a `LoginRequest` object and passed to Retrofit via Coroutines. The Spring Boot backend intercepts this request using its Spring Security filters; if the credentials match the BCrypt-hashed password in Supabase, the backend responds with the user's details and an encrypted JWT token (`AuthResponse`). The Android client parses this response, issues a personalized "Welcome back!" toast displaying the user's first name, and successfully flags the user as authenticated in the client tier.

## API Integration Used
The native Android client connects directly to the Phase 1 Spring Boot backend using the **Retrofit 2** HTTP library along with **Gson** for automatic JSON serialization/deserialization. To test on the local Android Emulator, the `ApiClient` utilizes the specialized `http://10.0.2.2:8080/` base URL which bridges the emulator’s virtual network loopback to the developer's physical machine hosting the local Spring Boot instance.

The integration explicitly targets two primary REST endpoints defined in the `AuthApiService` interface:
1. `POST /api/auth/register`
   - **Payload:** `RegisterRequest` (JSON mapping `firstName`, `lastName`, `email`, `password`)
   - **Response Structure:** Returns a confirmation or an HTTP 400 Bad Request if the email is in use.
2. `POST /api/auth/login`
   - **Payload:** `LoginRequest` (JSON mapping `email`, `password`)
   - **Response Structure:** Evaluates to an `AuthResponse` object mapped internally via Gson, returning the JWT authorization token and user meta-information.
