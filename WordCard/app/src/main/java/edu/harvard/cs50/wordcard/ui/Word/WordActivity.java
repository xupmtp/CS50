package edu.harvard.cs50.wordcard.ui.Word;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.harvard.cs50.wordcard.R;
import edu.harvard.cs50.wordcard.adapter.WordAdapter;

public class WordActivity extends AppCompatActivity {

    int lessonId;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private WordAdapter wordAdapter;
    private SwitchCompat favoriteSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        recyclerView = findViewById(R.id.word_recycle_view);
        layoutManager = new LinearLayoutManager(this);
        favoriteSwitch = findViewById(R.id.word_switch);

        wordAdapter = new WordAdapter(favoriteSwitch);
        lessonId = getIntent().getIntExtra("lessonId", -1);
        favoriteSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> wordAdapter.reload(lessonId, isChecked));

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(wordAdapter);

        FloatingActionButton button = findViewById(R.id.add_word_button);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this.getApplicationContext(), AddWordActivity.class);
            intent.putExtra("lessonId", lessonId);
            this.startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        getMenuInflater().inflate(R.menu.word_search, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        wordAdapter.reload(lessonId, favoriteSwitch.isChecked());
    }
}