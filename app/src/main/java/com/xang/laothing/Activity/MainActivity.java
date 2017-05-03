package com.xang.laothing.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xang.laothing.Adapter.SmartDeviceAdapter;
import com.xang.laothing.Api.ApiService;
import com.xang.laothing.Api.reponse.DevicesResponse;
import com.xang.laothing.Database.SmartDeviceTable;
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
public class MainActivity extends AppCompatActivity {


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

    private FirebaseDatabase database;
    private List<SmartDeviceModel> deviceModels =new ArrayList<SmartDeviceModel>();

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


    private void HandelScanQrcodeComplete(Intent data) {

        String qrcodeValue = data.getStringExtra(ScanQrcodeViewer.QR_CODE_VALUE);
        Toast.makeText(getApplicationContext(), qrcodeValue, Toast.LENGTH_LONG).show();

    } // handle scan QR code complete

    private void LoadSmartDevices() {

        smoothprogressBar.setVisibility(View.VISIBLE);

        ApiService.getRouterServiceApi().getDeviceByUser(SharePreferentService.getToken(MainActivity.this))
                .enqueue(new Callback<DevicesResponse>() {
                    @Override
                    public void onResponse(Call<DevicesResponse> call, Response<DevicesResponse> response) {

                        if (response != null && response.isSuccessful()) {
                            smoothprogressBar.setVisibility(View.INVISIBLE);
                            DevicesResponse devices = response.body();
                            if (devices.err != 1) {
                                handleLoadSmartDevicesuccess(devices);
                            } else {
                                AlertDialogService.ShowAlertDialog(MainActivity.this, "Load Smart Device", "your don't have Smart device, Please Add Your Smart Device");
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


    } //load smart device failure

    private void handleLoadSmartDevicesuccess(DevicesResponse devices) {

        List<DevicesResponse.deviceLists> devicelistes = devices.devices;
        Iterator<SmartDeviceTable> list = SmartDeviceTable.findAll(SmartDeviceTable.class);

        for (int i = 0; i < devicelistes.size(); i++) {

            DevicesResponse.deviceLists device = devicelistes.get(i);
            String name = "";
            switch (device.type){

                case 0 :
                    name = "Smart device "+device.sdid;
                    break;
                case 1:
                    name = "Temp and Humi "+device.sdid;
                    break;
                case 2:
                    name = "Gas Sensor "+device.sdid;
                    break;
                case 3:
                    name = "Smart Alarm "+device.sdid;

            }

            boolean ishave = false;

            while (list.hasNext()){

                SmartDeviceTable deviceTable = list.next();
                if (deviceTable.sdid.equals(device.sdid)){
                    list.remove();
                    ishave = true;
                    break;
                }

            }

            if (!ishave){
                SmartDeviceTable deviceTable = new SmartDeviceTable(device.sdid, device.regis, device.type, name);
                deviceTable.save();
            }

        }

        SharePreferentService.setFirstLoad(MainActivity.this, false);
        FillSmartdeviceToLists();

    }// load smart devices successfully

    private void FillSmartdeviceToLists(){

       Iterator<SmartDeviceTable> list = SmartDeviceTable.findAll(SmartDeviceTable.class);
        while (list.hasNext()){

            SmartDeviceTable deviceTable = list.next();
            SmartDeviceModel device = new SmartDeviceModel();
            device.setSdid(deviceTable.sdid);
            device.setName(deviceTable.name);
            device.setIsactive(false);
            device.setType(deviceTable.type);

            deviceModels.add(device);

        }

        setupSmartDeviceLists();

    }

    private void setupSmartDeviceLists() {

        SmartDeviceAdapter deviceAdapter = new SmartDeviceAdapter(MainActivity.this,deviceModels);
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
                                                        if (acklink == uplinkdata){
                                                            holder.status.setColorFilter(Color.BLUE);
                                                        }else {
                                                            holder.status.setColorFilter(Color.RED);
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                            }
                                        },3600);


                                    }
                                });

                        handler.postDelayed(this,5 * 3600);


                    }
                }, 2000);


            }
        });

        smartdeviceLists.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
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
