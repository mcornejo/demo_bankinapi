package entities;

/**
 * This class represents the output of the arrondis query.
 * It stores the arrondis for a given transaction. It also holds
 * the transaction_id and the original amount in order to verify the arrondis.
 */
public class Arrondis {

    private Double arrondis;
    private Long transactionId;
    private Double amount;

    public Arrondis(Double arrondis, Long transactionId, Double amount) {
        this.arrondis = arrondis;
        this.transactionId = transactionId;
        this.amount = amount;
    }

    public Double getArrondis() {
        return arrondis;
    }

    public Long getTransaction_id() {
        return transactionId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
