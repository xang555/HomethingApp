package com.xang.laothing.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.xang.laothing.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SmartSwitchSchedulerActivity extends AppCompatActivity {

    @BindView(R.id.center_title)
    TextView centerTitle;
    @BindView(R.id.maintoolbar)
    Toolbar maintoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_switch_scheduler);
        ButterKnife.bind(this);

        centerTitle.setText(R.string.scheduler);
        maintoolbar.setTitle("");
        setSupportActionBar(maintoolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


}

