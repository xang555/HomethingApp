package com.xang.laothing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xang.laothing.Model.SchedulerTimeFirebaseModel;
import com.xang.laothing.R;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SmartSwitchSchedulerActivity extends AppCompatActivity implements SettingTimeFragmentDialog.settingTimeListener {

    @BindView(R.id.center_title)
    TextView centerTitle;
    @BindView(R.id.maintoolbar)
    Toolbar maintoolbar;

    @BindViews({R.id.lamp_switch_one,R.id.lamp_switch_two,R.id.lamp_switch_three,R.id.lamp_switch_four})
    ImageView[] lamp;

    @BindViews({R.id.switch_one_name,R.id.switch_two_name,R.id.switch_three_name,R.id.switch_four_name})
    TextView[] switchName;

    @BindViews({R.id.time_label_one,R.id.time_label_two,R.id.time_label_three,R.id.time_label_four})
    TextView[] timeLabel;

    @BindViews({R.id.switch_one,R.id.switch_two,R.id.switch_three,R.id.switch_four})
    Switch[] switchState;

    @BindViews({R.id.switch_one_setting,R.id.switch_two_setting,R.id.switch_three_setting,R.id.switch_four_setting})
    ImageView[] switchSetting;



    private FirebaseDatabase database;

    private DatabaseReference lamp_one,lamp_two,lamp_three,lamp_four;
    private DatabaseReference name_one,name_two,name_three,name_four;
    private DatabaseReference time_one,time_two,time_three,time_four;
    private DatabaseReference switch_one,switch_two,switch_three,switch_four;

    private String sdid;

    private DatabaseReference[] lamp_ref = {lamp_one,lamp_two,lamp_three,lamp_four};
    private DatabaseReference[] name_ref = {name_one,name_two,name_three,name_four};
    private DatabaseReference[] time_ref = {time_one,time_two,time_three,time_four};
    private DatabaseReference[] switch_ref = {switch_one,switch_two,switch_three,switch_four};

    private BottomSheetDialog bottomSheetDialog;
    private TextView settingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_switch_scheduler);
        ButterKnife.bind(this);

        centerTitle.setText(R.string.scheduler);
        maintoolbar.setTitle("");
        maintoolbar.setNavigationIcon(R.drawable.cancel);
        setSupportActionBar(maintoolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        sdid = intent.getStringExtra(MainActivity.SDID_KEY_EXTRA);

        InitDatabaseRef(sdid); // init database ref
        subscribeEven(); // subscribe for change

    }


    @Override
    protected void onStart() {
        super.onStart();

        setSwitchChangeListener(); //listener for state chnage

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_time_scheduler,null,false);
        settingTime = (TextView)view.findViewById(R.id.menu_bottom_sheet_calendar);
        bottomSheetDialog = new BottomSheetDialog(SmartSwitchSchedulerActivity.this);
        bottomSheetDialog.setContentView(view);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @OnClick({R.id.switch_one_setting, R.id.switch_two_setting, R.id.switch_three_setting, R.id.switch_four_setting})
    public void onViewClicked(View view) {

         int position = 0;

        switch (view.getId()) {
            case R.id.switch_one_setting:
               position = 0;
                break;
            case R.id.switch_two_setting:
                position = 1;
                break;
            case R.id.switch_three_setting:
                position = 2;
                break;
            case R.id.switch_four_setting:
                position = 3;
                break;
        }

        bottomSheetDialog.show();

        final int finalPosition = position;
        settingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingTimeFragmentDialog settingTimeDialog = SettingTimeFragmentDialog.newInstance(finalPosition);
                settingTimeDialog.show(getSupportFragmentManager(),"dialog");
                bottomSheetDialog.hide();
            }
        });

    }


    /*------ init database ref ------*/

    private void InitDatabaseRef(String sdid){


        for (int i =0 ;i <lamp_ref.length ; i++){
            lamp_ref[i] =  database.getReference(sdid).child("scheduler").child("L"+(i+1)).child("status");
        }


        for (int i = 0 ;i < name_ref.length ; i++){
            name_ref[i] =database.getReference(sdid).child("name").child("L"+(i+1));
        }


        for (int i =0 ;i < time_ref.length ; i++){
            time_ref[i] = database.getReference(sdid).child("scheduler").child("L"+(i+1)).child("time");
        }

        for (int i =0 ;i<switch_ref.length ; i++){
            switch_ref[i] = database.getReference(sdid).child("scheduler").child("L"+(i+1)).child("state");
        }

    } // database ref

    private void subscribeEven(){

        for (int i = 0 ; i < lamp_ref.length ; i++){
            final int finalI = i;
            lamp_ref[i].addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    int status = dataSnapshot.getValue(Integer.class);
                    if (status == 1){
                        lamp[finalI].setColorFilter(ContextCompat.getColor(SmartSwitchSchedulerActivity.this,R.color.lamp_status_on));
                    }else if (status == 0){
                        lamp[finalI].setColorFilter(ContextCompat.getColor(SmartSwitchSchedulerActivity.this,R.color.lamp_status_off));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } // lamp

        for (int i = 0 ; i < name_ref.length ; i++){
            final int finalI = i;
            name_ref[i].addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String name = dataSnapshot.getValue(String.class);
                    switchName[finalI].setText(name);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } // name


        for (int i =0; i < time_ref.length ; i++){

            final int finalI = i;
            time_ref[i].addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    SchedulerTimeFirebaseModel timeFirebaseModel = dataSnapshot.getValue(SchedulerTimeFirebaseModel.class);
                    String hour = "";

                    if (timeFirebaseModel.hour < 10 ){
                        hour = "0";
                    }

                    hour+=timeFirebaseModel.hour;

                    String minute = "";
                    if (timeFirebaseModel.minute < 10){
                        minute = "0";
                    }

                    minute+=timeFirebaseModel.minute;

                    timeLabel[finalI].setText(hour+":"+minute);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } // time


        for (int i =0 ;i < switch_ref.length ; i++){

            final int finalI = i;
            switch_ref[i].addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean state = dataSnapshot.getValue(Boolean.class);
                    switchState[finalI].setChecked(state);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } //switch



    } //subscribe even for this activity

    private void setSwitchChangeListener(){

        for (int i = 0 ;i < switchState.length ; i++){

            final int finalI = i;
            switchState[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    switch_ref[finalI].setValue(isChecked);
                    switch_ref[finalI].keepSynced(true);
                }
            });

        }


    } // state change listener


    @Override
    public void onUserSettingTimeCompletted(int hour, int minute, boolean status,int position) {

        lamp_ref[position].setValue(status ? 1 : 0);
        lamp_ref[position].keepSynced(true);
        SchedulerTimeFirebaseModel timeFirebaseModel = new SchedulerTimeFirebaseModel(hour,minute);
        time_ref[position].setValue(timeFirebaseModel);
        time_ref[position].keepSynced(true);

    }


}

