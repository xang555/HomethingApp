package com.xang.laothing.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.xang.laothing.Database.SmartSwitchTable;
import com.xang.laothing.R;
import com.xang.laothing.Service.SharePreferentService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.State;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SmartSwitchActivity extends BaseActivity {

    @BindView(R.id.maintoolbar)
    Toolbar maintoolbar;
    @BindView(R.id.center_title)
    TextView centerTitle;

    @State protected String sdid ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_switch);
        ButterKnife.bind(this);

        centerTitle.setText(R.string.smartswitchactivity);
        maintoolbar.setTitle("");
        maintoolbar.setNavigationIcon(R.drawable.cancel);
        setSupportActionBar(maintoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState ==null){
            Intent intent = getIntent();
            sdid = intent.getStringExtra(MainActivity.SDID_KEY_EXTRA);
        }

        InitSmartSwitchDatabase(); //init databse

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.buttonController)
    public void onbuttonControllerClicked() {
        Intent intent = new Intent(SmartSwitchActivity.this, SmartSwitchControllerActivity.class);
        intent.putExtra(MainActivity.SDID_KEY_EXTRA,sdid);
        startActivity(intent);
    }


    @OnClick(R.id.scheduler_button)
    public void onSchedulerClicked() {
        Intent intent = new Intent(SmartSwitchActivity.this, SmartSwitchSchedulerActivity.class);
        intent.putExtra(MainActivity.SDID_KEY_EXTRA,sdid);
        startActivity(intent);
    }

    @OnClick(R.id.swartswitch_setting)
    public void onsettingClicked() {
        Intent intent = new Intent(SmartSwitchActivity.this, ConnectWifiActivity.class);
        startActivity(intent);
    }


    private void InitSmartSwitchDatabase(){

        if (SharePreferentService.isFirstUseSmartSwitch(SmartSwitchActivity.this)){
            SmartSwitchTable switchTable = new SmartSwitchTable(sdid,"Switch One","Switch Two","Switch Three","Switch Four");
            switchTable.save();

            SharePreferentService.setIsFirstLoadSmartDevice(SmartSwitchActivity.this,false);

        }
    }



}
