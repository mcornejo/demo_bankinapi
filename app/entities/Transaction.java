package entities;

/**
 * This class represents a Transaction from the Bankin API. This is done for parse purposes.
 */
public class Transaction {

    /* EXAMPLE:
      "id": 1000013123932,
      "description": "Prelevement Spotify SA",
      "raw_description": "Prlv 1512 Spotify SA",
      "amount": -4.99,
      "date": "2016-04-06",
      "updated_at": "2016-08-08T09:19:14Z",
      "is_deleted": false,
      "category": {
        "id": 1,
        "resource_uri": "/v2/categories/1",
        "resource_type": "category"
      },
      "account": {
        "id": 2341498,
        "resource_uri": "/v2/accounts/2341498",
        "resource_type": "account"
      },
      "resource_uri": "/v2/transactions/1000013123932",
      "resource_type": "transaction"
      */


    private Long id;
    private String description;
    private String raw_description;
    private Double amount;
    private String currency_code;
    private String date;
    private String updated_at;
    private Boolean is_deleted;
    private BankinResource category;
    private BankinResource account;
    private String resource_uri;
    private String resource_type;


    public Transaction(){}

    public Transaction(Long id, String description, String raw_description, Double amount,
                       String date, String updated_at, Boolean is_deleted, BankinResource category,
                       BankinResource account, String resource_uri, String resource_type){
        this.id = id;
        this.description = description;
        this.raw_description = raw_description;
        this.amount = amount;
        this.date = date;
        this.updated_at = updated_at;
        this.is_deleted = is_deleted;
        this.category = category;
        this.account = account;
        this.resource_uri = resource_uri;
        this.resource_type = resource_type;
    }


    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getRaw_description() {
        return raw_description;
    }

    public Double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public BankinResource getCategory() {
        return category;
    }

    public BankinResource getAccount() {
        return account;
    }

    public String getResource_uri() {
        return resource_uri;
    }

    public String getResource_type() {
        return resource_type;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", currency=" + currency_code +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                ", is_deleted=" + is_deleted +
                ", account=" + account +
                '}';
    }
}
