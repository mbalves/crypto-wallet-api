# Crypto Wallet API (SwissPost Code Challenge)

A cryptocurrency wallet management system that allows users to create wallets, add assets, and simulate profits.

## Features

- Create and manage cryptocurrency wallets
- Add and remove crypto assets
- Real-time price updates from CoinCap API
- Wallet profit simulation
- Historical price tracking
- RESTful API interface

## Prerequisites

- Java 21
- PostgreSQL 15+
- Gradle 8+

## Setup

1. Clone the repository:

```bash
git clone https://github.com/mbalves/crypto-wallet-api.git
cd crypto-wallet-api
```

2. Run the database via Docker:
   - A `docker-compose.yml` file is provided to run a local Postgres database configured to match the settings in `application.yml`.

```sh
   docker-compose up -d
```

- The database will be available at `localhost:5432` with credentials as defined in `application.yml`.

3. Configure Environment Variables:
   - Before running the application, create a `.env` file in the project root with the following variables:

```dotenv
CRYPTO_DB_URL=jdbc:postgresql://localhost:5432
CRYPTO_DB_NAME=crypto_wallet
CRYPTO_DB_USER=crypto
CRYPTO_DB_PASSWORD=crypto
CRYPTO_PRICING_API_KEY=your_pricing_api_key_here
```

- Adjust the values as needed for your local setup.
- For the database variables, ensure they match the settings in `docker-compose.yml`.
- Load the environment variables in your shell session:
  ```sh
  export $(cat .env | xargs)
  ```

4. Run the application:

```bash
./gradlew bootRun
```

## API Documentation

The API documentation is available at `http://localhost:8080/swagger-ui.html` when the application is running.

### Main Endpoints

#### Wallet Management

- `POST /api/wallets` - Create a new wallet
- `GET /api/wallets/{walletId}` - Get wallet details
- `DELETE /api/wallets/{walletId}` - Delete a wallet

#### Asset Management

- `POST /api/wallets/{walletId}/assets` - Add an asset to a wallet
- `DELETE /api/wallets/{walletId}/assets/{symbol}` - Remove an asset from a wallet

#### Simulation

- `POST /api/wallets/simulate` - Simulate wallet profit

## Architecture

The application follows a clean architecture approach with the following layers:

- **Domain**: Core business logic and entities
- **Application**: Use cases and business rules
- **Adapter**: External interfaces (REST, Database, External APIs)
- **Infrastructure**: Technical implementations and configurations

## Development

### Running Tests

```bash
./gradlew test
```

### Database Migrations

```bash
./gradlew flywayMigrate
```

### Building

```bash
./gradlew build
```

## Logging

The application uses SLF4J with Logback for logging. Logs are written to both console and file:

- Console: Standard output
- File: `./logs/crypto-wallet.log`

Log files are rotated daily and when they reach 10MB. Logs are kept for 30 days.

