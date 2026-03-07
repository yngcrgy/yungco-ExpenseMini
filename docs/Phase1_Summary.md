# IT342 Phase 1: User Registration and Login Summary

## User Registration
**Registration fields used:**
- First Name (`firstName`)
- Last Name (`lastName`)
- Email (`email`)
- Password (`password`)

**Validation process:**
The system uses Spring Boot Validation to ensure all fields are properly formatted. The `AuthService` checks the incoming `RegisterRequest` DTO. If any fields are empty, or if constraints violate the basic requirements, the request is rejected with a `400 Bad Request` before creating the record.

**How duplicate accounts are prevented:**
Within the `UserRepository`, the method `existsByEmail(email)` is called during registration. If the database already holds a record with the provided email, the system throws an exception ("Email already in use") preventing the account creation and responding with an error. The `email` column in the database is also marked as `UNIQUE`.

**How passwords are stored securely:**
Passwords are never stored in plain-text. Before saving the `User` entity to the database, the raw password string is encrypted using `BCryptPasswordEncoder`. This applies a one-way hashing algorithm with a salt, making the stored string undecipherable.

---

## User Login
**Login credentials used:**
- Email
- Password

**How the system verifies users:**
When a login request is received, the `AuthenticationManager` intercepts it and passes the credentials to the `DaoAuthenticationProvider`. This provider loads the User from the database using the provided email. The `BCryptPasswordEncoder` then verifies if the raw password provided matches the hashed password stored in the database. 

**What happens after successful login:**
If the credentials are valid, the `JwtService` generates a secure JSON Web Token (JWT) signed with an HMAC key. The system returns this token (along with user details) to the client. The client stores this JWT and the user is redirected to the protected System Dashboard. All subsequent requests to protected API endpoints will use the `Authorization: Bearer <TOKEN>` header.

---

## Database Table
The system uses a table named `users` to store all registered accounts.

**Table:** `users`

**Columns:**
- `id` (BIGINT, Primary Key, Auto-Increment)
- `first_name` (VARCHAR)
- `last_name` (VARCHAR)
- `email` (VARCHAR, Unique, Not Null)
- `password` (VARCHAR, Encrypted BCrypt hash)
- `role` (VARCHAR, e.g., 'ADMIN' or 'USER')
- `provider` (VARCHAR, e.g., 'LOCAL' or 'GOOGLE')

---

## API Endpoints
**Registration:**
`POST /api/auth/register` (Accepts JSON body: firstName, lastName, email, password)

**Login:**
`POST /api/auth/login` (Accepts JSON body: email, password)

**Profile / Current User (Protected):**
`GET /api/auth/me` (Requires JWT Bearer Token, returns authenticated user details)
