package com.example.ygolifepoints;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class FullscreenActivity extends AppCompatActivity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    // PLAYERS
    private TextView playerOneLpDisplay;
    private TextView playerTwoLpDisplay;

    // BUTTONS
    private Button optionsButton;
    private Button coinFlipButton;
    private Button rollDiceButton;

    // CONTAINERS
    private LinearLayout lifePointContainer;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.playerOneLpDisplay);

        // get the screen info and set the position of the life points
        getScreenInfo();
        // set up the settings button
        optionsButtonSetup();
        // set up the coin flip button
        coinFlipSetup();
        // set up the dice roll button
        rollDiceSetup();

        // Player one lp display
        playerOneLpDisplay = findViewById(R.id.playerOneLpDisplay);
        playerOneLpDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlert("Update Player 1 Life Points", 1);
            }
        });

        // Player two lp display
        playerTwoLpDisplay = findViewById(R.id.playerTwoLpDisplay);
        playerTwoLpDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlert("Update Player 2 Life Points", 2);
            }
        });
    }

    // recalculate and reposition the life point location
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getScreenInfo();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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

    private void createAlert(String title, final int player) {
        // Create Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(player == 1)
            builder.setMessage("Player 1 = " + playerOneLpDisplay.getText()).setTitle(title).setCancelable(true);
        else
            builder.setMessage("Player 2 = " + playerTwoLpDisplay.getText()).setTitle(title).setCancelable(true);
        // Create input that accepts numbers
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        // Create apply button
        builder.setPositiveButton("Increase Life Points", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int inputValue = Integer.parseInt(input.getText().toString());
                updateLp(player, true, inputValue);
            }
        });
        builder.setNegativeButton("Decrease Life Points", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int inputValue = Integer.parseInt(input.getText().toString());
                updateLp(player, false, inputValue);
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void updateLp(int player, boolean increase, int amount){
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

    private void checkLifePoints(int player, int currentLp){
        if(currentLp <= 0){
            System.out.println("Player " + player + " has lost the game");
            if(player == 1)
                playerOneLpDisplay.setText("0");
            else
                playerTwoLpDisplay.setText("0");
            displayResetOption();
        }
    }

    private void displayResetOption(){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    private void displaySettings(){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    // change to coin flip activity
    private void flipCoin(){
        Intent intent = new Intent(this, coinFlip.class);
        startActivity(intent);
    }

    // change to roll dice activity
    private void rollDice(){
        Intent intent = new Intent(this, rollDice.class);
        startActivity(intent);
    }

    // change to log activity
    private void viewLog(){
        // a record of all life point changes
//        Intent intent = new Intent(this, viewLog.class);
//        startActivity(intent);
    }

    private void optionsButtonSetup(){
        optionsButton = findViewById(R.id.options_button);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySettings();
            }
        });
    }

    private void coinFlipSetup(){
        coinFlipButton = findViewById(R.id.coin_flip_button);
        coinFlipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCoin();
            }
        });
    }

    private void rollDiceSetup(){
        rollDiceButton = findViewById(R.id.dice_roll_button);
        rollDiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollDice();
            }
        });
    }

    public void getScreenInfo(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        lifePointContainer = findViewById(R.id.life_point_container);
        lifePointContainer.setPadding(width/2, height/2, 0, 0);
    }
}