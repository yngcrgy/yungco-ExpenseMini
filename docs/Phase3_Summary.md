# IT342 Phase 3 – Web Main Feature Completed

## 4. Short Summary

### Description of the main feature
The main feature of the web application is the **Analytics Dashboard and Expense Management System**. This feature empowers users to track their everyday transactions, monitor spending limits against a configured Monthly Budget, and review interactive categorical visualizations. It bridges the gap between raw data and personal budgeting intelligence by utilizing responsive charts and categorized history logs.

### Inputs and validations used
- **Expense Validation:** Before capturing an expense, the system strictly validates that `title` is not empty, `amount` is greater than 0, and a specific `category` is formally selected via the dropdown.
- **Budget Interception Layer:** Implemented a core verification pipeline that explicitly blocks the user from adding new expenses if they lack an allocated monthly budget. If their budget evaluates to `0`, they immediately trigger a failed criteria popup and are redirected back to the `/profile` path to formulate a limit.
- **Database Constraints:** Employed strict JPA Spring Boot `@Column` bindings to seamlessly bypass old legacy PostgreSQL constraints without dropping tables, ensuring strict structural typing matches the Software Design Document.

### How the feature works
1. **Security Handshake:** Through automatic Axios JWT interceptors, the React frontend instantly authenticates session requests, securely retrieving a user’s internal ledger from the Spring Boot API.
2. **Dashboard Visuals:** When initialized, the Dashboard automatically generates a pie-chart categorical distribution and a proportional "Spent vs Budget" CSS tracker using reactive data parsed securely out of the backend aggregations.
3. **Expense Lifecycles:** When adding an expense, the React payload hits the backend API, intercepts into the Java `ExpenseService`, and pushes the precise transaction into the `expenses` Supabase table using `category_id` relational lookups. If validation checks out, it refreshes the dashboard UI state dynamically.

### API endpoints used
- `GET /api/dashboard/summary`: Retrieves aggregated user expense statistics, including `monthly_budget`, `total_expenses`, `top_category`, and `avg_daily_spending`.
- `POST /api/expenses`: Handles secure object payloads for spawning new expense entries into the SQL database.
- `GET /api/expenses`: Pulls the entirety of a user's chronological expense history for the History route.
- `PUT /api/expenses/{id}` & `DELETE /api/expenses/{id}`: Edit and Delete routes executing modifications.
- `POST /api/budgets`: Upserts the user's active Monthly Budget allocation globally.

### Database table/s involved
- `users`: Core identity table authenticating the caller's JWT scope constraint logic over the subsequent tables.
- `expenses`: The core tabular location where `amount`, `title`, `expense_date`, `notes`, `category_id`, and `is_recurring` variables are durably recorded in relationship with `user_id`.
- `budgets`: Extraneous table that safely houses the current parameter bounds (`budget_limit`) enforcing expense rules over a specific `month`.
- `categories`: The foreign-key data registry pre-seeded with values (e.g., `Food=1`, `Transport=2`) referenced mathematically by the expense lists.

---

## 3. Screenshots Checklist

*(Please replace the placeholders below with your own actual screenshots)*

### Main feature page/screen
> **[Insert Screenshot Here: The main Dashboard view showing the Charts, Budget Progress Bar, and Recent Transactions]**

### Adding or using the main feature
> **[Insert Screenshot Here: The /add-expense page filled out with data, OR one of the Dashboard Quick Add buttons being clicked]**

### Successful output/result
> **[Insert Screenshot Here: The Dashboard or History Table successfully updating to show the newly added ₱ expense correctly rendered]**

### Database record related to the feature
> **[Insert Screenshot Here: Your open Supabase "expenses" Table Editor vividly showing the identical inserted record sitting successfully into your PostgreSQL database]**
