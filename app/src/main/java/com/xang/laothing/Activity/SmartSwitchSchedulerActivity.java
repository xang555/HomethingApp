package com.xang.laothing.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import icepick.State;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SmartSwitchSchedulerActivity extends BaseActivity implements SettingTimeFragmentDialog.settingTimeListener {

    @BindView(R.id.center_title)
    TextView centerTitle;
    @BindView(R.id.maintoolbar)
    Toolbar maintoolbar;

    @BindViews({R.id.lamp_switch_one, R.id.lamp_switch_two, R.id.lamp_switch_three, R.id.lamp_switch_four})
    ImageView[] lamp;

    @BindViews({R.id.switch_one_name, R.id.switch_two_name, R.id.switch_three_name, R.id.switch_four_name})
    TextView[] switchName;

    @BindViews({R.id.time_label_one, R.id.time_label_two, R.id.time_label_three, R.id.time_label_four})
    TextView[] timeLabel;

    @BindViews({R.id.switch_one, R.id.switch_two, R.id.switch_three, R.id.switch_four})
    Switch[] switchState;

    @BindViews({R.id.switch_one_setting, R.id.switch_two_setting, R.id.switch_three_setting, R.id.switch_four_setting})
    ImageView[] switchSetting;
    @BindView(R.id.shceduler_container)
    ConstraintLayout shcedulerContainer;


    private FirebaseDatabase database;

    private DatabaseReference lamp_one, lamp_two, lamp_three, lamp_four;
    private DatabaseReference name_one, name_two, name_three, name_four;
    private DatabaseReference time_one, time_two, time_three, time_four;
    private DatabaseReference switch_one, switch_two, switch_three, switch_four;

    @State protected String sdid;

    private DatabaseReference[] lamp_ref = {lamp_one, lamp_two, lamp_three, lamp_four};
    private DatabaseReference[] name_ref = {name_one, name_two, name_three, name_four};
    private DatabaseReference[] time_ref = {time_one, time_two, time_three, time_four};
    private DatabaseReference[] switch_ref = {switch_one, switch_two, switch_three, switch_four};

    private BottomSheetDialog bottomSheetDialog;
    private TextView settingTime;
    private TextView editName;

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

        if (savedInstanceState == null){
            Intent intent = getIntent();
            sdid = intent.getStringExtra(MainActivity.SDID_KEY_EXTRA);
        }

        InitDatabaseRef(sdid); // init database ref
        subscribeEven(); // subscribe for change

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();

        setSwitchChangeListener(); //listener for state chnage

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_time_scheduler, null, false);
        settingTime = (TextView) view.findViewById(R.id.menu_bottom_sheet_calendar);
        editName = (TextView) view.findViewById(R.id.menu_bottom_sheet_edit_name);
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
    public void onsSwitchSettingClicked(View view) {

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
                settingTimeDialog.show(getSupportFragmentManager(), "dialog");
                bottomSheetDialog.hide();
            }
        });

        handleEditName(name_ref[position]); // handle change name

    } // setting click


    /*------ init database ref ------*/

    private void InitDatabaseRef(String sdid) {


        for (int i = 0; i < lamp_ref.length; i++) {
            lamp_ref[i] = database.getReference(sdid).child("scheduler").child("L" + (i + 1)).child("status");
        }


        for (int i = 0; i < name_ref.length; i++) {
            name_ref[i] = database.getReference(sdid).child("name").child("L" + (i + 1));
        }


        for (int i = 0; i < time_ref.length; i++) {
            time_ref[i] = database.getReference(sdid).child("scheduler").child("L" + (i + 1)).child("time");
        }

        for (int i = 0; i < switch_ref.length; i++) {
            switch_ref[i] = database.getReference(sdid).child("scheduler").child("L" + (i + 1)).child("state");
        }

    } // database ref

    private void subscribeEven() {

        for (int i = 0; i < lamp_ref.length; i++) {
            final int finalI = i;
            lamp_ref[i].addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    int status = dataSnapshot.getValue(Integer.class);
                    if (status == 1) {
                        lamp[finalI].setColorFilter(ContextCompat.getColor(SmartSwitchSchedulerActivity.this, R.color.lamp_status_on));
                    } else if (status == 0) {
                        lamp[finalI].setColorFilter(ContextCompat.getColor(SmartSwitchSchedulerActivity.this, R.color.lamp_status_off));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } // lamp

        for (int i = 0; i < name_ref.length; i++) {
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


        for (int i = 0; i < time_ref.length; i++) {

            final int finalI = i;
            time_ref[i].addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    SchedulerTimeFirebaseModel timeFirebaseModel = dataSnapshot.getValue(SchedulerTimeFirebaseModel.class);
                    String hour = "";

                    if (timeFirebaseModel.hour < 10) {
                        hour = "0";
                    }

                    hour += timeFirebaseModel.hour;

                    String minute = "";
                    if (timeFirebaseModel.minute < 10) {
                        minute = "0";
                    }

                    minute += timeFirebaseModel.minute;

                    timeLabel[finalI].setText(hour + ":" + minute);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } // time


        for (int i = 0; i < switch_ref.length; i++) {

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

    private void setSwitchChangeListener() {

        for (int i = 0; i < switchState.length; i++) {

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

    private void handleEditName(final DatabaseReference ref) {

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.hide();

                View dialoginputview = getLayoutInflater().inflate(R.layout.layout_change_smatrdevice_name, null, false);
                final EditText switch_name = (EditText) dialoginputview.findViewById(R.id.input_smart_device_name);
                new AlertDialog.Builder(SmartSwitchSchedulerActivity.this)
                        .setTitle("Change Switch Name")
                        .setView(dialoginputview)
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("RENAME", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (switch_name.getText().toString().trim().length() <= 0) {
                                    Snackbar.make(shcedulerContainer, "You Switch Name is Empty", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }

                                ref.setValue(switch_name.getText().toString().trim());
                                ref.keepSynced(true);

                            }

                        }).show();

            }

        });

    } //handle editname click

    @Override
    public void onUserSettingTimeCompletted(int hour, int minute, boolean status, int position) {

        lamp_ref[position].setValue(status ? 1 : 0);
        lamp_ref[position].keepSynced(true);
        SchedulerTimeFirebaseModel timeFirebaseModel = new SchedulerTimeFirebaseModel(hour, minute);
        time_ref[position].setValue(timeFirebaseModel);
        time_ref[position].keepSynced(true);

    }


}

