package entities;

/**
 * This class represents a storage database, which holds the private access information of the API
 */
public class BankinAPIDB {

    private BankinAPI api;

    public BankinAPIDB() {
        api = new BankinAPI("775683bc70d94beaa8044c81b2f16006", "sMgdYUzUPpo1DxbR67qP2ZbuTmU7H9gikvWPigDnQro9fk0PsRcb4EvI0iRheAJr");
    }

    public BankinAPI getCredentials() {
        return api;
    }

}
