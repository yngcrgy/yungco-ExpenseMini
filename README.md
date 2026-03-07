# ExpenseMini (IT342 Final Implementation)

## Application Details
- **Application Name:** ExpenseMini
- **Domain:** Expense & Finance Tracker
- **Course Section:** TBA
- **Author:** TBA

## Repository Structure
This repository contains the final implementation for IT342, following the strict required folder structure:
- `backend/`: Custom-built Spring Boot (Java 17+) REST API mapping all core endpoints.
- `web/`: ReactJS Web Application.
- `mobile/`: Android Kotlin (API 34) Mobile Application using XML layouts.
- `docs/`: System documentation & Architecture Diagrams.

## Core Features & Integrations
This system fulfills the MANDATORY feature requirements for IT342:
1. **Authentication & Security:** JWT-based login, registration, `/me` endpoint, BCrypt password hashing.
2. **Role-Based Access Control (RBAC):** `ADMIN` and `USER` roles restricting API and UI access.
3. **Core Business Module:** Full CRUD operations and validations for managing **Expenses**.
4. **Google OAuth Login:** Alternative sign-in bridging Google's profile data to the system's JWT filter.
5. **External API Integration:** Currency exchange API implementation for expense value conversions.
6. **File Uploads:** Uploading and retrieving expense receipt images/documents.
7. **Email Sending (SMTP):** Welcome emails upon registration and system notification emails on key triggers.
8. **Real-time Feature:** WebSocket integration for live expense summary updates across active sessions.
9. **Payment Integration (Sandbox):** Stripe/PayPal integration in Test Mode for a premium subscription feature.
