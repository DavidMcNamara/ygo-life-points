package com.example.ygolifepoints;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.graphics.Matrix;
import android.graphics.drawable.Animatable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Image;
import android.media.SoundPool;
import android.os.Build;
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
    private ImageView flipCoin;
    private ImageView removeCoin;
    private ImageView addCoin;
    private TextView results;
    private int numberOfCoins = 1;
    public int height;
    public ConstraintLayout cl;
    public LinearLayout ll;

    private SoundPool soundPool;
    private int coinFlipSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);
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

        coinFlipSound = soundPool.load(this, R.raw.coin_flip_sound, 1);

        hideUI();

        height = getHeight();

        cl = (ConstraintLayout) findViewById(R.id.cl);
        ll = (LinearLayout) findViewById(R.id.coin_container);

        results = (TextView) findViewById(R.id.results_text);
        results.setText("Number of coins: 1");

        flipCoin = findViewById(R.id.flip_coin_button);
        flipCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipTheCoins();
            }
        });

        addCoin = findViewById(R.id.add_coin_button);
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.playSound();
                if(numberOfCoins < 3){
                    numberOfCoins++;
                    results.setText("Number of coins: "+ numberOfCoins);
                }
            }
        });

        removeCoin = findViewById(R.id.remove_coin_button);
        removeCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.playSound();
                if(numberOfCoins > 1){
                    numberOfCoins--;
                    results.setText("Number of coins: "+ numberOfCoins);
                }
            }
        });
    }

    private void flipTheCoins(){
        soundPool.play(coinFlipSound, 1, 1, 0 , 0, 1);
        if(((LinearLayout) ll).getChildCount() > 0)
            ((LinearLayout) ll).removeAllViews();
        for(int i = 0 ; i < numberOfCoins; i++) {
            ImageView coin = new ImageView(this);
            coin.setImageResource(R.drawable.coin_unknown);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0,0);
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
    private void hideUI(){
//        final View decorView = getWindow().getDecorView();
//        final int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//        decorView.setSystemUiVisibility(uiOptions);
        ActionBar ab = getSupportActionBar();
        ab.hide();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }
}