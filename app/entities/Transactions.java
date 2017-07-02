package entities;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * This class represents one page full of transactions from the Bankin API. This is done for parse purposes.
 */
public class Transactions {

    private List<Transaction> resources;
    private Pagination pagination;
    private CompletionStage<User> user;

    public Transactions(){}

    public Transactions(List<Transaction> resources, Pagination pagination) {
        this.resources = resources;
        this.pagination = pagination;
    }

    public List<Transaction> getResources() {
        return resources;
    }

    public void setResources(List<Transaction> resources) {
        this.resources = resources;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public CompletionStage<User> getUser() {
        return user;
    }

    public void setUser(CompletionStage<User> user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Transactions{" +
                "resources=" + resources +
                ", pagination=" + pagination +
                '}';
    }
}
