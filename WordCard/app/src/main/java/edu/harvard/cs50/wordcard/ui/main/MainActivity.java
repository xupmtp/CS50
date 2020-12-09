package edu.harvard.cs50.wordcard.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.harvard.cs50.wordcard.CheckLogin;
import edu.harvard.cs50.wordcard.Util;
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

        // 新增lesson事件
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
        return Util.menuEvent(item, this);
    }

    /**
     * 每次回到此activity要重新loading
     */
    @Override
    protected void onResume() {
        super.onResume();
        int userId = getSharedPreferences("session", MODE_PRIVATE).getInt("id", -1);
        if (!CheckLogin.checkId(userId, this))
            return;
        lessonAdapter.reload(userId);
    }
}