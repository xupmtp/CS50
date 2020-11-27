package edu.harvard.cs50.wordcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import edu.harvard.cs50.wordcard.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView v = findViewById(R.id.main);
        v.setText(getIntent().getStringExtra("username"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //指定mainActivity使用main_menu.xml
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title = item.getTitle().toString();
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        if (getString(R.string.logout).equals(title)){
            getSharedPreferences("session", MODE_PRIVATE).edit().clear().apply();
            intent = new Intent(this.getApplicationContext(), LoginActivity.class);
        } else if (getString(R.string.change_password).equals(title)) {
            intent = new Intent(this.getApplicationContext(), EditProfileActivity.class);
            intent.putExtra("username", getIntent().getStringExtra("username"));
        }
        this.startActivity(intent);
        return true;
    }
}