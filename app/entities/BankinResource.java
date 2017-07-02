package entities;

/**
 * This class represents a resource from the Bankin API. This is done for parse purposes.
 */
public class BankinResource {

    private Long id;
    private String resource_uri;
    private String resource_type;

    public BankinResource(){}

    public BankinResource(Long id, String resource_uri, String resource_type) {
        this.id = id;
        this.resource_uri = resource_uri;
        this.resource_type = resource_type;
    }

    public Long getId() {
        return id;
    }

    public String getResource_uri() {
        return resource_uri;
    }

    public String getResource_type() {
        return resource_type;
    }

    @Override
    public String toString() {
        return "BankinResource{" +
                "id=" + id +
                ", resource_uri='" + resource_uri + '\'' +
                ", resource_type='" + resource_type + '\'' +
                '}';
    }
}
