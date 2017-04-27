package com.xang.laothing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.xang.laothing.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SmartSwitchActivity extends AppCompatActivity {

    @BindView(R.id.maintoolbar)
    Toolbar maintoolbar;
    @BindView(R.id.center_title)
    TextView centerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_switch);
        ButterKnife.bind(this);

        centerTitle.setText(R.string.smartswitchactivity);
        maintoolbar.setTitle("");
        setSupportActionBar(maintoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @OnClick(R.id.buttonController)
    public void onbuttonControllerClicked() {
        Intent intent = new Intent(SmartSwitchActivity.this, SmartSwitchControllerActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.scheduler_button)
    public void onSchedulerClicked() {

        Intent intent = new Intent(SmartSwitchActivity.this, SmartSwitchSchedulerActivity.class);
        startActivity(intent);

    }
}
