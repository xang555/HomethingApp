package com.xang.laothing.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xang.laothing.R;

import at.markushi.ui.CircleButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SmartSwitchControllerActivity extends AppCompatActivity {

    @BindView(R.id.center_title)
    TextView centerTitle;
    @BindView(R.id.maintoolbar)
    Toolbar maintoolbar;
    @BindView(R.id.lamp_switch_one)
    ImageView lampSwitchOne;

     boolean switch_button_is_active = false;
    @BindView(R.id.switch_one_button)
    CircleButton switchOneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_switch_controller);
        ButterKnife.bind(this);

        centerTitle.setText(R.string.smartswitchcontrolleractivity);
        maintoolbar.setTitle("");
        setSupportActionBar(maintoolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case android.R.id.home :
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.switch_one_button)
    public void onViewClicked() {

        if (!switch_button_is_active) {
            switchOneButton.setColorFilter(Color.BLUE);
            switch_button_is_active = true;
            lampSwitchOne.setColorFilter(Color.BLUE);
        }else {
            switchOneButton.setColorFilter(Color.BLACK);
            lampSwitchOne.setColorFilter(Color.BLACK);
           switch_button_is_active = false;
        }

    }

    @OnClick(R.id.switch_one_setting)
    public void onsettingClicked() {
        Toast.makeText(getApplicationContext(),"setting",Toast.LENGTH_LONG).show();
    }



}
