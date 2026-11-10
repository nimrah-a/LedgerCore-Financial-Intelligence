# LedgerCore — Financial Transaction Intelligence

**Java 21 / Spring Boot / REST API / H2 SQL / HTML / CSS / JavaScript / JUnit 5**

LedgerCore is an interactive, local-first financial dashboard with a fully functional Java backend. Its simulated data is persisted in a local SQL database, and the browser dashboard reads and updates that data through REST endpoints. Inspired by modern fintech dashboards, it emphasizes thoughtful UX while demonstrating real backend engineering.

> **Educational demo only.** No real bank integrations, live assets, financial advice, or payment rails. The starter account balances and initial activity are simulated. This is not production banking software.

## Features

- **Financial overview:** four live KPI cards, an interactive seven-day income/expense chart, a spending-category donut, recent transactions, and budget progress.
- **Account management:** three fictional accounts with balances persisted in the H2 database.
- **Real transfers:** atomic debit/credit across two demo accounts, with overdraft and same-account rejection. Each transfer creates a linked pair of ledger entries.
- **Transaction entry:** add expenses and income, instantly update balances, and review transaction history.
- **Transaction explorer:** filter by category and search transactions by merchant, category, type, or account.
- **Budget planner:** edit category limits and track spending against budgets for the current month.
- **Analytics:** category trends and daily cash-flow visualization backed by actual persisted entries.
- **Security-conscious defaults:** local-only web server and validation. No real financial or user data.

## Interface

The responsive dashboard uses a near-black charcoal theme with subtle violet accents, teal income indicators, sparklines, interactive charts, a collapsible mobile layout, and dark-mode action dialogs.

_Add actual screenshots after testing: `screenshots/overview.png`, `screenshots/transactions.png`, `screenshots/budgets.png`._

## Open in IntelliJ IDEA (recommended)

1. Extract `LedgerCore_SpringBoot_Java21.zip` and open the **LedgerCore** folder in IntelliJ IDEA.
2. Let IntelliJ import the Maven `pom.xml` and download dependencies (internet needed the first time).
3. Select **JDK 21 or newer**. You can use Java 23 locally; the compiler target is Java 21.
4. Open `src/main/java/com/ledgercore/LedgerCoreApplication.java` and click the green **Run** button beside `main()`.
5. Wait for the server to start, then open **http://localhost:8080** in Safari or Chrome.
6. Navigate between Overview, Transactions, Accounts, Budgets, and Analytics. Try a transfer, new expense, and editing a budget. Refresh the page to verify persistence.

**The UI is a web dashboard served by Spring Boot**, not a JavaFX window. This is intentional: it demonstrates full-stack Java and REST APIs alongside the desktop JavaFX skills showcased in GraphLab.

### Alternative: Maven terminal

```bash
mvn spring-boot:run
```

If macOS Terminal says `mvn: command not found`, launch from IntelliJ instead. IntelliJ's Maven support may work even when Maven isn't installed in the terminal PATH.

### Stop / reset

Stop the running server in IntelliJ before starting another instance. The database lives in the ignored `data/` folder and persists between restarts. To restore original demo data, stop the app, delete the local `data/` folder, and run it again. **This permanently discards your local demo changes.**

## REST API

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/overview` | Totals, spending, budget usage, seven-day activity |
| GET | `/api/accounts` | Account balances |
| GET | `/api/transactions` | All entries, newest first |
| GET | `/api/budgets` | Budget limits |
| POST | `/api/transfer` | Atomic simulated transfer |
| POST | `/api/expense` | Debit account and record an expense |
| POST | `/api/income` | Credit account and record income |
| PUT | `/api/budgets` | Change a category's monthly limit |

Example transfer request:

```json
{"fromAccountId":1,"toAccountId":2,"amount":125.50}
```

Example expense request:

```json
{"accountId":1,"merchant":"Coffee Shop","category":"Food & Drink","amount":8.75}
```

Amounts are processed as Java `BigDecimal` to avoid floating-point balance arithmetic. The backend rejects invalid or over-budget transfers using transaction-scoped mutations.

## Architecture

```text
Browser dashboard (HTML + CSS + vanilla JS)
    | fetch JSON
    v
Spring Boot REST Controller
    v
LedgerService (@Transactional business rules)
    v
Spring Data JPA Repositories
    v
H2 persistent SQL database (local file)
```

## Important design decisions

- **Transfer consistency:** funds are removed from one account and added to another within one database transaction; ledger entries share a reference ID.
- **Time-aware budgets:** budget usage is derived only from actual expense entries in the current calendar month; transfers do not count as spending.
- **Demo starter balances:** seeded balances are fictional opening balances, not recomputed from the example transaction log. Subsequent transfers and entries update balances consistently.
- **Local-first privacy:** the sample server binds only to `127.0.0.1` and is not exposed publicly by default.
- **Not a banking ledger:** a production financial system would also require authentication, authorization, reconciliation, stronger database constraints, concurrency controls, audit trails, anti-fraud controls, and regulatory review.

## Tests

Run JUnit tests from IntelliJ or with:

```bash
mvn test
```

Integration tests cover balanced transfers, same-account and insufficient-funds rejection, income/expense balance changes, and budget updates. GitHub Actions runs the Maven tests on pushes and pull requests.

## File layout

```text
src/main/java/com/ledgercore/
  LedgerCoreApplication.java
  api/             REST controller and error handling
  config/          fictional startup dataset
  dto/             validated request models
  model/           JPA entities
  repo/            Spring Data repositories
  service/         transactional financial rules
src/main/resources/
  application.properties
  static/index.html
  static/styles.css
  static/app.js
src/test/java/com/ledgercore/
  LedgerServiceIntegrationTest.java
```

## Portfolio talking points

1. Designed a Java Spring Boot application integrating REST APIs with a persistent SQL database.
2. Implemented transactional balance updates and transfer validation using `BigDecimal` and Spring `@Transactional`.
3. Built an interactive analytics dashboard driven by server-side data rather than hardcoded UI numbers.
4. Added integration tests, input validation, and GitHub Actions CI.

**License:** MIT (see `LICENSE`).
