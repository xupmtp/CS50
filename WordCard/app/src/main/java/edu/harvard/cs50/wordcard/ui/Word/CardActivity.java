package edu.harvard.cs50.wordcard.ui.Word;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.harvard.cs50.wordcard.R;
import edu.harvard.cs50.wordcard.dao.WordsDao;
import edu.harvard.cs50.wordcard.model.Words;
import edu.harvard.cs50.wordcard.ui.login.LoginActivity;

public class CardActivity extends AppCompatActivity {

    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean mIsBackVisible = false;
    private View cardFrontView;
    private View cardBackView;
    private View cardMain;


    int wordId;
    WordsDao dao = LoginActivity.database.wordsDao();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        cardBackView = findViewById(R.id.card_back);
        cardFrontView = findViewById(R.id.card_front);
        cardMain = findViewById(R.id.card_main);

        //正面元件
        ImageButton frontButton = findViewById(R.id.front_edit_button);
        TextView frontTextView = findViewById(R.id.card_front_text);
        Button frontFinish = findViewById(R.id.front_finish);
        Button frontCancel = findViewById(R.id.front_cancel);
        EditText frontEditText = findViewById(R.id.front_edit_text);

        //背面元件
        ImageButton backButton = findViewById(R.id.back_edit_button);
        TextView backTextView = findViewById(R.id.card_back_text);
        Button backFinish = findViewById(R.id.back_finish);
        Button backCancel = findViewById(R.id.back_cancel);
        EditText backEditText = findViewById(R.id.back_edit_text);


        String frontText = getIntent().getStringExtra("front");
        String backText = getIntent().getStringExtra("back");
        frontTextView.setText(frontText);
        frontEditText.setText(frontText);
        backTextView.setText(backText);
        backEditText.setText(backText);

        wordId = getIntent().getIntExtra("wordId", -1);
        if (wordId == -1) {
            Log.e("word", "get word id error");
            Intent intent = new Intent(this.getApplicationContext(), LoginActivity.class);
            this.startActivity(intent);
        }

        View[] frontViews = {
                frontEditText,
                frontTextView,
                frontButton,
                frontFinish,
                frontCancel};

        View[] backViews = {
                backEditText,
                backTextView,
                backButton,
                backFinish,
                backCancel};

        //front card edit event
        frontButton.setOnClickListener(v -> targetChange(frontText, frontViews));
        frontCancel.setOnClickListener(v -> targetChange(frontText, frontViews));
        frontFinish.setOnClickListener(v -> editFrontFinish(backText, frontViews));

        //back card edit event
        backButton.setOnClickListener(v -> targetChange(backText, backViews));
        backCancel.setOnClickListener(v -> targetChange(backText, backViews));
        backFinish.setOnClickListener(v -> editBackFinish(frontText, backViews));

        //啟用翻轉動畫
        loadAnimations();
        changeCameraDistance();
    }

    /**
     * 切換隱藏/顯示元件
     *
     * @param views
     */
    public void targetChange(View... views) {
        for (View v : views)
            v.setVisibility(v.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        //隱藏鍵盤
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(views[0].getWindowToken(), 0);
    }

    /**
     * card編輯&取消事件
     *
     * @param text  card text
     * @param views card元件, EditText必須放第一, textView第二
     */
    public void targetChange(String text, View... views) {
        targetChange(views);
        ((EditText) views[0]).setText(text);
    }

    /**
     * word 完成事件
     * @param views card元件, EditText必須放第一, textView第二
     */
    public void editFrontFinish(String back, View... views) {
        String frontText = ((EditText) views[0]).getText().toString();
        int res = dao.updateWordText(frontText, back, wordId);
        ((TextView) views[1]).setText(frontText);
        targetChange(views);
    }

    /**
     * word 完成事件
     * @param views card元件, EditText必須放第一, textView第二
     */
    public void editBackFinish(String front, View... views) {
        String backText = ((EditText) views[0]).getText().toString();
        int back = dao.updateWordText(front, backText, wordId);
        ((TextView) views[1]).setText(backText);
        targetChange(views);
    }

    /**
     * 翻轉時的畫面長度
     * distance設過低會導致失真
     */
    private void changeCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        cardFrontView.setCameraDistance(scale);
        cardBackView.setCameraDistance(scale);
    }

    /**
     * 加載動畫
     */
    private void loadAnimations() {
        final int time = 1000;
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.in_animation);

        mSetLeftIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                cardMain.setClickable(true);
            }
        });
        mSetRightOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

                cardMain.setClickable(false);
            }
        });
    }

    /**
     * 點擊事件 翻轉頁面
     *
     * @param view
     */
    public void flipCard(View view) {
        if (!mIsBackVisible) {
            mSetRightOut.setTarget(cardFrontView);
            mSetLeftIn.setTarget(cardBackView);
            mSetRightOut.start();
            mSetLeftIn.start();

            mIsBackVisible = true;
        } else {
            mSetRightOut.setTarget(cardBackView);
            mSetLeftIn.setTarget(cardFrontView);
            mSetRightOut.start();
            mSetLeftIn.start();

            mIsBackVisible = false;
        }
    }
}