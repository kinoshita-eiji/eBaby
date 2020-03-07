package application;

import java.util.HashMap;

import application.exception.BadCredentialException;
import application.exception.DuplicatedUserException;
import application.exception.NotAuthenticatedException;
import application.role.PreferredSeller;
import application.role.Seller;

public class Users {

    private HashMap<String, User> registeredUser = new HashMap<String, User>();

    public User register(User user) {

        if (findByUserName(user.getUserName()) != null) {
            throw new DuplicatedUserException("You can't register same userName");
        }

        registeredUser.put(user.getUserName(), user);
        return user;

    }

    public User findByUserName(String userName) {
        return registeredUser.get(userName);

    }

    public User login(String userName, String password) {

        User user = findByUserName(userName);

        if (user == null) {
            throw new BadCredentialException("You can't login with inexistent user");
        }

        if (!user.getPassword().equals(password)) {
            throw new BadCredentialException("You can't login with wrong password");
        }

        user.setLoggedIn(true);
        registeredUser.put(userName, user);

        return user;
    }

    public User logout(String userName) {
         User user = findByUserName(userName);

         if (user == null) {
             throw new NotAuthenticatedException("You can't logout with inexistent user");
         }

         if (!user.isLoggedIn()) {
             throw new NotAuthenticatedException("You are not Authenticated");
         }

         user.setLoggedIn(false);
         registeredUser.put(userName, user);

         return user;
    }

    public void promoteToSeller(User user) {
        User foundUser = findByUserName(user.getUserName());
        foundUser.setRole(new Seller());
        registeredUser.put(foundUser.getUserName(), foundUser);
    }

    public void promoteToPreferredSeller(User user) {
        User foundUser = findByUserName(user.getUserName());
        foundUser.setRole(new PreferredSeller());
        registeredUser.put(foundUser.getUserName(), foundUser);
    }

}
