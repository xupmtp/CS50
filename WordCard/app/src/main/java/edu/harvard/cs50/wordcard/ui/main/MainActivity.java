package edu.harvard.cs50.wordcard.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.harvard.cs50.wordcard.CheckLogin;
import edu.harvard.cs50.wordcard.ui.profile.EditProfileActivity;
import edu.harvard.cs50.wordcard.R;
import edu.harvard.cs50.wordcard.adapter.LessonAdapter;
import edu.harvard.cs50.wordcard.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LessonAdapter lessonAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.main_recycle_view);
        lessonAdapter = new LessonAdapter();
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(lessonAdapter);

        FloatingActionButton addButton = findViewById(R.id.add_lesson_button);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this.getApplicationContext(), AddLessonActivity.class);
            this.startActivity(intent);
        });
    }

    /**
     * 啟用menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //指定mainActivity使用main_menu.xml
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    /**
     * menu選單事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title = item.getTitle().toString();
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        if (getString(R.string.logout).equals(title)){
            getSharedPreferences("session", MODE_PRIVATE).edit().clear().apply();
            intent = new Intent(this.getApplicationContext(), LoginActivity.class);
        } else if (getString(R.string.edit_profile).equals(title)) {
            intent = new Intent(this.getApplicationContext(), EditProfileActivity.class);
            intent.putExtra("username", getIntent().getStringExtra("username"));
        }
        this.startActivity(intent);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        int userId = getSharedPreferences("session", MODE_PRIVATE).getInt("id", -1);
        if (!CheckLogin.checkId(userId, this))
            return;
        lessonAdapter.reload(userId);
    }
}