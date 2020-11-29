package edu.harvard.cs50.wordcard.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import edu.harvard.cs50.wordcard.ui.main.MainActivity;
import edu.harvard.cs50.wordcard.R;
import edu.harvard.cs50.wordcard.WordCardDatabase;
import edu.harvard.cs50.wordcard.model.Users;
import edu.harvard.cs50.wordcard.ui.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    public static WordCardDatabase database;
    private LoginViewModel loginViewModel;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        database = Room.databaseBuilder(this.getApplicationContext(), WordCardDatabase.class, "Wordcard")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        preferences = getSharedPreferences("session", MODE_PRIVATE);
        checkLogin();

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                    return;
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }


    private void updateUiWithUser(Users model) {
        String welcome = getString(R.string.welcome) + model.getName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();

        preferences.edit().putInt("id", model.getId()).apply();
        preferences.edit().putString("username", model.getName()).apply();

        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        intent.putExtra("id", model.getId());
        intent.putExtra("username", model.getName());

        this.startActivity(intent);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();

    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this.getApplicationContext(), RegisterActivity.class);
        this.startActivity(intent);
    }

    private void checkLogin() {
        int id = preferences.getInt("id", -1);
        String name = preferences.getString("username", null);
        if (name == null || id == -1)
            return;
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("username", name);
        this.startActivity(intent);
    }
}