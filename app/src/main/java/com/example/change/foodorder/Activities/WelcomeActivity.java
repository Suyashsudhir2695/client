package com.example.change.foodorder.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.change.foodorder.R;
import com.example.change.foodorder.ViewHolder.SlideAdapter;

import org.w3c.dom.Text;

public class WelcomeActivity extends AppCompatActivity {
    ViewPager viewPager;
    LinearLayout layout;
    SlideAdapter slideAdapter;
    TextView[] textDots;
    ImageView imgForward, imgBack;
    int currentPage;
    boolean firstTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity

            slideAdapter = new SlideAdapter(this);
            viewPager = findViewById(R.id.layoutSplashContainer);
            layout = findViewById(R.id.dotLinearLayout);
            imgBack = findViewById(R.id.imgBack);
            imgForward = findViewById(R.id.imgForward);

            viewPager.setAdapter(slideAdapter);
            addDots(0);

            viewPager.addOnPageChangeListener(listener);

            imgForward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(currentPage + 1);
                }
            });
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(currentPage - 1);
                }
            });

            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).apply();
        }
        else {
            startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
        }





       // firstTime = true;

    }


    private void addDots(int pos){
        textDots = new TextView[3];
        layout.removeAllViews();
        for (int i = 0; i<textDots.length;i++){
            textDots[i] = new TextView(this);
            textDots[i].setText(Html.fromHtml("&#8226"));
            textDots[i].setTextSize(36);
            textDots[i].setTextColor(getResources().getColor(R.color.colorPrimary));

            layout.addView(textDots[i]);

        }
        if (textDots.length > 0){
            textDots[pos].setTextColor(getResources().getColor(R.color.lightGreenBackground));
        }

    }

    ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {


        }

        @Override
        public void onPageSelected(int i) {
            addDots(i);
            currentPage = i;
            if (i == 0){
                imgBack.setEnabled(false);
                imgBack.setVisibility(View.INVISIBLE);
                imgForward.setImageDrawable(getResources().getDrawable(R.drawable.ic_right_arrow));

            }
            else if (i == (textDots.length - 1)){
                imgBack.setEnabled(true);
                imgBack.setVisibility(View.VISIBLE);
                imgForward.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick));
                if (currentPage == 2) {
                    imgForward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                            finish();
                            overridePendingTransition(R.anim.slid_in_right, R.anim.slide_out_left);


                        }
                    });
                }
            }
            else {
                imgBack.setEnabled(true);
                imgBack.setVisibility(View.VISIBLE);
                imgForward.setImageDrawable(getResources().getDrawable(R.drawable.ic_right_arrow));



            }

        }

        @Override
        public void onPageScrollStateChanged(int i) {


        }
    };
}
