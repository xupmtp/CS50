package edu.harvard.cs50.wordcard.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import edu.harvard.cs50.wordcard.R;
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

        editPassword = findViewById(R.id.edit_password);
        editPwdCheck = findViewById(R.id.edit_pwd_check);
        editEmail = findViewById(R.id.edit_email);

        String name = getIntent().getStringExtra("username");
        user = dao.selectByName(name).get(0);
        editEmail.setText(user.getEmail());
    }

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
}