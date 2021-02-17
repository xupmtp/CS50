package edu.harvard.cs50.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NoteActivity extends AppCompatActivity {
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Intent intent = getIntent();
        editText = findViewById(R.id.note_edit_text);
        editText.setText(intent.getStringExtra("content"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        MainActivity.database.noteDao().save(editText.getText().toString(), id);
    }

    /**
     * 當用戶點擊刪除按紐時,刪除當前note並返回MainActivity
     * @param v
     */
    public void deleteNote(View v) {
        //呼叫DAO的刪除方法
        MainActivity.database.noteDao().delete(getIntent().getIntExtra("id", 0));
        //finish()方法關閉當前視窗並返回上一個activity
        super.finish();
    }
}
