package com.xang.laothing.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xang.laothing.Model.tempHumUpdateTime;
import com.xang.laothing.R;

import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;
import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.State;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TempHumiActivity extends BaseActivity {

    @BindView(R.id.center_title)
    TextView centerTitle;
    @BindView(R.id.maintoolbar)
    Toolbar maintoolbar;
    @BindView(R.id.temp_pie)
    PieView tempPie;
    @BindView(R.id.humi_pie)
    PieView humiPie;
    @BindView(R.id.time_update_label)
    TextView timeUpdateLabel;


    private FirebaseDatabase database;
    private DatabaseReference temp_ref, humi_ref, time_ref;
    @State protected String sdid;
    PieAngleAnimation animation;
    PieAngleAnimation humi_animation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        ButterKnife.bind(this);

        centerTitle.setText(R.string.temp_and_humi_toolbar_title);
        maintoolbar.setTitle("");
        maintoolbar.setNavigationIcon(R.drawable.cancel);
        setSupportActionBar(maintoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState ==null){
            Intent intent = getIntent();
            sdid = intent.getStringExtra(MainActivity.SDID_KEY_EXTRA);
        }

        database = FirebaseDatabase.getInstance();

        temp_ref = database.getReference(sdid).child("sensor").child("values").child("temp").child("val");
        humi_ref = database.getReference(sdid).child("sensor").child("values").child("hum").child("val");
        time_ref = database.getReference(sdid).child("sensor").child("values").child("time");

        humi_animation = new PieAngleAnimation(humiPie);
        humi_animation.setDuration(1000); //This is the duration of the animation in millis

        animation = new PieAngleAnimation(tempPie);
        animation.setDuration(1000); //This is the duration of the animation in millis

        subscribeFirebaseDataSensor(); // listen sensor change

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        tempPie.startAnimation(animation);
        humiPie.startAnimation(humi_animation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }else if (item.getItemId() == R.id.sd_setting){
            Intent intent = new Intent(TempHumiActivity.this,ConnectWifiActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    private void subscribeFirebaseDataSensor() {

        temp_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                float temp_val = dataSnapshot.getValue(Float.class);
                updatePieTemp(temp_val);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        humi_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float humi_val = dataSnapshot.getValue(Float.class);
                updatePieHumi(humi_val);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        time_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                tempHumUpdateTime time = dataSnapshot.getValue(tempHumUpdateTime.class);
                UpdateTime(time);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    } //subscribe data sensor change

    private void UpdateTime(tempHumUpdateTime time) {

        String hour = "";
        if (time.hour <10 ){
            hour ="0";
        }
        hour+=time.hour;

        String minute = "";
        if (time.minute <10 ){
            minute = "0";
        }

        minute+=time.minute;

        timeUpdateLabel.setText(hour+":"+minute);

    } //update time

    private void updatePieTemp(float temp_val) {

        if (temp_val >= 30 && temp_val <= 40){
            tempPie.setPercentageBackgroundColor(ContextCompat.getColor(TempHumiActivity.this,R.color.temp_warning));
        }else if (temp_val >= 40){
            tempPie.setPercentageBackgroundColor(ContextCompat.getColor(TempHumiActivity.this,R.color.temp_hot));
        }else if (temp_val < 30 ){
            tempPie.setPercentageBackgroundColor(ContextCompat.getColor(TempHumiActivity.this,R.color.temp_cloud));
        }

        tempPie.setPercentage((temp_val * 100)/80);
        tempPie.setInnerText(temp_val+"C");

    } // update pie temp chart

    private void updatePieHumi(float humi) {
        humiPie.setPercentageBackgroundColor(ContextCompat.getColor(TempHumiActivity.this,R.color.colorPrimary));
        humiPie.setPercentage(humi);
        humiPie.setInnerText(humi+"%");
    } //update humanity

}
