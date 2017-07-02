package entities;

import java.util.List;

/**
 * This class represents the output of the AggregateArrondis query.
 * It stores the total as Double and a list of emails (it also could have held the uuid).
 *
 */
public class AggregateArrondis {

    private Double total;
    private List<String> accounts;

    public AggregateArrondis(){}

    public AggregateArrondis(Double total, List<String> accounts) {
        this.total = total;
        this.accounts = accounts;
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
}
