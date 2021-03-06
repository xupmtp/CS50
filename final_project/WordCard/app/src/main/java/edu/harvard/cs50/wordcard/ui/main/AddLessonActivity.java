package edu.harvard.cs50.wordcard.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

import edu.harvard.cs50.wordcard.CheckLogin;
import edu.harvard.cs50.wordcard.R;
import edu.harvard.cs50.wordcard.Util;
import edu.harvard.cs50.wordcard.model.Lessons;
import edu.harvard.cs50.wordcard.ui.login.LoginActivity;

public class AddLessonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lesson);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Button button = findViewById(R.id.new_lesson_button);
        EditText editText = findViewById(R.id.add_lesson);

        //新增button事件
        button.setOnClickListener(v -> {
            String text = editText.getText().toString().trim();
            if (text.isEmpty()) {
                editText.setError(getString(R.string.invalid_lesson_name));
                return;
            }
            int id = getSharedPreferences("session", MODE_PRIVATE).getInt("id", -1);
            if(!CheckLogin.checkId(id, this)) return;

            Lessons lessons = new Lessons(id, text, true);
            LoginActivity.database.lessonsDao().insertLesson(lessons);
            finish();
        });
    }

    @Override
    public void openOptionsMenu() {
        super.openOptionsMenu();
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