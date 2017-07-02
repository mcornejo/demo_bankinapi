package controllers;

import akka.actor.ActorSystem;
import javax.inject.*;

import entities.BankinAPI;
import entities.*;
import entities.User;
import play.libs.Json;
import play.mvc.*;
import play.libs.ws.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.*;

import akka.actor.Scheduler;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import scala.concurrent.ExecutionContext;
import scala.concurrent.ExecutionContextExecutor;
import play.data.FormFactory;

import static java.util.stream.Collectors.toList;


@Singleton
public class AsyncController extends Controller {

    private final ActorSystem actorSystem;
    private final ExecutionContextExecutor exec;

    @Inject WSClient ws;

    @Inject
    public AsyncController(ActorSystem actorSystem, ExecutionContextExecutor exec) {
      this.actorSystem = actorSystem;
      this.exec = exec;
    }

    /*
     * Returns a JSON response with a List of transactions_id, the amount and the arrondis.
     * The email argument is required to select the user among different ones.
     *
     **/
    public CompletionStage<Result> arrondis(String email)  {
        /*
         * Get the connection information, (client_id and secret).
         * Search for the user in the User Store
         */
        UserDB usersdb = new UserDB();
        BankinAPIDB apiConnection = new BankinAPIDB();
        Optional<User> user = usersdb.getUserByEmail(email);
        BankinAPI connection = apiConnection.getCredentials();


        /*
         * Check for the user whether exists or not.
         * If the user does not exist, then a notFound() is returned to the client.
         * */
        if(!user.isPresent())
            return CompletableFuture.completedFuture(notFound("<h1>User not found</h1>").as("text/html"));

        /*
         *  Login to BankingAPI to get the credentials and in particular the access_token.
         */
        CompletionStage<User> futLogin = futureLogin(connection, user.get());


        /*
         * After getting the access_token, the getTransactions query is prepared (adding the limit header)
         * getTransactions returns the first page of the transactions for the user.
         * */
        HashMap<String, String> limit = new HashMap<>();
        limit.put("limit", "500");
        CompletionStage<Transactions> currentTransaction = getTransactions(connection, futLogin, limit);

        /*
         * This try-catch block inspects the first page of the transactions to check if there is more pages
         * to retrieve. It stores each page retrieved in the List allTransactions.
         */
        ArrayList<Transactions> allTransactions = new ArrayList<>();
        try {
            allTransactions = getAllTransactions(currentTransaction, connection);
        } catch(Exception e){
            System.out.println("ERROR: " + e.getMessage());
        }

        /*
         * After all the transaction pages from the Bankin API are retrieved, they are merged using mergeTransactions
         */
        List<Transaction> collectedTransactions = mergeTransactions(allTransactions);

        /* Get the currencies */
        List<String> currencies = collectedTransactions.stream()
                .filter(distinctByKey(p -> p.getCurrency_code()))
                .map(t -> t.getCurrency_code())
                .collect(toList());

        List<Arrondis> arrondis = currencies.stream()
                .flatMap(currency -> getArrondis(collectedTransactions, currency).stream())
                .collect(toList());

        //List<Arrondis> arrondis = getArrondis(collectedTransactions))
        /*
         * After having all the transactions merged in collectedTransactions, the arrondis is computed
         * for all the list, then it is converted to Json and returned to the client.
         */
        return CompletableFuture.completedFuture(ok(Json.toJson(arrondis)));

        /*
        // To return just the list of arrondis
        List<Double> amounts = getPayments(collectedTransactions);
        return CompletableFuture.completedFuture(ok(Json.toJson(amounts.stream().map(a -> calculateArrondis(a)).collect(toList()))));
        */
    }


