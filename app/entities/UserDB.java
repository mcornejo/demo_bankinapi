package entities;

import java.util.ArrayList;
import java.util.Optional;

/**
 * This class represents a storage database, which holds the private access information of the users
 */
public class UserDB {

    private ArrayList<User> users = new ArrayList<>();

    public UserDB() {
        users.add(new User("user1@mail.com", "a!Strongp#assword1"));
        users.add(new User("user2@mail.com", "a!Strongp#assword2"));
        users.add(new User("user3@mail.com", "a!Strongp#assword3"));
    }

    public Optional<User> getUserByEmail(String email) {
        return users.stream().filter(user -> user.getEmail().equals(email)).findFirst() ;
    }

    public ArrayList<User> getAllUsers(){
        return users;
    }

}
