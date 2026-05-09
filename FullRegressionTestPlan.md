# Full Regression Test Plan - ExpenseMini

## 1. Introduction
This document outlines the test plan for the Full Regression Test of the ExpenseMini multi-platform application following its refactoring to Vertical Slice Architecture. The goal is to ensure that all previously implemented functional requirements across the Backend, Web Frontend, and Mobile Application remain intact and functional.

## 2. Scope of Testing
The regression test covers the following core modules:
- **Authentication & User Management** (Registration, Login, Profile)
- **Expense Management** (Add Expense, History, Filtering)
- **Dashboard & Analytics** (Summary, Recent Transactions)
- **Budgeting** (Set Budget, Budget Status)

## 3. Functional Requirements Coverage
| Req ID | Requirement Description | Module | Priority |
|---|---|---|---|
| FR-01 | User can register with email and password | Auth | High |
| FR-02 | User can login and receive JWT token | Auth | High |
| FR-03 | User can view their profile details | Profile | Medium |
| FR-04 | User can add a new expense | Expense | High |
| FR-05 | User can view expense history | Expense | High |
| FR-06 | User can view summary dashboard | Dashboard | High |

## 4. Test Cases & Test Scripts

### TC-01: User Registration
- **Precondition**: User is not logged in.
- **Steps**:
  1. Navigate to Registration page.
  2. Enter valid Name, Email, Password.
  3. Click "Register".
- **Expected Result**: User is successfully registered and redirected to Login.

### TC-02: User Login
- **Precondition**: User has a registered account.
- **Steps**:
  1. Navigate to Login page.
  2. Enter valid Email and Password.
  3. Click "Login".
- **Expected Result**: JWT token is generated. User is redirected to Dashboard.

### TC-03: Add New Expense
- **Precondition**: User is logged in.
- **Steps**:
  1. Navigate to "Add Expense" page.
  2. Enter amount, select category, enter description.
  3. Click "Save Expense".
- **Expected Result**: Expense is saved. Success message displayed. Expense appears in history.

### TC-04: View Dashboard Summary
- **Precondition**: User is logged in and has existing expenses.
- **Steps**:
  1. Navigate to Dashboard.
- **Expected Result**: Total expenses, recent transactions, and category breakdown are displayed accurately.

## 5. Automated Testing Strategy
- **Backend**: Spring Boot Unit Tests (`@SpringBootTest`, `MockMvc`) to validate controllers and services after vertical slice refactoring.
- **Frontend (Web)**: Vite/React build tests and potentially Jest/React Testing Library to ensure components render correctly.
- **Execution Command**: 
  - Backend: `./mvnw clean test`
  - Web: `npm run build`
