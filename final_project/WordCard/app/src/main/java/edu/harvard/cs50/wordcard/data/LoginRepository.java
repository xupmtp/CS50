package edu.harvard.cs50.wordcard.data;

import edu.harvard.cs50.wordcard.data.model.LoggedInUser;
import edu.harvard.cs50.wordcard.model.Users;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private Users user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setUsers(Users user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<Users> login(String username, String password) {
        // handle login
        Result<Users> result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setUsers(((Result.Success<Users>) result).getData());
        }
        return result;
    }
}