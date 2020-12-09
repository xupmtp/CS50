package edu.harvard.cs50.wordcard.ui.Word;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

import edu.harvard.cs50.wordcard.R;
import edu.harvard.cs50.wordcard.Util;
import edu.harvard.cs50.wordcard.model.Words;
import edu.harvard.cs50.wordcard.ui.login.LoginActivity;

public class AddWordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        EditText word = findViewById(R.id.add_word_name);
        EditText wordDetail = findViewById(R.id.add_word_detail);
        Button button = findViewById(R.id.new_word_button);
        int lessonId = getIntent().getIntExtra("lessonId", -1);

        button.setOnClickListener(v -> {
            Words words = new Words(lessonId,
                    word.getText().toString().trim(),
                    wordDetail.getText().toString().trim(),
                    false, true);
            try {
                LoginActivity.database.wordsDao().insertWord(words);
            } catch (Exception e) {
                Log.e("SQL", "Create new word error", e);
            }
            finish();
        });
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