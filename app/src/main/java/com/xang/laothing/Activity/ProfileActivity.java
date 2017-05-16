package com.xang.laothing.Activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.xang.laothing.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_toolbar)
    Toolbar profileToolbar;
    @BindView(R.id.collap_toolbar)
    CollapsingToolbarLayout collapToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        profileToolbar.setTitle("xang");
        setSupportActionBar(profileToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
}
