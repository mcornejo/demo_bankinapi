package entities;

/**
 * This class represents a user of the system.
 */
public class User {
    private String email;
    private String passwd;
    private String accessToken;
    private String expiresAt;

    public User(String email, String passwd) {
        this.email = email;
        this.passwd = passwd;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", passwd='" + passwd + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", expiresAt='" + expiresAt + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public String getPasswd() {
        return passwd;
    }



    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

}
