package edu.harvard.cs50.wordcard.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.harvard.cs50.wordcard.dao.WordsDao;
import edu.harvard.cs50.wordcard.ui.Word.CardActivity;
import edu.harvard.cs50.wordcard.R;
import edu.harvard.cs50.wordcard.model.Words;
import edu.harvard.cs50.wordcard.ui.login.LoginActivity;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    List<Words> wordsList = new ArrayList<>();
    WordsDao wordDao = LoginActivity.database.wordsDao();
    private SwitchCompat favoriteSwitch;

    public WordAdapter(SwitchCompat favoriteSwitch) {
        this.favoriteSwitch = favoriteSwitch;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_row, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Words current = wordsList.get(position);
        holder.textView.setText(current.getFrontWord());
        holder.imageView.setImageResource(getStarId(current.isFavorite(), holder.itemView));
        holder.containLayout.setTag(current);
    }

    @Override
    public int getItemCount() {
        return wordsList.size();
    }

    public void reload(int lessonId, boolean favorite) {
        if (favorite)
            wordsList = wordDao.selectFavoriteWords(lessonId);
        else
            wordsList = wordDao.selectByLessons(lessonId);
        notifyDataSetChanged();
    }



    private static int getStarId(boolean favorite, View itemView) {
        String star = favorite ? "on" : "off";
        return itemView.getResources().getIdentifier("@android:drawable/btn_star_big_" + star, null, null);
    }

    public class WordViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageView imageView;
        LinearLayout containLayout;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.word_row_text);
            imageView = itemView.findViewById(R.id.word_star);
            containLayout = itemView.findViewById(R.id.word_row);

            imageView.setOnClickListener(v -> {
                Words current = (Words) containLayout.getTag();

                boolean changeStar = !current.isFavorite();
                LoginActivity.database.wordsDao().updateFavorite(changeStar, current.getId());
                reload(current.getLessonsId(), favoriteSwitch.isChecked());
            });

            containLayout.setOnClickListener(v -> {
                Words current = (Words) containLayout.getTag();

                Intent intent = new Intent(v.getContext(), CardActivity.class);
                intent.putExtra("wordId", current.getId());
                intent.putExtra("front", current.getFrontWord());
                intent.putExtra("back", current.getBackDetail());

                v.getContext().startActivity(intent);
            });
        }
    }
}
