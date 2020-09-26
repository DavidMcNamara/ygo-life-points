package com.example.ygolifepoints;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // PLAYERS
    private TextView playerOneLpDisplay;
    private TextView playerTwoLpDisplay;
    // BUTTONS
    private ImageView optionsButton;
    private ImageView coinFlipButton;
    private ImageView rollDiceButton;
    private ImageView displayLogButton;
    // CONTAINERS
    private LinearLayout lifePointContainer;
    public ArrayList<String> duelLog = new ArrayList<String>();

    public static SoundPool soundPool;
    public static int buttonClickedSound;
    private int lifePointSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up sound pool depending on android version

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        else
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        buttonClickedSound = soundPool.load(this, R.raw.button_click_sound, 1);
        lifePointSound = soundPool.load(this, R.raw.life_points_sound, 1);

        lifePointContainer = findViewById(R.id.life_point_container);
        lifePointContainer.setPadding(0, 0, 0, 0);

        optionsButtonSetup();
        coinFlipSetup();
        rollDiceSetup();
        displayLogSetup();
        hideUI();

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
                soundPool.play(lifePointSound, 1, 1, 0 , 0, 1);
                int inputValue = Integer.parseInt(input.getText().toString());
                updateLp(player, true, inputValue);
            }
        });
        builder.setNegativeButton("Decrease Life Points", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                soundPool.play(lifePointSound, 1, 1, 0 , 0, 1);
                int inputValue = Integer.parseInt(input.getText().toString());
                updateLp(player, false, inputValue);
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void updateLp(int player, boolean increase, int amount){
        int currentLp;
        String history = "";

        if(player == 1) {
            currentLp = Integer.parseInt(playerOneLpDisplay.getText().toString());
            history += "Player 1 ";
        }
        else {
            currentLp = Integer.parseInt(playerTwoLpDisplay.getText().toString());
            history += "Player 2 ";
        }
        if(increase) {
            currentLp += amount;
            history += "gained " + amount + " life points.\n";
        }
        else {
            currentLp -= amount;
            history += "lost " + amount + " life points.\n";
        }

        if(player == 1)
            playerOneLpDisplay.setText(String.valueOf(currentLp));
        else
            playerTwoLpDisplay.setText(String.valueOf(currentLp));

        duelLog.add(history);
        checkLifePoints(player, currentLp);
    }

    private void checkLifePoints(int player, int currentLp){
        String history = "";
        if(currentLp <= 0){
            history = ("Player " + player + " lost the game.\n");
            duelLog.add(history);
            System.out.println("Player " + player + " has lost the game");
            if(player == 1) {
                playerOneLpDisplay.setText("0");
            }
            else {
                playerTwoLpDisplay.setText("0");
            }
            displayResetOption();
        }
    }

    private void displayResetOption(){
        startActivity(new Intent(this, Settings.class));
    }

    // change to settings activity
    private void displaySettings(){
        playSound();
        startActivity(new Intent(this, Settings.class));
    }

    // change to display log activity
    private void displayLog(){
        playSound();
        Intent intent = new Intent(this, displayLog.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("bundle", duelLog);
        intent.putExtra("bundle", duelLog);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // change to coin flip activity
    private void flipCoin(){
        playSound();
        startActivity(new Intent(this, coinFlip.class));
    }

    // change to roll dice activity
    private void rollDice(){
        playSound();
        startActivity(new Intent(this, rollDice.class));
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

    private void displayLogSetup(){
        displayLogButton = findViewById(R.id.display_log_button);
        displayLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLog();
            }
        });
    }

    public void getScreenInfo(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
    }

    private void hideUI(){
        ActionBar ab = getSupportActionBar();
        ab.hide();
    }

    public static void playSound(){
        soundPool.play(buttonClickedSound, 1, 1, 0 , 0, 1);
    }
}