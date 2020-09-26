package com.example.ygolifepoints;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Image;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class rollDice extends AppCompatActivity {
    private ImageView removeDice;
    private ImageView addDice;
    private ImageView rollDice;
    private TextView results;
    private int numberOfDice;
    private LinearLayout ll;
    private SoundPool soundPool;
    private int rollDiceSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roll_dice);

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

        rollDiceSound = soundPool.load(this, R.raw.dice_roll_sound, 1);

        hideUI();

        ll = (LinearLayout) findViewById(R.id.dice_roll);

        results = (TextView) findViewById(R.id.roll_dice_results);
        results.setText("Number of dice: 1");

        numberOfDice = 1;

        addDiceSetup();
        removeDiceSetup();
        rollDiceSetup();
    }

    private void rollTheDice(){
        soundPool.play(rollDiceSound, 1, 1, 0, 0, 1);
        if(((LinearLayout) ll).getChildCount() > 0)
            ((LinearLayout) ll).removeAllViews();
        for(int i = 0 ; i < numberOfDice; i++) {
            ImageView dice = new ImageView(this);
            dice.setImageResource(R.drawable.dice_face_unknown);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0,0);
            dice.setLayoutParams(lp);
            ll.addView(dice);
            rollAnimation(dice);
        }
    }

    private void rollAnimation(final ImageView dice){
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);
        fadeOut.setFillAfter(true);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                for(int i = 0 ; i < numberOfDice; i++) {
                    double result = Math.random();
                    if(result <= 1.0/6.0)
                        dice.setImageResource(R.drawable.dice_face_1);
                    else if(1.0/6.0 < result  && result <= (1.0/6.0) * 2)
                        dice.setImageResource(R.drawable.dice_face_2);
                    else if((1.0/6.0) * 2 < result  && result <= (1.0/6.0) * 3)
                        dice.setImageResource(R.drawable.dice_face_3);
                    else if((1.0/6.0) * 3 < result  && result <= (1.0/6.0) * 4)
                        dice.setImageResource(R.drawable.dice_face_4);
                    else if((1.0/6.0) * 4 < result  && result <= (1.0/6.0) * 5)
                        dice.setImageResource(R.drawable.dice_face_5);
                    else if((1.0/6.0) * 5 < result  && result <= (1.0/6.0) * 6)
                        dice.setImageResource(R.drawable.dice_face_6);
                }
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(1000);
                fadeIn.setFillAfter(true);

                dice.startAnimation(fadeIn);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        dice.startAnimation(fadeOut);
    }

    private void addDiceSetup(){
        addDice = findViewById(R.id.add_dice);
        addDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.playSound();
                if(numberOfDice < 3){
                    numberOfDice++;
                    results.setText("Number of dice: "+ numberOfDice);
                }
            }
        });
    }

    private void removeDiceSetup(){
        removeDice = findViewById(R.id.remove_dice);
        removeDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.playSound();
                if(numberOfDice > 1){
                    numberOfDice--;
                    results.setText("Number of dice: "+ numberOfDice);
                }
            }
        });
    }

    private void rollDiceSetup(){
        rollDice = findViewById(R.id.roll_dice);
        rollDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollTheDice();
            }
        });
    }

    private void hideUI(){
        ActionBar ab = getSupportActionBar();
        ab.hide();
    }
}