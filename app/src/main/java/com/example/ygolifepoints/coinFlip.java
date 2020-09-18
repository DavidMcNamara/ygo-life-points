package com.example.ygolifepoints;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.graphics.Matrix;
import android.graphics.drawable.Animatable;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class coinFlip extends AppCompatActivity {
    // TODO add a back button to the main activity
    private Button flipCoin;
    private Button removeCoin;
    private Button addCoin;
    // TODO remove this, if not needed
    private ImageView image;
    private TextView results;
    private int numberOfCoins = 1; // 1 is the default and minimum number of coins
    public int height;
    // TODO remove this, if not needed
    public int width;
    public ConstraintLayout cl;
    public LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        height = getHeight();

        cl = (ConstraintLayout) findViewById(R.id.cl);
        ll = (LinearLayout) findViewById(R.id.linear_layout);

        results = (TextView) findViewById(R.id.results_text);
        results.setText("Number of coins: 1");

        flipCoin = (Button) findViewById(R.id.flip_coin_button);
        flipCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipTheCoins();
            }
        });

        addCoin = (Button) findViewById(R.id.add_coin_button);
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberOfCoins < 3){
                    numberOfCoins++;
                    results.setText("Number of coins: "+ numberOfCoins);
                }
            }
        });

        removeCoin = (Button) findViewById(R.id.remove_coin_button);
        removeCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberOfCoins > 1){
                    numberOfCoins--;
                    results.setText("Number of coins: "+ numberOfCoins);
                }
            }
        });
    }

    private void flipTheCoins(){
        if(((LinearLayout) ll).getChildCount() > 0)
            ((LinearLayout) ll).removeAllViews();
        for(int i = 0 ; i < numberOfCoins; i++) {
            ImageView coin = new ImageView(this);
            coin.setImageResource(R.drawable.coin_unknown);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, height/2, 0,0);
            coin.setLayoutParams(lp);
            ll.addView(coin);
            flipAnimation(coin);
        }
    }

    private void flipAnimation(final ImageView coin){
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
                for(int i = 0 ; i < numberOfCoins; i++) {
                    if (Math.random() > 0.5)
                        coin.setImageResource(R.drawable.coin_heads);
                    else
                        coin.setImageResource(R.drawable.coin_tails);
                }
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(1000);
                fadeIn.setFillAfter(true);

                coin.startAnimation(fadeIn);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        coin.startAnimation(fadeOut);
    }

    private void findScreenInfo(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
    }
    public int getHeight(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return height;
    }
}