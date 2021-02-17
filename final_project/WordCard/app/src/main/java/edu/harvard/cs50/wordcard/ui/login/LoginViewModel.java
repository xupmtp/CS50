package edu.harvard.cs50.wordcard.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import edu.harvard.cs50.wordcard.data.LoginRepository;
import edu.harvard.cs50.wordcard.data.Result;
import edu.harvard.cs50.wordcard.data.model.LoggedInUser;
import edu.harvard.cs50.wordcard.R;
import edu.harvard.cs50.wordcard.model.Users;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<Users> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            Users data = ((Result.Success<Users>) result).getData();
            loginResult.setValue(new LoginResult(data));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed_text));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        return !username.trim().isEmpty();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 0;
    }
}