package edu.harvard.cs50.wordcard.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.harvard.cs50.wordcard.dao.WordsDao;
import edu.harvard.cs50.wordcard.ui.Word.CardActivity;
import edu.harvard.cs50.wordcard.R;
import edu.harvard.cs50.wordcard.model.Words;
import edu.harvard.cs50.wordcard.ui.login.LoginActivity;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> implements Filterable {

    //存DB查到的完整資料
    List<Words> wordsList = new ArrayList<>();
    //存search後的暫存資料
    List<Words> wordsListFilter = new ArrayList<>();
    WordsDao wordDao = LoginActivity.database.wordsDao();
    //抓favorite switch狀態用
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
        Words current = wordsListFilter.get(position);
        holder.textView.setText(current.getFrontWord());
        holder.imageView.setImageResource(getStarId(current.isFavorite(), holder.itemView));
        holder.containLayout.setTag(current);
    }

    @Override
    public int getItemCount() {
        return wordsListFilter.size();
    }

    /**
     * favorite switch啟用時只抓最愛的words
     * @param lessonId
     * @param favorite switch是否啟用
     */
    public void reload(int lessonId, boolean favorite) {
        if (favorite)
            wordsList = wordDao.selectFavoriteWords(lessonId);
        else
            wordsList = wordDao.selectByLessons(lessonId);
        wordsListFilter = wordsList;
        notifyDataSetChanged();
    }


    /**
     * 黃星表示最愛 白星反之
     * @param favorite
     * @param itemView
     * @return 回傳黃星 or 白星 icon
     */
    private static int getStarId(boolean favorite, View itemView) {
        String star = favorite ? "on" : "off";
        return itemView.getResources().getIdentifier("@android:drawable/btn_star_big_" + star, null, null);
    }

    /**
     * 搜尋時呼叫的方法
     * @return
     */
    @Override
    public Filter getFilter() {
        return new wordFilter();
    }

    /**
     * 搜尋時過濾字元的class
     */
    public class wordFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Words> listFilter;
            if (constraint.toString().trim().length() == 0)
                listFilter = wordsList;
            else
                listFilter = wordsList.stream().filter(w -> w.getFrontWord().contains(constraint)).collect(Collectors.toList());
            results.values = listFilter;
            results.count = listFilter.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            wordsListFilter = (List<Words>) results.values;
            notifyDataSetChanged();
        }
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

            // 星星點擊時切換喜愛狀態並發出SQL更新該筆資料
            imageView.setOnClickListener(v -> {
                Words current = (Words) containLayout.getTag();

                boolean changeStar = !current.isFavorite();
                LoginActivity.database.wordsDao().updateFavorite(changeStar, current.getId());
                reload(current.getLessonsId(), favoriteSwitch.isChecked());
            });

            // 點擊recycleView跳轉至CardActivity
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
