package edu.harvard.cs50.wordcard.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.harvard.cs50.wordcard.R;
import edu.harvard.cs50.wordcard.ui.Word.WordActivity;
import edu.harvard.cs50.wordcard.model.Lessons;
import edu.harvard.cs50.wordcard.ui.login.LoginActivity;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private List<Lessons> lessonsList = new ArrayList<>();

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lesson_row, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lessons current = lessonsList.get(position);
        holder.textView.setText(current.getName());
        holder.containLayout.setTag(current);
    }

    @Override
    public int getItemCount() {
        return lessonsList.size();
    }

    public void reload(int userId) {
        lessonsList = LoginActivity.database.lessonsDao().selectByUserId(userId);
        notifyDataSetChanged();
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        LinearLayout containLayout;
        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.lesson_row_text);
            containLayout = itemView.findViewById(R.id.lesson_row);

            containLayout.setOnClickListener(v -> {
                Lessons current = (Lessons) v.getTag();

                //點擊recycleView跳轉到wordActivity
                Intent intent = new Intent(v.getContext(), WordActivity.class);
                intent.putExtra("lessonId", current.getId());
                v.getContext().startActivity(intent);
            });
        }
    }
}
