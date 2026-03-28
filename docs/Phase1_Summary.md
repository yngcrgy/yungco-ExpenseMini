# ExpenseMini: Phase 1

### User Registration
- **Registration Fields Used:** The system collects `firstName`, `lastName`, `email`, `password`, and a `confirmPassword` field to ensure accuracy during sign-up. 
- **Validation Process:** On the frontend (React), the form verifies that the password array matches the `confirmPassword` field before submitting. The backend (Spring Boot) validates that the fields correspond to the correct data types and that the password is a minimum of 6 characters string.
- **How Duplicate Accounts Are Prevented:** The `users` database table enforces a `UNIQUE` constraint on the `email` column. If a user attempts to register with an email that already exists, the Spring Boot backend catches the `DataIntegrityViolationException` and returns a `400 Bad Request` citing "Registration failed. Email might already be in use."
- **How Passwords Are Stored Securely:** Passwords are never stored in plaintext. The Spring Boot backend uses `BCryptPasswordEncoder` (supplied by Spring Security) to salt and hash the password string before it is ever written to the database.

### User Login
- **Login Credentials Used:** Users authenticate using their registered `email` and `password`.
- **How the System Verifies Users:** The Spring Boot backend leverages an injected `AuthenticationManager` that compares the raw input password against the BCrypt-hashed password stored in the Supabase database. 
- **What Happens After Successful Login:** Upon successful verification, the backend generates a signed JSON Web Token (JWT) containing the user's encoded identity. The frontend React application receives this JWT, stores it in `localStorage`, and instantly redirects the authenticated user to their secure Dashboard.

### Database Table
The PostgreSQL database (hosted on Supabase) stores user entities in the `users` table.

**Table:** `users`
**Columns:** 
- `id` (Primary Key, Auto-incremented)
- `first_name` (VARCHAR)
- `last_name` (VARCHAR)
- `email` (VARCHAR, UNIQUE)
- `password` (VARCHAR, BCrypt Hashed)
- `role` (VARCHAR, Defaults to USER)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### API Endpoints
The Phase 1 backend exposes the following REST API endpoints:
- `POST /api/auth/register` - Accepts a `RegisterRequest` JSON payload and writes a new user to the database.
- `POST /api/auth/login` - Accepts an `AuthRequest` JSON payload and returns a `AuthResponse` containing the JWT token.
