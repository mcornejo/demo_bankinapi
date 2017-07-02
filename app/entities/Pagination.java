package entities;

/**
 * This class represents the pagination from the Bankin API. This is done for parse purposes.
 */
public class Pagination {
    public String previous_uri;
    public String next_uri;

    public Pagination(){

    }

    public Pagination(String previous_uri, String next_uri) {
        this.previous_uri = previous_uri;
        this.next_uri = next_uri;
    }

    public String getPrevious_uri() {
        return previous_uri;
    }

    public void setPrevious_uri(String previous_uri) {
        this.previous_uri = previous_uri;
    }

    public String getNext_uri() {
        return next_uri;
    }

    public void setNext_uri(String next_uri) {
        this.next_uri = next_uri;
    }

    @Override
    public String toString() {
        return "Pagination{" +
                "previous_uri='" + previous_uri + '\'' +
                ", next_uri='" + next_uri + '\'' +
                '}';
    }
}
