package edu.harvard.cs50.wordcard;

import android.content.Intent;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import edu.harvard.cs50.wordcard.ui.login.LoginActivity;
import edu.harvard.cs50.wordcard.ui.main.MainActivity;
import edu.harvard.cs50.wordcard.ui.profile.EditProfileActivity;

import static android.content.Context.MODE_PRIVATE;

public class Util {

    /**
     * toolBar按鈕點擊事件
     * @param item
     * @param activity
     * @return
     */
    public static boolean menuEvent (MenuItem item, AppCompatActivity activity) {
        // back按鈕 ID為android預設ID
        if( item.getItemId() == android.R.id.home) {
            activity.finish();
            return true;
        }

        String title = item.getTitle().toString();
        Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
        if (activity.getString(R.string.logout).equals(title)){
            activity.getSharedPreferences("session",  MODE_PRIVATE).edit().clear().apply();
            intent = new Intent(activity.getApplicationContext(), LoginActivity.class);
        } else if (activity.getString(R.string.edit_profile).equals(title)) {
            intent = new Intent(activity.getApplicationContext(), EditProfileActivity.class);
            intent.putExtra("username", activity.getIntent().getStringExtra("username"));
        }
        activity.startActivity(intent);
        return true;
    }
    
}
