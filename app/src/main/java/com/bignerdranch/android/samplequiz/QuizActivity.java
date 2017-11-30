package com.bignerdranch.android.samplequiz;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Arrays;

public class QuizActivity extends AppCompatActivity
{
    // constant variables
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_CHEAT_COUNT = "cheat_count";
    private static final String KEY_ANSWER_COUNT = "answer_count";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final int MAX_CHEAT_ALLOWED = 3;
    private static final String[] KEY_ANS = new String[]
            {
                    new String("ans1"),
                    new String("ans2"),
                    new String("ans3"),
                    new String("ans4"),
            };
    private static final String[] KEY_CHEATED = new String[]
            {
                    new String("quest1"),
                    new String("quest2"),
                    new String("quest3"),
                    new String("quest4"),
            };

    // variables
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mBackButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private TextView mAnswerCountTextView;
    private TextView mCheatLeftTextView;

    private Question[] mQuestionBank = new Question[]
            {
                   new Question(R.string.question1,true),
                   new Question(R.string.question2,true),
                   new Question(R.string.question3,true),
                   new Question(R.string.question4,true),
            };

    private int[] mAnswered = new int[mQuestionBank.length];
    private boolean[] mCheated = new boolean[mQuestionBank.length];

    private int mCurrentIndex = 0;
    private int mCheatCount = 0;
    private int mAnswerCount = 0;

    //---------------------Methods-----------------------

    // go to the next/previous question
    private void updateQuestion(boolean goNext)
    {
        if (goNext)
        {
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        }
        else if (mCurrentIndex == 0)
            mCurrentIndex = mQuestionBank.length - 1;
        else mCurrentIndex -= 1;
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    // checks user's answer and shows the corresponding toast
    private void checkAnswer(boolean userPressedTrue)
    {
        boolean answer = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int message;
        if (userPressedTrue == answer)
        {
            ++mAnswerCount;
            if (mCheated[mCurrentIndex])
                message = R.string.after_cheat_toast;
            else
                message = R.string.true_toast;
            mAnswered[mCurrentIndex] = 1;
            mAnswerCountTextView.setText("Correct answer: " + mAnswerCount + "/" + mQuestionBank.length);
        }
        else
        {
            message = R.string.false_toast;
            mAnswered[mCurrentIndex] = 2;
        }
        Toast.makeText(QuizActivity.this,message,Toast.LENGTH_SHORT).show();

        if (mCurrentIndex == mQuestionBank.length-1 && mAnswered[mCurrentIndex] != 0)
        {
            int correctAns = 0;
            for (int i=0; i < mQuestionBank.length; ++i)
                if (mAnswered[i] == 1)
                    ++correctAns;
            String msg = "You answered correctly " + correctAns + "/" +
                    mQuestionBank.length + " questions.";
            Toast.makeText(QuizActivity.this,msg,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate(Bundle) called.");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState == null)
        {
            Arrays.fill(mAnswered, 0);
            Arrays.fill(mCheated, false);
        }

        if (savedInstanceState != null)
        {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            mCheatCount = savedInstanceState.getInt(KEY_CHEAT_COUNT,0);
            mAnswerCount = savedInstanceState.getInt(KEY_ANSWER_COUNT,0);
            for (int i=0; i < mQuestionBank.length; ++i)
            {
                mAnswered[i] = savedInstanceState.getInt(KEY_ANS[i],0);
                mCheated[i] = savedInstanceState.getBoolean(KEY_CHEATED[i],false);
            }
        }

        mAnswerCountTextView = (TextView) findViewById(R.id.answer_count);
        mAnswerCountTextView.setText("Correct answers: " + mAnswerCount + "/" + mQuestionBank.length);

        mCheatLeftTextView = (TextView) findViewById(R.id.cheat_left);
        mCheatLeftTextView.setText("Remaining cheats: " + (MAX_CHEAT_ALLOWED - mCheatCount));

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getTextResId());
        mQuestionTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateQuestion(true);
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mAnswered[mCurrentIndex] == 0)
                    checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mAnswered[mCurrentIndex] == 0)
                    checkAnswer(false);
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateQuestion(true);
            }
        });

        mBackButton = (Button) findViewById(R.id.prev_button);
        mBackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateQuestion(false);
            }
        });

        /*-----Cheat button-----
        Set up "Cheat" button listener.
        Call CheatActivity and transfer current question's answer
        ----------------------*/
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mCheatCount < MAX_CHEAT_ALLOWED)
                {
                    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                    Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                    startActivityForResult(intent, REQUEST_CODE_CHEAT);
                }
                else
                    Toast.makeText(QuizActivity.this,R.string.max_cheat_exceeded,Toast.LENGTH_SHORT).show();
            }
        });
    }

    // save current states
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG,"onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX,mCurrentIndex);
        savedInstanceState.putInt(KEY_CHEAT_COUNT,mCheatCount);
        savedInstanceState.putInt(KEY_ANSWER_COUNT,mAnswerCount);
        for (int i=0; i < mQuestionBank.length; ++i)
        {
            savedInstanceState.putInt(KEY_ANS[i],mAnswered[i]);
            savedInstanceState.putBoolean(KEY_CHEATED[i],mCheated[i]);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == REQUEST_CODE_CHEAT && data == null)
            return;
        mCheated[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
        if (mCheated[mCurrentIndex])
        {
            ++mCheatCount;
            mCheatLeftTextView.setText("Remaining cheat: " + (MAX_CHEAT_ALLOWED - mCheatCount));
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(TAG,"onStart() called.");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG,"onResume() called.");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG,"onPause() called.");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(TAG,"onStop() called.");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG,"onDestroy() called");
    }
}

