package com.xang.laothing.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xang.laothing.Adapter.SmartDeviceAdapter;
import com.xang.laothing.Api.ApiService;
import com.xang.laothing.Api.reponse.DevicesResponse;
import com.xang.laothing.Database.SmartDeviceTable;
import com.xang.laothing.Database.SmartSwitchTable;
import com.xang.laothing.Model.SmartDeviceModel;
import com.xang.laothing.R;
import com.xang.laothing.Service.AlertDialogService;
import com.xang.laothing.Service.RandomService;
import com.xang.laothing.Service.SharePreferentService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RuntimePermissions
public class MainActivity extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener{


    private static final int REQURE_SCAN_QRCODE = 500;
    @BindView(R.id.maintoolbar)
    Toolbar maintoolbar;
    @BindView(R.id.container)
    CoordinatorLayout container;
    @BindView(R.id.center_title)
    TextView centerTitle;
    @BindView(R.id.smoothprogressBar)
    SmoothProgressBar smoothprogressBar;
    @BindView(R.id.smartdevice_lists)
    RecyclerView smartdeviceLists;
    @BindView(R.id.smartdevice_refresh_container)
    SwipeRefreshLayout smartdeviceRefreshContainer;
    @BindView(R.id.no_device_container)
    FrameLayout noDeviceContainer;

    private FirebaseDatabase database;
    private List<SmartDeviceModel> deviceModels = new ArrayList<SmartDeviceModel>();
    SmartDeviceAdapter deviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        maintoolbar.setNavigationIcon(R.drawable.user_profile);
        maintoolbar.setTitle("");
        centerTitle.setText(R.string.center_title);
        setSupportActionBar(maintoolbar);

        database = FirebaseDatabase.getInstance();
        smartdeviceRefreshContainer.setOnRefreshListener(this);
        smartdeviceRefreshContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        setupSmartDeviceLists(); //set up lists

        if (SharePreferentService.isFirstLoad(MainActivity.this)) {
            LoadSmartDevices();
        } else {
            FillSmartdeviceToLists();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQURE_SCAN_QRCODE && resultCode == RESULT_OK) {
            HandelScanQrcodeComplete(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    } // on activity result


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(MainActivity.this, requestCode, grantResults);
    } // on request permission result

    @OnClick(R.id.fab_scan_qrcode)
    public void onScanQrcodeClicked() {
        MainActivityPermissionsDispatcher.scanQrcodeWithCheck(MainActivity.this);
    } //scan qr coe



    @Override
    public void onRefresh() {

    LoadSmartDevices(); // load smart device again

    } // on refresh

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case android.R.id.home :

