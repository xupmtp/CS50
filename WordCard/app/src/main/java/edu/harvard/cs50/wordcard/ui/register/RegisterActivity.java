package edu.harvard.cs50.wordcard.ui.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import edu.harvard.cs50.wordcard.MainActivity;
import edu.harvard.cs50.wordcard.R;
import edu.harvard.cs50.wordcard.dao.UsersDao;
import edu.harvard.cs50.wordcard.model.Users;
import edu.harvard.cs50.wordcard.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    EditText password_check;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        password_check = findViewById(R.id.pwd_check);
        email = findViewById(R.id.email);
    }

    /**
     * 註冊按鈕事件
     * @param view
     */
    public void register(View view) {
        UsersDao dao = LoginActivity.database.usersDao();
        Users users = new Users(username.getText().toString().trim(),
                password.getText().toString().trim(),
                email.getText().toString().trim());
        String pwdCheckText = password_check.getText().toString().trim();

        if (users.getName().length() == 0) {
            username.setError(getString(R.string.invalid_username));
        } else if (users.getPassword().length() == 0) {
            password.setError(getString(R.string.invalid_password));
        } else if (!pwdCheckText.equals(users.getPassword())) {
            password_check.setError(getString(R.string.invalid_password_check));
        } else {
            try{
                dao.insertUser(users);
            } catch (SQLiteConstraintException e) {
                Log.e("SQL", "Insert error", e);
                username.setError("user name already exists");
                return;
            }
            List<Users> userList = dao.selectByName(users.getName());
            Intent intent = new Intent(this.getApplication(), MainActivity.class);
            intent.putExtra("userId", userList.get(0).getId());
            intent.putExtra("username", userList.get(0).getName());

            this.startActivity(intent);
        }
    }
}