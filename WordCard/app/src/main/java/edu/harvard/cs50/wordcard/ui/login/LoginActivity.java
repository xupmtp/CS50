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

/**
 * LoginActivity程式流程說明
 * 此功能採用MVVM設計模式,UI有任何更動都會觸發ViewModel內的方法
 * Start -> User輸入文字 -> 觸發afterTextChanged(),此時會呼叫loginViewModel.loginDataChanged() ->
 * 驗證使用者資料回傳驗證結果,此時 MutableLiveData<LoginFormState>會發生更動 ->
 * LiveData更動時會觸發事件,這裡觸發loginViewModel.getLoginFormState().observe() ->
 * 若有錯誤則會顯示訊息,否則login button啟用 ->
 * User點擊log in -> 觸發loginViewModel.login() ->
 * 從Repository返回驗證結果並更改MutableLiveData<LoginResult> ->
 * LiveData變動會呼叫loginViewModel.getLoginResult().observe() ->
 * 若登入成功呼叫updateUiWithUser()跳轉到MainActivity -> Finish
 */
public class LoginActivity extends AppCompatActivity {

    public static WordCardDatabase database;
    private LoginViewModel loginViewModel;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 建立ViewModel物件
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        // 建立DB物件 只建這一次,其他功能呼叫此物件來連線DB
        database = Room.databaseBuilder(this.getApplicationContext(), WordCardDatabase.class, "Wordcard")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        preferences = getSharedPreferences("session", MODE_PRIVATE);
        // 檢查登入
        checkLogin();

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);


        // 輸入資料時執行的change事件
        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
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
        });

        // login()觸發時執行的方法
        loginViewModel.getLoginResult().observe(this, loginResult -> {
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
        });

        // 輸入change事件
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
        // 輸入文字改變時觸發事件
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        // password輸入完成時若沒有驗證失敗也會觸發login()方法
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        // 登入按鈕事件
        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });
    }


    /**
     * 登入成功時跳轉至MainActivity
     * @param model
     */
    private void updateUiWithUser(Users model) {
        String welcome = getString(R.string.welcome) + model.getName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();

        preferences.edit().putInt("id", model.getId()).apply();
        preferences.edit().putString("username", model.getName()).apply();

        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        intent.putExtra("id", model.getId());
        intent.putExtra("username", model.getName());
        finish();

        this.startActivity(intent);
    }

    /**
     * 登入失敗時呼叫
     * @param errorString
     */
    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();

    }

    /**
     * 註冊文字點擊事件
     * @param view
     */
    public void goToRegister(View view) {
        Intent intent = new Intent(this.getApplicationContext(), RegisterActivity.class);
        this.startActivity(intent);
    }

    /**
     * 檢查若有登入過則跳轉至MainActivity
     */
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