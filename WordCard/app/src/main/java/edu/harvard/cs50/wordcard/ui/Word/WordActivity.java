package edu.harvard.cs50.wordcard.ui.Word;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import edu.harvard.cs50.wordcard.R;
import edu.harvard.cs50.wordcard.Util;
import edu.harvard.cs50.wordcard.adapter.WordAdapter;

public class WordActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    int lessonId;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private WordAdapter wordAdapter;
    private SwitchCompat favoriteSwitch;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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

        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView)menuItem.getActionView();
        searchView.setIconifiedByDefault(true);
        //綁定此class實做的OnQueryTextListener方法
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return Util.menuEvent(item, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //第一次進activity時searchView還未建立, 不調用getFilter()
        wordAdapter.reload(lessonId, favoriteSwitch.isChecked());
        if (searchView != null)
            wordAdapter.getFilter().filter(searchView.getQuery());
    }


    @Override
    public boolean onSupportNavigateUp() {
        // do your stuff
        wordAdapter.getFilter().filter(searchView.getQuery());
        return super.onSupportNavigateUp();
    }

    /**
     * search完成時
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        wordAdapter.getFilter().filter(query);
        return false;
    }

    /**
     * 每次key in文字時
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        wordAdapter.getFilter().filter(newText);
        return false;
    }
}