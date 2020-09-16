package com.example.ygolifepoints;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class FullscreenActivity extends AppCompatActivity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    // PLAYER ONE
    private Button playerOneLpIncrease;
    private Button playerOneLpDecrease;
    private TextView playerOneLpDisplay;

    // PLAYER TWO
    private Button playerTwoLpIncrease;
    private Button playerTwoLpDecrease;
    private TextView playerTwoLpDisplay;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            // when the app runs hid the UI
            hide();
        }
    };

    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.playerOneLpDisplay);

        // Player one lp display
        playerOneLpDisplay = (TextView) findViewById(R.id.playerOneLpDisplay);

        // Player one button logic
        playerOneLpIncrease = (Button) findViewById(R.id.increaseLp);
        playerOneLpDecrease = (Button) findViewById(R.id.decreaseLp);

        // Player one increase lp onclick event
        playerOneLpIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlert("Increase Life Points", true, 1);
                System.out.println("Player 1 LP increase clicked");
            }
        });

        playerOneLpDecrease.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                createAlert("Decrease Life Points", false, 1);
                System.out.println("Player 1 LP decrease clicked");
            }
        });

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("User Clicked the screen");
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = true;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void createAlert(String title, final boolean increase, final int player) {
        // Create Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enter Value").setTitle(title).setCancelable(true);
        // Create input that accepts numbers
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        // Create apply button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int inputValue = Integer.parseInt(input.getText().toString());
                updateLp(player, increase, inputValue);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void updateLp(int player, boolean increase, int amount){
        int currentLp;

        if(player == 1)
            currentLp = Integer.parseInt(playerOneLpDisplay.getText().toString());
        else
            currentLp = Integer.parseInt(playerTwoLpDisplay.getText().toString());

        if(increase)
            currentLp += amount;
        else
            currentLp -= amount;

        if(player == 1)
            playerOneLpDisplay.setText(String.valueOf(currentLp));
        else
            playerTwoLpDisplay.setText(String.valueOf(currentLp));

        checkLifePoints(player, currentLp);
    }

    public void checkLifePoints(int player, int currentLp){
        if(currentLp <= 0){
            System.out.println("Player " + player + " has lost the game");
            if(player == 1)
                playerOneLpDisplay.setText("0");
            else
                playerTwoLpDisplay.setText("0");
            //displayResetOption();
        }

    }
}