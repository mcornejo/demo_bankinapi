package entities;

import play.data.validation.Constraints;

import java.util.*;

/**
 * This class represents the connection data of the Bankin API.
 * It holds information relative to Bankin like the URL for the queries,
 * the client_id, secret_id, the header version and API version.
 */
public class BankinAPI {

    private String client_id;
    private String client_secret;
    private String BASE_URL = "https://sync.bankin.com/";
    private String API_VERSION = "v2/";
    private String VERSION = "2016-01-18";

    public BankinAPI(String client_id, String client_secret) {
        this.client_id = client_id;
        this.client_secret = client_secret;
    }


    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getAuthenticateURL() {
        return BASE_URL+API_VERSION+"authenticate";
    }

    public String getListUsersURL() {
        return BASE_URL+API_VERSION+"users";
    }

    public String geBanksURL() {
        return BASE_URL+API_VERSION+"banks";
    }

    public String getAccountsURL() {
        return BASE_URL+API_VERSION+"accounts";
    }

    public String getItemsURL() {
        return BASE_URL+API_VERSION+"items";
    }

    public String getTransactionsURL() {
        return BASE_URL+API_VERSION+"transactions";
    }

    public String getVERSION() {
        return VERSION;
    }
}