                break;
            case R.id.logout:
                handleLogout(); //logout fromm app
        }


        return super.onOptionsItemSelected(item);
    }

    private void handleLogout() {

        SmartDeviceTable.deleteAll(SmartDeviceTable.class); //delete data smart device
        SmartSwitchTable.deleteAll(SmartSwitchTable.class); // delete data smart switch
        SharePreferentService.setFirstLoad(MainActivity.this,true); // set first load
        SharePreferentService.SaveToken(MainActivity.this,""); // delete token

        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();

    } // when logout

    private void HandelScanQrcodeComplete(Intent data) {

        String qrcodeValue = data.getStringExtra(ScanQrcodeViewer.QR_CODE_VALUE);
        Toast.makeText(getApplicationContext(), qrcodeValue, Toast.LENGTH_LONG).show();

    } // handle scan QR code complete

    private void LoadSmartDevices() {


        ApiService.getRouterServiceApi().getDeviceByUser(SharePreferentService.getToken(MainActivity.this))
                .enqueue(new Callback<DevicesResponse>() {
                    @Override
                    public void onResponse(Call<DevicesResponse> call, Response<DevicesResponse> response) {

                        if (smartdeviceRefreshContainer.isRefreshing()){
                            smartdeviceRefreshContainer.setRefreshing(false);
                        }else{
                            smoothprogressBar.setVisibility(View.INVISIBLE);
                        }

                        if (response != null && response.isSuccessful()) {

                            DevicesResponse devices = response.body();
                            if (devices.err != 404) {
                                handleLoadSmartDevicesuccess(devices);
                            } else {
                                smartdeviceLists.setVisibility(View.INVISIBLE);
                                noDeviceContainer.setVisibility(View.VISIBLE);
                            }

                        } else {
                            handleLoadSmartDeviceFailure("Something Wrong!");
                        }

                    }

                    @Override
                    public void onFailure(Call<DevicesResponse> call, Throwable t) {
                        handleLoadSmartDeviceFailure(t.getMessage());
                    }
                });


    } //load smart device

    private void handleLoadSmartDeviceFailure(String message) {


        if (smartdeviceRefreshContainer.isRefreshing()){
            smartdeviceRefreshContainer.setRefreshing(false);
            Snackbar.make(container,message,Snackbar.LENGTH_LONG).show();
        }else {

            smoothprogressBar.setVisibility(View.INVISIBLE);

            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Load devices Failure")
                    .setMessage(message + " , do you want try again ?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            smoothprogressBar.setVisibility(View.VISIBLE);
                            LoadSmartDevices();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

        }


    } //load smart device failure

    private void handleLoadSmartDevicesuccess(DevicesResponse devices) {

        List<DevicesResponse.deviceLists> devicelistes = devices.devices;
        if ( devicelistes ==null || devicelistes.size() <=0 ){
            smartdeviceLists.setVisibility(View.INVISIBLE);
            noDeviceContainer.setVisibility(View.VISIBLE);
            return;
        }

        Iterator<SmartDeviceTable> list = SmartDeviceTable.findAll(SmartDeviceTable.class);

        ArrayList<SmartDeviceTable> smartDeviceTableArrayList = new ArrayList<>();
        while (list.hasNext()){
            smartDeviceTableArrayList.add(list.next());
        }

        for (int i = 0; i < devicelistes.size(); i++) {

            DevicesResponse.deviceLists device = devicelistes.get(i);
            String name = "";
            switch (device.type) {

                case 0:
                    name = "Smart device " + device.sdid;
                    break;
                case 1:
                    name = "Temp and Humi " + device.sdid;
                    break;
                case 2:
                    name = "Gas Sensor " + device.sdid;
                    break;
                case 3:
                    name = "Smart Alarm " + device.sdid;

            }

            boolean ishave = false;

            for (int j =0 ; j < smartDeviceTableArrayList.size() ; j++){
                SmartDeviceTable deviceTable = smartDeviceTableArrayList.get(j);
                if (deviceTable.sdid.equals(device.sdid)) {
                    smartDeviceTableArrayList.remove(j);
                    ishave = true;
                    break;
                }
            }

            if (!ishave) {
                SmartDeviceTable deviceTable = new SmartDeviceTable(device.sdid, device.regis, device.type, name);
                deviceTable.save();
            }

        }

        SharePreferentService.setFirstLoad(MainActivity.this, false); // set first load data to false

        FillSmartdeviceToLists(); //fill data that load to lists

    }// load smart devices successfully

    private void FillSmartdeviceToLists() {

        deviceModels.clear(); //clear data in side devicemodel lists

        Iterator<SmartDeviceTable> list = SmartDeviceTable.findAll(SmartDeviceTable.class);
        while (list.hasNext()) {

            SmartDeviceTable deviceTable = list.next();
            SmartDeviceModel device = new SmartDeviceModel();
            device.setSdid(deviceTable.sdid);
            device.setName(deviceTable.name);
            device.setIsactive(false);
            device.setType(deviceTable.type);

            deviceModels.add(device);

        }

        deviceAdapter.notifyDataSetChanged();

    } // fill data to lists

    private void setupSmartDeviceLists() {

        deviceAdapter = new SmartDeviceAdapter(MainActivity.this, deviceModels);
        deviceAdapter.setItemClickListener(new SmartDeviceAdapter.onItemClickListener() {
            @Override
            public void itemClick(int position) {

            }
        });

        deviceAdapter.setOnSettingClickListener(new SmartDeviceAdapter.onSettingClickListener() {
            @Override
            public void onClick(int position) {

            }
        });

        deviceAdapter.setOnUpdatestatusListener(new SmartDeviceAdapter.onUpdatestatusListener() {
            @Override
            public void onUpdate(final SmartDeviceAdapter.viewholder holder, int position) {

                DatabaseReference root = database.getReference(deviceModels.get(position).getSdid());

                final DatabaseReference uplink = root.child("active").child("uplink");
                final DatabaseReference ack = root.child("active").child("ack");

                final Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        final int uplinkdata = RandomService.getRandomNumber();
                        uplink.setValue(uplinkdata)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                ack.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        int acklink = dataSnapshot.getValue(Integer.class);
                                                        if (acklink == uplinkdata) {
                                                            holder.status.setColorFilter(Color.BLUE);
                                                        } else {
                                                            holder.status.setColorFilter(Color.RED);
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                            }
                                        }, 60000); // delay 1 mn


                                    }
                                });

                        handler.postDelayed(this, 300000); // delay 5 mn


                    }
                }, 2000); //delay 2 sec


            }
        });

        smartdeviceLists.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        smartdeviceLists.setHasFixedSize(true);
        smartdeviceLists.setAdapter(deviceAdapter);

    } // setup lists

    /*---------------------- require permission ---------------*/
    @NeedsPermission(Manifest.permission.CAMERA)
    void scanQrcode() {

        Intent scanview = new Intent(MainActivity.this, ScanQrcodeViewer.class);
        startActivityForResult(scanview, REQURE_SCAN_QRCODE);

    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_camera_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void showDeniedForCamera() {
        Snackbar.make(container, R.string.permission_camera_denied, Snackbar.LENGTH_LONG).show();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void showNeverAskForCamera() {
        Snackbar.make(container, R.string.permission_camera_neverask, Snackbar.LENGTH_LONG).show();
    }





}