    /*
     * Returns a JSON response with the aggregated arrondis and the users emails for a list of users.
     * It takes two parameters as input: since and until that represent the timeframe of the transactions required
     *
     **/
    public CompletionStage<Result> aggregateArrondis(String since, String until)  {
        /*
         * Get the connection information, (client_id and secret).
         * Search for the user in the User Store
         */
        UserDB usersdb = new UserDB();
        BankinAPIDB apiConnection = new BankinAPIDB();
        List<User> users = usersdb.getAllUsers();
        BankinAPI connection = apiConnection.getCredentials();

        /*
         * Check for the user Store whether the users exist or not.
         * If there is no user stored, then a notFound() is returned to the client.
         * */
        if(users.isEmpty())
            return CompletableFuture.completedFuture(notFound("<h1>Users not found</h1>").as("text/html"));

        /*
         *  To get the first page of every transaction, first the query is prepared (adding the limit header)
         *  then, the login is computed for each user in the list users (first map)
         *  then, the first page of the transactions is got for each user in the list (second map)
         *  then, the following pages (as list of lists) are retrieved for each user (flatMap)
         *  Finally, all the transactions are collected in one list as `currentTransactions'.
         */
        HashMap<String, String> headers = new HashMap<>();
        headers.put("limit", "500");
        headers.put("since", since);
        headers.put("until", until);
        List<Transactions> currentTransactions = users.stream()
                .map(user ->futureLogin(connection, user))
                .map(futLogin -> getTransactions(connection, futLogin, headers))
                .flatMap(currentTransaction -> getAllTransactions(currentTransaction, connection).stream())
                .collect(toList());

        /*
         * After having the list of all transactions (for all the users) it is merged in one list (collectedTransactions)
         */
        ArrayList<Transactions> allTransactions = new ArrayList<Transactions>(currentTransactions);
        List<Transaction> collectedTransactions = mergeTransactions(allTransactions);

        /*
         * All the transactions are map/reduced to get the total of the arrondis.
         */

        List<String> currencies = collectedTransactions.stream()
                .filter(distinctByKey(p -> p.getCurrency_code()))
                .map(t -> t.getCurrency_code())
                .collect(toList());

        List<AggregateArrondis> allArrondis = currencies.stream()
                .map(currency -> {
                            List<Arrondis> arrondis = getArrondis(collectedTransactions, currency);
                            Double total = arrondis.stream().map(e-> e.getArrondis()).reduce(0.0, (a,b) -> a+b);
                            List<String> curr_users = users.stream().map(u -> u.getEmail()).collect(toList());
                            return new AggregateArrondis(total, currency, curr_users);
                        })
                .collect(toList());

        /*
         * Once the total is already computed, the output is formatted: total + users email.
         */
        return CompletableFuture.completedFuture(ok(Json.toJson(allArrondis)));
    }


    /*
    * Auxiliary functions to query and compute the arrondis.
    *
    * The current architecture is based on one thread per user. This is basically due to the fact of pagination.
    * The pagination forces the API to wait for the response, read the next page, and perform the next query.
    *
    * */

    // POSSIBLE OPTIMIZATION: PROCESS 5 QUERIES PER SECOND, AND WHILE WAIT FOR THE ENDPOINT, PROCESS THE DATA.
    // POSSIBLE OPTIMIZATION: DO ALL THE PROBLEMS USING STREAMS AND MAPS IN ONE SHOT. FOR REDIBILITY IT WAS PREFERRED NOT TO.

    /*
    *  This method takes a list of Transactions (one page of the Bankin API) and returns
    *  a list of (single) transaction. This method is used to merge multiple pages of the Bankin API.
    * */
    private List<Transaction> mergeTransactions(ArrayList<Transactions> transactions) {
        return transactions.parallelStream()
                .flatMap(transaction -> transaction.getResources().stream())
                .collect(toList());
    }

    /*
    *  This method calculates the Arrondis for one amount.
    *  This is meant to be a lambda function to operate in each transaction.
    *  The input could have been a transaction, nevertheless, it was preferred to be object agnostic.
    * */
    private Double calculateArrondis(Double amount) {
        BigDecimal amountBD = (new BigDecimal(amount)).abs();
        BigDecimal arrondis = amountBD.divide(BigDecimal.TEN, RoundingMode.UP).setScale(0, RoundingMode.UP).multiply(BigDecimal.TEN);
        BigDecimal difference = arrondis.subtract(amountBD).setScale(2, RoundingMode.UP);
        return difference.doubleValue();
    }

    /*
    * The getArrondis method, takes a list of Transaction,
    * filter the payments (negative amounts),
    * map the calculateArrondis method to each element of the list
    * filter the arrondis equal to cero
    * Finally it collects all the Arrondis objets to a list.
    * */
    private List<Arrondis> getArrondis(List<Transaction> transactions, String currencyCode) {
        return transactions.parallelStream()
                .filter(transaction -> transaction.getAmount() < 0 && transaction.getCurrency_code().equals(currencyCode))
                .map(transaction ->  new Arrondis(calculateArrondis(transaction.getAmount()), transaction.getId(), transaction.getAmount(), currencyCode))
                .filter(arrondis -> arrondis.getArrondis() > 0)
                .collect(toList());
    }


