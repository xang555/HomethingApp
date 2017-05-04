package com.xang.laothing.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyleduo.switchbutton.SwitchButton;
import com.xang.laothing.Offline.SmartSwitchOfflineMode;
import com.xang.laothing.Offline.request.SmartSwitchOfflineRequest;
import com.xang.laothing.Offline.response.SmatrtSwitchOfflineResponse;
import com.xang.laothing.R;

import at.markushi.ui.CircleButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmartSwitchControllerActivity extends AppCompatActivity {

    @BindView(R.id.center_title)
    TextView centerTitle;
    @BindView(R.id.maintoolbar)
    Toolbar maintoolbar;
    @BindView(R.id.lamp_switch_one)
    ImageView lampSwitchOne;

    @BindView(R.id.switch_one_button)
    CircleButton switchOneButton;
    @BindView(R.id.switch_controller_mode)
    SwitchButton switchControllerMode;
    @BindView(R.id.switch_one_setting)
    ImageView switchOneSetting;
    @BindView(R.id.switch_one_name)
    TextView switchOneName;
    @BindView(R.id.switch_status)
    TextView switchoneStatus;
    @BindView(R.id.switch_two_setting)
    ImageView switchTwoSetting;
    @BindView(R.id.lamp_switch_two)
    ImageView lampSwitchTwo;
    @BindView(R.id.switch_two_name)
    TextView switchTwoName;
    @BindView(R.id.switch_two_status)
    TextView switchTwoStatus;
    @BindView(R.id.switch_two_button)
    CircleButton switchTwoButton;
    @BindView(R.id.switch_three_setting)
    ImageView switchThreeSetting;
    @BindView(R.id.lamp_switch_three)
    ImageView lampSwitchThree;
    @BindView(R.id.switch_three_name)
    TextView switchThreeName;
    @BindView(R.id.switch_three_status)
    TextView switchThreeStatus;
    @BindView(R.id.switch_three_button)
    CircleButton switchThreeButton;
    @BindView(R.id.switch_four_setting)
    ImageView switchFourSetting;
    @BindView(R.id.lamp_switch_four)
    ImageView lampSwitchFour;
    @BindView(R.id.switch_four_name)
    TextView switchFourName;
    @BindView(R.id.switch_four_button)
    CircleButton switchFourButton;
    @BindView(R.id.switch_four_status)
    TextView switchFourStatus;
    @BindView(R.id.switch_controller_container)
    ConstraintLayout switchControllerContainer;


    private FirebaseDatabase database;
    private ValueEventListener switchOneValueEventListener;
    private ValueEventListener switchTwoValueEventListener;
    private ValueEventListener switchThreeValueEventListener;
    private ValueEventListener switchFourValueEventListener;

    private ValueEventListener switchOneButtonValueEventListener;
    private ValueEventListener switchTwoButtonValueEventListener;
    private ValueEventListener switchThreeButtonValueEventListener;
    private ValueEventListener switchFourButtonValueEventListener;

    private boolean settingmode = true; // true is online / false offline
    private String sdid;

    boolean switchOne_button_is_active = false;
    boolean switchTwo_button_is_active = false;
    boolean switchThree_button_is_active = false;
    boolean switchFour_button_is_active = false;

    DatabaseReference switchOneItem;
    DatabaseReference switchTwoItem;
    DatabaseReference switchThreeItem;
    DatabaseReference switchFourItem;

    DatabaseReference switchOne;
    DatabaseReference switchTwo;
    DatabaseReference switchThree;
    DatabaseReference switchFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_switch_controller);
        ButterKnife.bind(this);

        centerTitle.setText(R.string.smartswitchcontrolleractivity);
        maintoolbar.setTitle("");
        setSupportActionBar(maintoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        sdid = intent.getStringExtra(MainActivity.SDID_KEY_EXTRA);

        database = FirebaseDatabase.getInstance();

        switchOneItem = database.getReference(sdid).child("status").child("L1").child("ack");
        switchTwoItem = database.getReference(sdid).child("status").child("L2").child("ack");
        switchThreeItem = database.getReference(sdid).child("status").child("L3").child("ack");
        switchFourItem = database.getReference(sdid).child("status").child("L4").child("ack");

        switchOne = database.getReference(sdid).child("status").child("L1").child("status");
        switchTwo = database.getReference(sdid).child("status").child("L2").child("status");
        switchThree = database.getReference(sdid).child("status").child("L3").child("status");
        switchFour = database.getReference(sdid).child("status").child("L4").child("status");

        SubscribeEven(); // online event


    }

    @Override
    protected void onStart() {
        super.onStart();

        switchControllerMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingmode = isChecked;
                if (settingmode){
                    SubscribeEven(); // online event
                }else {
                    unSubscribeEven(); // offline event
                }
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unSubscribeEven(); // offline event

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


    private void unSubscribeEven(){

        if (switchOneValueEventListener !=null){
            switchOneItem.removeEventListener(switchOneValueEventListener);
        }

        if (switchTwoValueEventListener !=null){
            switchTwoItem.removeEventListener(switchTwoValueEventListener);
        }

        if (switchThreeValueEventListener !=null){
            switchThreeItem.removeEventListener(switchThreeValueEventListener);
        }

        if (switchFourValueEventListener !=null){
            switchFourItem.removeEventListener(switchFourValueEventListener);
        }

        if (switchOneButtonValueEventListener !=null){
            switchOne.removeEventListener(switchOneButtonValueEventListener);
        }

        if (switchTwoButtonValueEventListener !=null){
            switchTwo.removeEventListener(switchTwoButtonValueEventListener);
        }

        if (switchThreeButtonValueEventListener !=null){
            switchThree.removeEventListener(switchThreeButtonValueEventListener);
        }

        if (switchFourButtonValueEventListener !=null){
            switchFour.removeEventListener(switchFourButtonValueEventListener);
        }


    } //unSubscribe

    private void SubscribeEven(){
        SubscribeAckStateChange();
        SubscribeStatusChange();
    } //SubscribeEven


    private void SubscribeAckStateChange() {



        switchOneValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int ackstate = dataSnapshot.getValue(Integer.class);
                if (ackstate == 1) {
                    lampSwitchOne.setColorFilter(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.lamp_status_on));
                    switchoneStatus.setTextColor(Color.BLUE);
                    switchoneStatus.setText("ON");
                } else if (ackstate == 0) {
                    lampSwitchOne.setColorFilter(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.lamp_status_off));
                    switchoneStatus.setTextColor(Color.RED);
                    switchoneStatus.setText("OFF");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        switchTwoValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int ackstate = dataSnapshot.getValue(Integer.class);
                if (ackstate == 1) {
                    lampSwitchTwo.setColorFilter(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.lamp_status_on));
                    switchTwoStatus.setTextColor(Color.BLUE);
                    switchTwoStatus.setText("ON");
                } else if (ackstate == 0) {
                    lampSwitchTwo.setColorFilter(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.lamp_status_off));
                    switchTwoStatus.setTextColor(Color.RED);
                    switchTwoStatus.setText("OFF");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        switchThreeValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int ackstate = dataSnapshot.getValue(Integer.class);
                if (ackstate == 1) {
                    lampSwitchThree.setColorFilter(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.lamp_status_on));
                    switchThreeStatus.setTextColor(Color.BLUE);
                    switchThreeStatus.setText("ON");
                } else if (ackstate == 0) {
                    lampSwitchThree.setColorFilter(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.lamp_status_off));
                    switchThreeStatus.setTextColor(Color.RED);
                    switchThreeStatus.setText("OFF");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        switchFourValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int ackstate = dataSnapshot.getValue(Integer.class);
                if (ackstate == 1) {
                    lampSwitchFour.setColorFilter(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.lamp_status_on));
                    switchFourStatus.setTextColor(Color.BLUE);
                    switchFourStatus.setText("ON");
                } else if (ackstate == 0) {
                    lampSwitchFour.setColorFilter(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.lamp_status_off));
                    switchFourStatus.setTextColor(Color.RED);
                    switchFourStatus.setText("OFF");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        switchOneItem.addValueEventListener(switchOneValueEventListener);
        switchTwoItem.addValueEventListener(switchTwoValueEventListener);
        switchThreeItem.addValueEventListener(switchThreeValueEventListener);
        switchFourItem.addValueEventListener(switchFourValueEventListener);

    } // listent for ack chang


    private void SubscribeStatusChange() {

        switchOneButtonValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int status = dataSnapshot.getValue(Integer.class);
                if (status == 1) {
                    switchOneButton.setColor(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.buttonActive));
                    switchOne_button_is_active = true;
                } else if (status == 0) {
                    switchOneButton.setColor(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.buttonnonActive));
                    switchOne_button_is_active = false;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        switchTwoButtonValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int status = dataSnapshot.getValue(Integer.class);
                if (status == 1) {
                    switchTwoButton.setColor(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.buttonActive));
                    switchTwo_button_is_active = true;
                } else if (status == 0) {
                    switchTwoButton.setColor(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.buttonnonActive));
                    switchTwo_button_is_active = false;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        switchThreeButtonValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int status = dataSnapshot.getValue(Integer.class);
                if (status == 1) {
                    switchThreeButton.setColor(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.buttonActive));
                    switchThree_button_is_active = true;
                } else if (status == 0) {
                    switchThreeButton.setColor(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.buttonnonActive));
                    switchThree_button_is_active = false;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        switchFourButtonValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int status = dataSnapshot.getValue(Integer.class);
                if (status == 1) {
                    switchFourButton.setColor(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.buttonActive));
                    switchFour_button_is_active = true;
                } else if (status == 0) {
                    switchFourButton.setColor(ContextCompat.getColor(SmartSwitchControllerActivity.this, R.color.buttonnonActive));
                    switchFour_button_is_active = false;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        switchOne.addValueEventListener(switchOneButtonValueEventListener);
        switchTwo.addValueEventListener(switchTwoButtonValueEventListener);
        switchThree.addValueEventListener(switchThreeButtonValueEventListener);
        switchFour.addValueEventListener(switchFourButtonValueEventListener);

    } // listen for status change

    @OnClick({R.id.switch_one_setting, R.id.switch_one_button, R.id.switch_two_setting, R.id.switch_two_button, R.id.switch_three_setting, R.id.switch_three_button, R.id.switch_four_setting, R.id.switch_four_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.switch_one_setting:
                break;
            case R.id.switch_one_button:
                handleswitchOneButtonClick();
                break;
            case R.id.switch_two_setting:
                break;
            case R.id.switch_two_button:
                handleswitchTwoButtonClick();
                break;
            case R.id.switch_three_setting:
                break;
            case R.id.switch_three_button:
                handleswitchThreeButtonClick();
                break;
            case R.id.switch_four_setting:
                break;
            case R.id.switch_four_button:
                handleswitchFourButtonClick();
                break;
        }
    }

    private void handleswitchFourButtonClick() {

        if (settingmode) { //online
            handleswitchFourOnline();
        } else {
            switchFour_button_is_active = handleswitchOffline("L4",!switchFour_button_is_active ? "1":"0",switchFourButton);
        }

    } //handle switch four click

    private void handleswitchThreeButtonClick() {
        if (settingmode) { //online
            handleswitchThreeOnline();
        } else {
            switchThree_button_is_active = handleswitchOffline("L3",!switchThree_button_is_active ? "1":"0",switchThreeButton);
        }

    } // handle switch three click

    private void handleswitchTwoButtonClick() {

        if (settingmode) { //online
            handleswitchTwoOnline();
        } else {
            switchTwo_button_is_active = handleswitchOffline("L2",!switchTwo_button_is_active ? "1":"0",switchTwoButton);
        }

    } // handle switch two click


    private void handleswitchOneButtonClick() {

        if (settingmode) { //online
            handleswitchOneOnline();
        } else {
            switchOne_button_is_active = handleswitchOffline("L1",!switchOne_button_is_active ? "1":"0",switchOneButton);
        }

    } //handle switch one button click

    private boolean handleswitchOffline(String swn , String cmd, final CircleButton button) {

        final boolean[] state = {false};

        SmartSwitchOfflineRequest request = new SmartSwitchOfflineRequest(swn,cmd);
        SmartSwitchOfflineMode.offlineService().SendCommand(request)
                .enqueue(new Callback<SmatrtSwitchOfflineResponse>() {
                    @Override
                    public void onResponse(Call<SmatrtSwitchOfflineResponse> call, Response<SmatrtSwitchOfflineResponse> response) {

                        if (response !=null && response.isSuccessful()){
                            SmatrtSwitchOfflineResponse switchOffline = response.body();
                            if (switchOffline.state.equals("1")){
                                button.setColorFilter(ContextCompat.getColor(SmartSwitchControllerActivity.this,R.color.buttonActive));
                                state[0] = true;
                            }else{
                                button.setColorFilter(ContextCompat.getColor(SmartSwitchControllerActivity.this,R.color.buttonnonActive));
                                state[0] = false;
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<SmatrtSwitchOfflineResponse> call, Throwable t) {
                        Snackbar.make(switchControllerContainer,t.getMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                });

        return state[0];

    } // switch  offline mode

    private void handleswitchOneOnline() {

        DatabaseReference switchOne = database.getReference(sdid).child("status").child("L1").child("status");
        switchOne.setValue(!switchOne_button_is_active ? 1 : 0)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(switchControllerContainer,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                });

    } //switch one online mode

    private void handleswitchTwoOnline() {
        DatabaseReference switchOne = database.getReference(sdid).child("status").child("L2").child("status");
        switchOne.setValue(!switchTwo_button_is_active ? 1 : 0)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(switchControllerContainer,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                });
    } // switch two offline mode


    private void handleswitchThreeOnline() {
        DatabaseReference switchOne = database.getReference(sdid).child("status").child("L3").child("status");
        switchOne.setValue(!switchThree_button_is_active ? 1 : 0)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(switchControllerContainer,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                });
    } // switch three offline mode

    private void handleswitchFourOnline() {
        DatabaseReference switchOne = database.getReference(sdid).child("status").child("L4").child("status");
        switchOne.setValue(!switchFour_button_is_active ? 1 : 0)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(switchControllerContainer,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                });
    } // switch four offline mode


}
