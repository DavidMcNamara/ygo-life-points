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
import android.widget.TextView;

import org.w3c.dom.Text;

public class FullscreenActivity extends AppCompatActivity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    // PLAYER ONE
    private TextView playerOneLpDisplay;
    // PLAYER TWO
    private TextView playerTwoLpDisplay;
    // OPTIONS BUTTON
    private Button optionsButton;
    // COIN FLIP BUTTON
    private Button coinFlipButton;

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

        // set up the settings button
        optionsButtonSetup();
        // set up the coin flip button
        coinFlipSetup();
        ////////////////////////////////
        // get the device orientation
        // TODO change the layout depending on the device orientation
        String orientation = getScreenOrientation();

        boolean sideBySide = true; // temp

        // screen info
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        // TODO align the life point displays in the correct location when everything is working correctly
        // Player one lp display
        playerOneLpDisplay = (TextView) findViewById(R.id.playerOneLpDisplay);
        if(!sideBySide)
            playerOneLpDisplay.setPadding(0, height - (height/4), 0, 0);
        else
            playerOneLpDisplay.setPadding(width/4, height/2, 0, 0);
        playerOneLpDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("clicked the lp");
                createAlert("Update Player 1 Life Points", 1);
            }
        });
        // Player two lp display
        playerTwoLpDisplay = (TextView) findViewById(R.id.playerTwoLpDisplay);
        if(!sideBySide)
            playerTwoLpDisplay.setPadding(0, 0, 0, height/4);
        else
            playerTwoLpDisplay.setPadding(width - (width/4), height/3, 0, 0);
        playerTwoLpDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("clicked the lp");
                createAlert("Update Player 2 Life Points", 2);
            }
        });

        // Set up the user interaction to manually show or hide the system UI.
//        mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                System.out.println("User Clicked the screen");
//            }
//        });

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

    private void flipCoin(){
        // TODO add an animation to this
        // To just implement the functionality of this feature create a dialog
        Intent intent = new Intent(this, coinFlip.class);
        startActivity(intent);
    }
    private void rollDice(){

    }
    private void viewLog(){

    }

    private String getScreenOrientation(){
        int orientation = getWindowManager().getDefaultDisplay().getRotation();
        if (orientation%4==0 || orientation%4==2)
            return "Portrait";
        else if (orientation%4==1 || orientation%4==3)
            return "Landscape";
        return "Something when wrong";
    }

    private void optionsButtonSetup(){
        optionsButton = (Button) findViewById(R.id.options_button);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySettings();
            }
        });
    }

    private void coinFlipSetup(){
        coinFlipButton = (Button) findViewById(R.id.coin_flip_button);
        coinFlipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCoin();
            }
        });
    }
}