    /*
    *  The method getAllTransactions takes a promise of Transactions (one page full of transaction) and the API credentials.
    *  This recursive method, process each page of the Bankin API and returns a List of pages to be processed in the future
    *
    *  It follows the typical recursive scheme:
    *  First it waits until the (promise) page is resolved and inspect the next_uri field:
    *
    *  a) If it is the last page, then it returns a list with the last page appended.
    *  b) If it is not the last page, then it creates a promise with the next page and appends all the transactions
    *
    * */
    private  ArrayList<Transactions> getAllTransactions(CompletionStage<Transactions> futureTransactions, BankinAPI connection) {
        try {
            Transactions transactions = futureTransactions.toCompletableFuture().get();
            if (transactions.getPagination() == null || transactions.getPagination().getNext_uri() == null) {
                ArrayList<Transactions> res = new ArrayList<Transactions>();
                res.add(transactions);
                return res;
            } else {
                String nextUrl = URLDecoder.decode(transactions.getPagination().getNext_uri(), "UTF-8");
                Map<String, String> args = parseURL(nextUrl);
                Thread.sleep(500);  // To be able to perform 5 queries per second
                CompletionStage<Transactions> currentTransaction = getTransactions(connection, transactions.getUser(), args);
                ArrayList<Transactions> res = getAllTransactions(currentTransaction, connection);
                res.add(transactions);
                return res;
            }
        } catch (Exception e) {
            System.out.println("ERROR getAllTransactions: " + e.getMessage() + " Line: " + e.getStackTrace()[1].getLineNumber());
            return new ArrayList<Transactions>();
        }
    }

    /*
    *  The parseURL method takes a string, and extract the information required to perfom the next query:
    *  The URL "/v2/transactions?after=MjAxNS0xMS0xNjo0NTU3ODE1Mg%3D%3D&limit=12&until=2016-04-06" is parsed
    *  to get the after value, limit, until, etc. It stores the values in a Map which is passed when the query is done.
    * */
    private Map<String, String>parseURL(String url) {

        List<String> tokens = new ArrayList<String>(Arrays.asList(url.substring(url.indexOf('?')+1).split("&")));
        Map<String, String> map =
                tokens.stream()
                .map(token -> token.split("=", 2))
                .collect(Collectors.toMap(e -> e[0], e-> e[1]));
        return map;
    }

    /*
    * The futureLogin method, retrieves the access_token of the user taking in account the private information of the API
    * */
    private CompletionStage<User> futureLogin(BankinAPI bankin, User user) {
        WSRequest request = ws.url(bankin.getAuthenticateURL());
        request.setHeader("Bankin-Version", bankin.getVERSION())
                .setQueryParameter("client_id", bankin.getClient_id())
                .setQueryParameter("client_secret", bankin.getClient_secret())
                .setQueryParameter("email", user.getEmail())
                .setQueryParameter("password", user.getPasswd());
        return request
                .post("")
                .thenApply(WSResponse::asJson)
                .thenApply(jsonResponse -> {
                    user.setAccessToken(jsonResponse.findPath("access_token").textValue());
                    user.setExpiresAt(jsonResponse.findPath("expires_at").textValue());
                    return user;
                });
    }

    /*
    * The getTransactions method, retrieves one page of the transactions API.
    * */
    private CompletionStage<Transactions> getTransactions(BankinAPI bankin, CompletionStage<User> user, Map<String, String> args) {
        return user.thenCompose(userUpdated -> {
            WSRequest request = ws.url(bankin.getTransactionsURL());
            request.setHeader("Bankin-Version", bankin.getVERSION())
                    .setHeader("Authorization", "Bearer " + userUpdated.getAccessToken())
                    .setQueryParameter("client_id", bankin.getClient_id())
                    .setQueryParameter("client_secret", bankin.getClient_secret());

            args.forEach((k, v) -> request.setQueryParameter(k, v));


            return request.get();
        }).thenApply(WSResponse::asJson)
                .thenApply(jsonTransaction -> {
                    Transactions res = Json.fromJson(jsonTransaction, Transactions.class);
                    res.setUser(user);
                    return res;
                });
    }


    private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


}
