package com.xang.laothing.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xang.laothing.Adapter.SmartAlarmSpinerAdapter;
import com.xang.laothing.Database.SmartDeviceTable;
import com.xang.laothing.Model.SmartAlarmModel;
import com.xang.laothing.R;
import com.xang.laothing.Service.AlertDialogService;
import com.xang.laothing.Service.Depending;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SmartAlarmActivity extends AppCompatActivity {

    @BindView(R.id.center_title)
    TextView centerTitle;
    @BindView(R.id.maintoolbar)
    Toolbar maintoolbar;
    @BindView(R.id.spinner_smart_sensor)
    Spinner spinnerSmartSensor;
    @BindView(R.id.stop_alert_button)
    CircleButton stopAlertButton;
    @BindView(R.id.smart_alarm_container)
    CoordinatorLayout smartAlarmContainer;

    private SmartAlarmSpinerAdapter adapter;
    private List<SmartAlarmModel> spinner_data;
    private FirebaseDatabase database;
    private String sdid;

    private String gass_sdid = "";
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_alarm);
        ButterKnife.bind(this);

        centerTitle.setText(R.string.smart_slarm_title);
        maintoolbar.setTitle("");
        maintoolbar.setNavigationIcon(R.drawable.cancel);
        setSupportActionBar(maintoolbar);
        database = FirebaseDatabase.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        sdid = intent.getStringExtra(MainActivity.SDID_KEY_EXTRA);

        spinner_data = getGasSensor();
        adapter = new SmartAlarmSpinerAdapter(SmartAlarmActivity.this, spinner_data);
        spinnerSmartSensor.setAdapter(adapter);

        spinnerSmartSensor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gass_sdid = spinner_data.get(position).getSdid();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        SubscribeFirebase();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_smartalarm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.save_setting) {
            handleSaveSetting();
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleSaveSetting() {

        if (gass_sdid.trim().length() <= 0) {
            AlertDialogService.ShowAlertDialog(SmartAlarmActivity.this, "Smart Alarm", "Please select sensor device");
            return;
        }

        progressDialog = Depending.showDependingProgressDialog(SmartAlarmActivity.this, "wait...");
        DatabaseReference smart_alarm_link_to_ga = database.getReference(sdid).child("link");
        final DatabaseReference gas_link_to_smart_larm = database.getReference(gass_sdid).child("sensor").child("SmartAlarmlinks");

        smart_alarm_link_to_ga.setValue(gass_sdid)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        gas_link_to_smart_larm.child(sdid).setValue(sdid)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        AlertDialogService.ShowAlertDialog(SmartAlarmActivity.this, "Smrt Alarm", e.getMessage() + ", Please try again");
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        AlertDialogService.ShowAlertDialog(SmartAlarmActivity.this, "Smrt Alarm", e.getMessage() + ", Please try again");
                    }
                });

    } //handle save setting

    private List<SmartAlarmModel> getGasSensor() {

        ArrayList<SmartAlarmModel> data = new ArrayList<SmartAlarmModel>();
        List<SmartDeviceTable> deviceTable = SmartDeviceTable.find(SmartDeviceTable.class, "type = ?", "2");

        SmartAlarmModel smartAlarm = new SmartAlarmModel();

        smartAlarm.setName("No register device");
        smartAlarm.setSdid("");
        data.add(smartAlarm);

        if (!deviceTable.isEmpty()) {

            for (int i = 0; i < deviceTable.size(); i++) {
                smartAlarm = new SmartAlarmModel();
                smartAlarm.setName(deviceTable.get(i).name + " - Gas Sensor");
                smartAlarm.setSdid(deviceTable.get(i).sdid);
                data.add(smartAlarm);
            }
        }

        return data;

    }

    private void SubscribeFirebase() {
        database.getReference(sdid).child("link").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String gas_sdid = dataSnapshot.getValue(String.class);

                if (gas_sdid != null) {

                    for (int i = 0; i < spinner_data.size(); i++) {
                        if (gas_sdid.equals(spinner_data.get(i).getSdid())) {
                            spinnerSmartSensor.setSelection(i, true);
                        }
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @OnClick(R.id.stop_alert_button)
    public void onStopButtonClicked() {

        database.getReference(sdid).child("alert").setValue(false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(smartAlarmContainer,"Stop Alarm Sucessfully",Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(smartAlarmContainer,"Stop Alarm failure , " + e.getMessage(),Snackbar.LENGTH_LONG).show();
                    }
                });

    } //stop alert button click


}
