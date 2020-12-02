package edu.harvard.cs50.wordcard.ui.Word;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import edu.harvard.cs50.wordcard.R;

public class CardActivity extends AppCompatActivity {

    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean mIsBackVisible = false;
    private View cardFrontView;
    private View cardBackView;
    private View cardMain;
    private TextView frontTextView;
    private TextView backTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        cardBackView = findViewById(R.id.card_back);
        cardFrontView = findViewById(R.id.card_front);
        cardMain = findViewById(R.id.card_main);
        frontTextView = findViewById(R.id.card_front_text);
        backTextView = findViewById(R.id.card_back_text);

        frontTextView.setText(getIntent().getStringExtra("front"));
        backTextView.setText(getIntent().getStringExtra("back"));

        loadAnimations();
        changeCameraDistance();

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