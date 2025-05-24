# SPICE-P Code Challenge: Crypto wallet management

You need to create a **program** that helps people keep track of their “ **crypto money”** (also known as token), like
BTC or ETH, in a special **wallet**. This wallet stores information about different types of cryptocurrencies, how much
of each person owns, and how much it costs right now.

## **What the Program Should Do:**

1. **Get the Latest Prices** :
   o You will need to keep the user tokens prices updated, and for that you must **get the latest price** of
   each token using the **pricing API (CoinCap)**. This API has endpoints to tell the token price.
   o Do it recurrently, but not too often, and this time must be configurable.
   o Check the prices at the same time for 3 tokens at once, using threads to be as quick as possible,
   but respecting the limit of 3 threads maximum.
2. **Save the Information** :
   o When you get the new price for each crypto, save that information into a **database**
   o The database will save the information about which crypto someone has, the quantity they have
   and its price.
3. **Create new wallet:**
   o By sending a request with his email, which should be unique, the user must be able to create an
   empty wallet and receive its saved data. If there is already a wallet created with this e-mail let the
   user know.
4. **Add asset to the wallet** :
   o The user must be able to add an assets (token) to the wallet, by passing symbol, price and quantity.
   The application should check on the **pricing API** that token’s price and not allowing it to be saved
   if the price is not found.
5. **Show Wallet Information** :
   o The program should have an **API** that shows the wallet information, with all tokens (symbol, price,
   quantity, value) and the **total** , which is **the wallet total value in USD** :

```json
{
  "id": "123",
  "total": 158000.0,
  "assets": [
    {
      "symbol": "BTC",
      "quantity": 1.5,
      "price": 100000.0,
      "value": 150000
    },
    {
      "symbol": "ETH",
      "quantity": 2,
      "price": 4000,
      "value": 8000
    }
  ]
}
```

## **Wallet profit simulation:**

- The system must have a functionality for the user to simulate a wallet evolution today or in a spe-
  cific date in the past
- **Input and Output:**

  - **Input** : You will receive the list of tokens containing symbol, quantity and its value. Like in the
    example below:

```json
{
  assets: [
    {
      symbol: "BTC",
      quantity: 0 .5,
      value: 35000
    },
    {
      symbol: "ETH",
      quantity: 4. 25 ,
      value: 15310.
    }
  ]
}
```

The example above means this wallet have **0. 5 BTC** (Bitcoin) with **a value of 35000. 00 8 USD (meaning 1 BTC
costs 70000)** and **4. 25 ETH** (Ethereum) with **15310. 71 USD value (a price of 3602.52 per ETH)**. The application
must calculate the appreciation or depreciation of each token, specifying the best and worst assets and their perfor-
mance. For this example, comparing with 07/01/2025, BTC is up 35.3561% and ETH is up 2.70575%, so that wallet
would worth around 63097.33 and show the total this worths in **USD.** The purpose of this service is to simulate **if
the user had invested in a certain wallet would he be in more profit or loss? Meaning that NOT necessarily
the user have that tokens, but want to evaluate the situation.**

- **Output** : After checking the prices from **CoinCap** , you should send back a result that looks something like
  this:

```json
{
  total: 63097.33,
  best_asset: "BTC",
  best_performance: 35. 35 ,
  worst_asset: "ETH",
  worst_performance: 2.
}
```

This tells the person:

- **total** : How much all this wallet would be worth in USD on that given time.
- **best_asset** : Which crypto made the most money.
- **best_performance** : How much that crypto went up in price (as a percentage).
- **worst_asset** : Which crypto lost the most money.
- **worst_performance** : How much that crypto went down in price (as a percentage).

## **Key Points:**

- **Project structure**
  - Create your entities and your logic with the current information in mind:
    - A **user** has only one **wallet**
    - A **wallet** can have from 0 - \* assets (tokens)
- **Assets recurrent update:**
  - **Frequency** : How often you check the prices is up to you but need to be configurable.
  - **Concurrency** : Check the prices of **3 crypto assets at the same time**.
- **Wallet evaluation:**
  - **Result** : The program will show the total value of the wallet and which crypto did the best and
    worst.
- **General:**
  - **Database** : Save the wallet information and prices in a database.
  - **Request and Response** : Requests and responses must be in JSON format.

**Technical Briefing:**

- Java 17+ with Spring 3.
- Build the project with Maven or Gradle.
- Write your code in English.
- Feel free to use any additional Java libraries you want.

**Tools You Need:**

- **Spring** framework (to help build the program).
- **SQL database** (to save information about the crypto assets).
- **CoinCap API:** https://docs.coincap.io
  - Signup for Free to create the **apiKey** : https://pro.coincap.io/signup
  - Assets: https://docs.coincap.io/#89deffa0-ab03-4e0a-8d92-637a857d2c
  - Price history: https://docs.coincap.io/#61e708a8- 8876 - 4fb2-a418-86f12f

**Deliverables:**

- Functional and well implemented requirements
- Write tests, it’s up to you what to test and how.
- You are free to use any library for database, the only requirement is that is must be SQL.
- **Please upload your code to a remote public Git repository and send the link.**

**Final observations:**

This project doesn't need to reflect the exact rules of web 3 wallets, so there’s no need to create real pri-
vate/public keys, you can use a generated ID to represent the wallet private key for example, also there’s no need
to capture the token name (only symbol). Focus on the development and code quality, and if you have spare time
feel free to add improvements.
