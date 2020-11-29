package edu.harvard.cs50.wordcard;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import edu.harvard.cs50.wordcard.ui.login.LoginActivity;

public class CheckLogin {
    public static boolean checkId(int id, AppCompatActivity activity) {
        if (id == -1) {
            Log.e("session", "login: lose session id");
            Intent intent = new Intent(activity.getApplicationContext(), LoginActivity.class);
            activity.startActivity(intent);
            return false;
        }
        return true;
    }

    public static boolean checkName(String name, AppCompatActivity activity) {
        if (name == null) {
            Log.e("session", "login: lose session name");
            Intent intent = new Intent(activity.getApplicationContext(), LoginActivity.class);
            activity.startActivity(intent);
            return false;
        }
        return true;
    }
}
