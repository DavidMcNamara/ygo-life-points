package com.example.ygolifepoints;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.ArrayList;

public class displayLog extends AppCompatActivity {

    private LinearLayout duelLogContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_log);

        hideUI();

        duelLogContainer = findViewById(R.id.duel_log_container);

        Bundle b = this.getIntent().getExtras();
        ArrayList<String> duelLog = b.getStringArrayList("bundle");

        for(int i = 0 ; i < duelLog.size(); i++){
            TextView tv = new TextView(this);
            tv.setText(duelLog.get(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            if(duelLog.get(i).contains("Player 1")){
                tv.setTextColor(Color.parseColor("#000000"));
                tv.setBackgroundColor(Color.parseColor("#FFFFFF"));
                params.gravity = Gravity.START;
            }
            else{
                tv.setTextColor(Color.parseColor("#000000"));
                tv.setBackgroundColor(Color.parseColor("#FF0000"));
                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                params.gravity = Gravity.END;
            }
            tv.setLayoutParams(params);
            duelLogContainer.addView(tv);
        }
    }
    private void hideUI(){
        ActionBar ab = getSupportActionBar();
        ab.hide();
    }
}