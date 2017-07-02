package entities;

import java.util.List;

/**
 * This class represents the output of the AggregateArrondis query.
 * It stores the total as Double and a list of emails (it also could have held the uuid).
 *
 */
public class AggregateArrondis {

    private Double total;
    private String currency_code;
    private List<String> accounts;

    public AggregateArrondis(){}

    public AggregateArrondis(Double total, String currency_code, List<String> accounts) {
        this.total = total;
        this.accounts = accounts;
        this.currency_code = currency_code;
    }

    public Double getTotalArrondis() {
        return total;
    }

    public void setTotalArrondis(Double total) {
        this.total = total;
    }

    public List<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<String> accounts) {
        this.accounts = accounts;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }
}
