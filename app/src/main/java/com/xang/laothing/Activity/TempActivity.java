package com.xang.laothing.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.xang.laothing.R;

import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TempActivity extends AppCompatActivity {

    @BindView(R.id.center_title)
    TextView centerTitle;
    @BindView(R.id.maintoolbar)
    Toolbar maintoolbar;
    @BindView(R.id.temp_pie)
    PieView tempPie;
    @BindView(R.id.humi_pie)
    PieView humiPie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        ButterKnife.bind(this);

        centerTitle.setText(R.string.temp_and_humi_toolbar_title);
        maintoolbar.setTitle("");
        setSupportActionBar(maintoolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    @Override
    protected void onStart() {
        super.onStart();

        PieAngleAnimation animation = new PieAngleAnimation(tempPie);
        animation.setDuration(3000); //This is the duration of the animation in millis
//        humiPie.setPercentageBackgroundColor(Color.GREEN);
        tempPie.startAnimation(animation);

        PieAngleAnimation humi_animation = new PieAngleAnimation(humiPie);
        humi_animation.setDuration(3000); //This is the duration of the animation in millis
        humiPie.startAnimation(humi_animation);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



}
