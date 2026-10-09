# JavaBank

A console-based banking application written in pure Java. Simulates a
personal bank account with multi-currency deposits, current accounts, and
a credit module — all driven through an interactive text menu.

Built as a learning project to practice object-oriented design, working with
`BigDecimal` for money, date/time handling, and application state management
without a database.

## Features

### Deposits

- **Two deposits per account**, each in its own currency and with its own
  interest rate and term.
- **Deposit 1** supports both top-up and withdrawal.
- **Deposit 2** supports top-up only (as designed).
- **Transfers between deposits** with automatic currency conversion.
- **Monthly interest simulation** — accrues interest month by month over a
  chosen number of months.
- **Early closure** — closes a deposit ahead of schedule and voids all
  accrued interest.

### Current Accounts

- One current account per currency: **KZT, USD, EUR**.
- Open new accounts, top up, withdraw.
- **Transfers between current accounts** with currency conversion.
- **Transfers between deposits and current accounts** in both directions.

### Credits

- Issue a credit up to **1,000,000 KZT** at a **30% annual rate**.
- **Annuity payment** calculated with the standard formula
  `P = S · r · (1+r)^n / ((1+r)^n − 1)`.
- **Repayment simulation** — runs month-by-month and automatically
  withdraws funds from the credit account, then Deposit 1, then Deposit 2
  (converting currencies when needed).
- **Manual repayment** with excess credited to the KZT current account
  (or Deposit 1 if no such account is open).
- **Default handling** — if funds run out during simulation, the bank
  writes off all available balances and blocks the account. The status
  becomes `OVERDUE`, and the account stays blocked until the debt is repaid.

### Other

- **Transaction history** — every operation is recorded with a unique ID,
  amount, description, and timestamp.
- **Fixed exchange rates** — 1 USD = 500 KZT, 1 EUR = 550 KZT. All
  cross-currency conversions go through KZT.
- **Input validation** — the menu tolerates garbage input and re-prompts
  instead of crashing.

## Tech Stack

- **Java** — object-oriented design with `enum`, `BigDecimal`,
  `java.time` API.
- **Maven** — project build and dependency management.
- **No external dependencies** — the entire project runs on the standard
  JDK.

## Project Structure
JavaBank/
├── pom.xml
├── README.md
├── LICENSE
└── src/
└── main/
└── java/
└── com/
└── bank/
├── Main.java # Entry point + console menu
├── model/
│ ├── Account.java # Aggregates deposits, credit, current accounts
│ ├── Credit.java # Credit with annuity payment
│ ├── Currency.java # Enum: KZT, USD, EUR
│ ├── CurrentAccount.java # Current account in a single currency
│ ├── Transaction.java # Single transaction record
│ └── User.java # Bank customer
├── repository/
│ └── InMemoryDatabase.java # Static lists acting as "tables"
├── service/
│ └── BankService.java # All banking operations
└── util/
└── ExchangeRates.java # Fixed rates + conversion

## Getting Started

### Requirements

- **JDK 27** — the version specified in `pom.xml`
  (`maven.compiler.source` / `maven.compiler.target`).
- **Maven** for building.

### Build

git clone https://github.com/Arost235/JavaBank.git
cd JavaBank
mvn clean package

## Run

java -jar target/JavaBank-1.0-SNAPSHOT.jar

Or run the com.bank.Main class directly from your IDE.

## Usage

On start, the application seeds a demo user (Alexey Smirnov) with:

Deposit 1: KZT, 17.5% annual, 6 months

Deposit 2: USD, 21.0% annual, 12 months

Three current accounts: KZT, USD, EUR

From the main menu you can navigate to Deposits, Credits, Current Accounts,
view Account Info, or browse Transaction History. Every operation is
performed through the interactive console menu — no arguments required.

## Notes

All data is kept in memory and is lost when the program exits.
There is no persistence layer.

Exchange rates are hard-coded and do not reflect real market values.

Credits are issued in KZT only.

## Author

Arost235 — github.com/Arost235

## License

This project is licensed under the MIT License — see the LICENSE
file for details.