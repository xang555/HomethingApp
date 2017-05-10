package com.xang.laothing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kyleduo.switchbutton.SwitchButton;
import com.xang.laothing.Database.FcmTable;
import com.xang.laothing.R;
import com.xang.laothing.Service.IdentifierService;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GassSensorActivity extends AppCompatActivity {

    @BindView(R.id.center_title)
    TextView centerTitle;
    @BindView(R.id.maintoolbar)
    Toolbar maintoolbar;
    @BindView(R.id.plus_notification_switch)
    SwitchButton plusNotificationSwitch;
    @BindView(R.id.sma_device_count)
    TextView smaDeviceCount;
    @BindView(R.id.gass_container)
    CoordinatorLayout gassContainer;

    private FirebaseDatabase database;

    private String sdid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gass_sensor);
        ButterKnife.bind(this);

        centerTitle.setText(R.string.gass_sensor_title);
        maintoolbar.setTitle("");
        maintoolbar.setNavigationIcon(R.drawable.cancel);
        setSupportActionBar(maintoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        sdid = intent.getStringExtra(MainActivity.SDID_KEY_EXTRA);

        InitActivity(); //int activity

        plusNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handlePlusNotify(isChecked);
            }
        });


    }


    private void InitActivity() {

        intPlusNotificationSwitch();
        intSmartAlarmInfo(sdid); //init smart switch

    } //init activity

    private void handlePlusNotify(boolean isChecked) {

        List<FcmTable> fcms = FcmTable.find(FcmTable.class, "sdid = ?", sdid);
        if (!fcms.isEmpty()) {

            if (!isChecked) {

                database.getReference(sdid).child("sensor").child("alert").child(IdentifierService.getDeivceId(GassSensorActivity.this)).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("REMOVE TOKEN","remove token suucessfully");
                            }
                        });
            }

            fcms.get(0).regis = isChecked;
            fcms.get(0).save();

        } else {
            FcmTable fcmTable = new FcmTable(sdid, isChecked);
            fcmTable.save();
        }

        if (isChecked){
            SendTokenToFirebase(sdid);
        }

        Snackbar.make(gassContainer,isChecked ? "you trun on Notification":"you trun off Notification", Toast.LENGTH_SHORT).show();

    } //handle user setting notify


    private void intSmartAlarmInfo(String sdid) {

        database.getReference(sdid).child("sensor").child("SmartAlarmlinks")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, String> links = (HashMap<String, String>) dataSnapshot.getValue();
                        smaDeviceCount.setText(links.size() + " device");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    } //sett sumary smart slarm count

    private void intPlusNotificationSwitch() {
        List<FcmTable> fcms = FcmTable.find(FcmTable.class, "sdid = ?", sdid);
        if (fcms.size() > 0 ){
            plusNotificationSwitch.setChecked(fcms.get(0).regis);
        }
    } // init plus switch

    private void SendTokenToFirebase(String sdid){

        String token = FirebaseInstanceId.getInstance().getToken();

        DatabaseReference alertref = database.getReference(sdid).child("sensor").child("alert").child(IdentifierService.getDeivceId(GassSensorActivity.this));
         alertref.keepSynced(true);

                 alertref
                .setValue(token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REMOVE TOKEN","insert token suucessfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("REMOVE TOKEN","insert token failure");
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
