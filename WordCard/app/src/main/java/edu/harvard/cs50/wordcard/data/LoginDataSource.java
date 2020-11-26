package edu.harvard.cs50.wordcard.data;

import edu.harvard.cs50.wordcard.data.model.LoggedInUser;
import edu.harvard.cs50.wordcard.model.Users;
import edu.harvard.cs50.wordcard.ui.login.LoginActivity;

import java.io.IOException;
import java.util.List;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<Users> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            List<Users> usersList = LoginActivity.database.usersDao().login(username, password);
            if (usersList.size() < 1)
                throw new Exception();
            return new Result.Success<>(usersList.get(0));
        } catch (Exception e) {
            return new Result.Error(new IOException("User name or password error", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}