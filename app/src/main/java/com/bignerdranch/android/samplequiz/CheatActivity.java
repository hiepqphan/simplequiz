package com.bignerdranch.android.samplequiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import java.lang.*;

import org.w3c.dom.Text;

public class CheatActivity extends AppCompatActivity
{
    // const variables
    private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.samplequiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.samplequiz.answer_shown";

    // variables
    private boolean mAnswerIsTrue;
    private Button mShowAnswerButton;
    private TextView mAnswerTextView;
    private TextView mAPITextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE,false);

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mAPITextView = (TextView) findViewById(R.id.API_level);

        mAPITextView.setText("API Level " + Build.VERSION.SDK_INT);

        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mAnswerIsTrue)
                    mAnswerTextView.setText("It's true mate");
                else
                    mAnswerTextView.setText("It's false mate");
                setAnswerShownResult(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                }
                else
                    mShowAnswerButton.setVisibility(View.INVISIBLE);

                //finish();
            }
        });
    }

    public static Intent newIntent(Context packageContext, boolean answerIsTrue)
    {
        Intent intent = new Intent(packageContext,CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue);
        return intent;
    };

    private void setAnswerShownResult(boolean isAnswerShown)
    {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK,data);
    }

    public static boolean wasAnswerShown(Intent result)
    {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN,false);
    }
}