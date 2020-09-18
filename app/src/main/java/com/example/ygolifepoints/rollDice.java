package com.example.ygolifepoints;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
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

    private Button removeDice;
    private Button addDice;
    private Button rollDice;
    private TextView results;
    private int numberOfDice;
    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roll_dice);

        ll = (LinearLayout) findViewById(R.id.dice_roll);

        results = (TextView) findViewById(R.id.roll_dice_results);
        results.setText("Number of dice: 1");

        numberOfDice = 1;

        addDice = (Button) findViewById(R.id.add_dice);
        addDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberOfDice < 3){
                    numberOfDice++;
                    // TODO add animation to show the dice as they are being added and removed
                    results.setText("Number of dice: "+ numberOfDice);
                }
            }
        });

        removeDice = (Button) findViewById(R.id.remove_dice);
        removeDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberOfDice > 1){
                    numberOfDice--;
                    // TODO add animation to show the dice as they are being added and removed
                    results.setText("Number of dice: "+ numberOfDice);
                }
            }
        });

        rollDice = (Button) findViewById(R.id.roll_dice);
        rollDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollTheDice();
            }
        });
    }

    private void rollTheDice(){
        if(((LinearLayout) ll).getChildCount() > 0)
            ((LinearLayout) ll).removeAllViews();
        for(int i = 0 ; i < numberOfDice; i++) {
            ImageView dice = new ImageView(this);
            dice.setImageResource(R.drawable.dice_face_unknown);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 100, 0,0);
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
                    System.out.println("This dice roll -> " + result);
                    if(result <= 1.0/6.0){
                        dice.setImageResource(R.drawable.dice_face_1);
                    }
                    else if(1.0/6.0 < result  && result <= (1.0/6.0) * 2){
                        dice.setImageResource(R.drawable.dice_face_2);
                    }
                    else if((1.0/6.0) * 2 < result  && result <= (1.0/6.0) * 3){
                        dice.setImageResource(R.drawable.dice_face_3);
                    }
                    else if((1.0/6.0) * 3 < result  && result <= (1.0/6.0) * 4){
                        dice.setImageResource(R.drawable.dice_face_4);
                    }
                    else if((1.0/6.0) * 4 < result  && result <= (1.0/6.0) * 5){
                        dice.setImageResource(R.drawable.dice_face_5);
                    }
                    else if((1.0/6.0) * 5 < result  && result <= (1.0/6.0) * 6){
                        dice.setImageResource(R.drawable.dice_face_6);
                    }
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
}