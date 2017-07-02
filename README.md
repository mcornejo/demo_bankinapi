# Bankin API 

The idea of this project is to show the application of consuming an API. In here we present 
two REST API, one to compute the _arrondis_ for each transaction for a given user. The 
second one computes the aggregation of the _arrondis_ for a given list of users.

## Internals

In order to connect to the Bankin API, a client_id and client_secret is required, along with 
the user email and password credentials. The classes `BankinAPI` and `User` are meant to hold
these credentials respectively.

After the connection is set up, a promise is returned with the access_token. This promise
is passed to the method (another promise) in charge to perform the queries. 
 
Once the query is resolved, the data collected is stored in different classes: 
- `Transactions`: Represents one page of transactions from the Bankin API.
- `Transaction`: Represents a single transaction for a given user.
- `BankinResource`: Represents a `resource` object of Bankin API.
- `Pagination`: Represents the pagination information of each page.
- `User`: Represents a user, with its username and password.
- `BankinAPIDB` and `UserDB`: Represent a storage for the crendentials.

Once the information was retrieved and stored in objects, it follows the next_uri to process 
the next page. This process is done until the last page is rendered.
  
To output the results, the objects are treated using Java 8 `streams`: They are filtered, 
mapped and collected in order to extract the information relative to the query.  

# API

## Reference
This API provides two REST API:

- `/arrondis/<user>`: It returns a list of transactions, with their amount and arrondis calculated.
- `/aggregateArrondis/since/until`: IT returns the aggregation of all arrondis for all users, along with their email

## Usage
To run the project, just open the terminal and run:

```bash
$ cd challenge
$ sbt run
```

Open a browser, and query the API as the example:

To retrieve the list of arrondis:

- `http://localhost:9000/arrondis/user1@mail.com`
- `http://localhost:9000/arrondis/user2@mail.com`
- `http://localhost:9000/arrondis/user3@mail.com`

An example response would be:

```json
[{
	"arrondis": 1.51,
	"amount": -138.49,
	"transaction_id": 38000003650888
}, {
	"arrondis": 3.63,
	"amount": -26.38,
	"transaction_id": 38000003650892
}, ... ]
```

To retrieve the aggregation:

- `http://localhost:9000/aggregateArrondis/2010-05-01/2017-06-01`
- `http://localhost:9000/aggregateArrondis/2016-05-01/2016-06-01`
- `http://localhost:9000/aggregateArrondis/2010-05-01/2010-06-01`

An example response for the aggregated data would be:
```json
{
	"accounts": ["user@mail.com"],
	"totalArrondis": 120.12
}
```




## Test
To run the tests, open a terminal and run:
To run the project, just open the folder and run:

```bash
$ cd challenge
$ sbt test
```

## Documentation
Classes and code contains its own comments explaning how they work.
