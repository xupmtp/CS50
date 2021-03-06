package edu.harvard.cs50.wordcard.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

import edu.harvard.cs50.wordcard.CheckLogin;
import edu.harvard.cs50.wordcard.R;
import edu.harvard.cs50.wordcard.Util;
import edu.harvard.cs50.wordcard.dao.UsersDao;
import edu.harvard.cs50.wordcard.model.Users;
import edu.harvard.cs50.wordcard.ui.login.LoginActivity;

public class EditProfileActivity extends AppCompatActivity {

    EditText editPassword;
    EditText editPwdCheck;
    EditText editEmail;
    Users user;
    UsersDao dao = LoginActivity.database.usersDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        editPassword = findViewById(R.id.edit_password);
        editPwdCheck = findViewById(R.id.edit_pwd_check);
        editEmail = findViewById(R.id.edit_email);

        String name = getSharedPreferences("session", MODE_PRIVATE).getString("username", null);
        CheckLogin.checkName(name, this);
        user = dao.selectByName(name).get(0);
        editEmail.setText(user.getEmail());
    }

    /**
     * 編輯完成按鈕點擊事件
     * @param view
     */
    public void editProfile(View view) {
        String pwd = editPassword.getText().toString().trim();
        String pwdCheck = editPwdCheck.getText().toString().trim();
        user.setEmail(editEmail.getText().toString().trim());

        if (pwd.isEmpty()) {
            editPassword.setError(getString(R.string.invalid_password));
        } else if (!pwdCheck.equals(pwd)) {
            editPwdCheck.setError(getString(R.string.invalid_password_check));
        } else {
            user.setPassword(pwd);
            dao.updateUsers(user);

            getSharedPreferences("session", MODE_PRIVATE).edit().clear().apply();
            Toast.makeText(getApplicationContext(), getString(R.string.success_edit), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this.getApplicationContext(), LoginActivity.class);
            this.startActivity(intent);
        }
    }

    /**
     * toolbar event
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return Util.menuEvent(item, this);
    }
}