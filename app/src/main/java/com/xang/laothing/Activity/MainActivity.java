package com.xang.laothing.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xang.laothing.Adapter.SmartDeviceAdapter;
import com.xang.laothing.Api.ApiService;
import com.xang.laothing.Api.reponse.AddSmartDeviceResponse;
import com.xang.laothing.Api.reponse.DeleteSmartDeviceResponse;
import com.xang.laothing.Api.reponse.DevicesResponse;
import com.xang.laothing.Api.reponse.EditSmartDeviceResponse;
import com.xang.laothing.Api.request.AddSmartDeviceRequest;
import com.xang.laothing.Api.request.DeleteSmartDeviceRequest;
import com.xang.laothing.Api.request.EditSmartDeviceRequest;
import com.xang.laothing.Database.FcmTable;
import com.xang.laothing.Database.SmartDeviceTable;
import com.xang.laothing.Database.SmartSwitchTable;
import com.xang.laothing.Model.SmartDeviceModel;
import com.xang.laothing.R;
import com.xang.laothing.Service.AlertDialogService;
import com.xang.laothing.Service.Depending;
import com.xang.laothing.Service.IdentifierService;
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
    private static final int SMART_SWITCH = 0;
    private static final int TEMP_AND_HUMI = 1;
    private static final int GASS_SENSOR = 2;
    private static final int SMART_ALARM = 3;
    public static final String SDID_KEY_EXTRA = "sdid";

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
    private TextView delete;
    private TextView edit;
    private TextView device_code;

    private FirebaseDatabase database;
    private FirebaseAuth auth;

    private List<SmartDeviceModel> deviceModels = new ArrayList<SmartDeviceModel>();
    SmartDeviceAdapter deviceAdapter;
    private ProgressDialog ptrDialog;
    private BottomSheetDialog bottomSheetDialog;


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
        auth = FirebaseAuth.getInstance();

        smartdeviceRefreshContainer.setOnRefreshListener(this);
        smartdeviceRefreshContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        setupSmartDeviceLists(); //set up lists

        if (SharePreferentService.isFirstLoad(MainActivity.this)) {
            smoothprogressBar.setVisibility(View.VISIBLE);
            LoadSmartDevices();
        } else {
            FillSmartdeviceToLists();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        View view = getLayoutInflater().inflate(R.layout.bottomsheet_container,null,false);
        bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setContentView(view);

        delete = (TextView)view.findViewById(R.id.menu_bottom_sheet_delete);
        edit = (TextView)view.findViewById(R.id.menu_bottom_sheet_edit);
        device_code = (TextView)view.findViewById(R.id.menu_bottom_sheet_show_device_code);

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


        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Logout")
                .setMessage("Logout from homething ?")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                SmartDeviceTable.deleteAll(SmartDeviceTable.class); //delete data smart device
                SmartSwitchTable.deleteAll(SmartSwitchTable.class); // delete data smart switch
                DeleteNotificationToken(); // delete fcm token
                SharePreferentService.setFirstLoad(MainActivity.this,true); // set first load
                SharePreferentService.SaveToken(MainActivity.this,""); // delete token
                SharePreferentService.setIsFirstLoadSmartDevice(MainActivity.this,true);
                auth.signOut();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

            }
        }).show();


    } // when logout

    private void HandelScanQrcodeComplete(Intent data) {

        String qrcodeValue = data.getStringExtra(ScanQrcodeViewer.QR_CODE_VALUE);
        handleScanSmartDevice(qrcodeValue);

    } // handle scan QR code complete

    private void handleScanSmartDevice(String qrcodeValue) {
        ShowInputShareCodeDialog(qrcodeValue);
    } // handle scan qrcode suucessfully

    private void ShowInputShareCodeDialog(final String sdid) {

        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_smart_device_dialog, null, false);
        final EditText sharecode_passwd = (EditText)view.findViewById(R.id.sharecode_password);
        CheckBox checkshowpassword= (CheckBox)view.findViewById(R.id.check_show_password);

        checkshowpassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    sharecode_passwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else{
                    sharecode_passwd.setInputType(129);
                }
            }
        });


        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Add Smart device")
                .setView(view)
                .setCancelable(false)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sharecode = sharecode_passwd.getText().toString().trim();
                        if (sharecode.length() <=0 ){
                            Snackbar.make(container,"Please, Enter Device code",Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        ptrDialog = Depending.showDependingProgressDialog(MainActivity.this,"Searching Device ...");
                        addSmartDevice(sdid,sharecode);
                        dialog.dismiss();
                    }

                }).show();

    } // show dialog input password


    private void addSmartDevice(String sdid,String sharecode){

        AddSmartDeviceRequest request = new AddSmartDeviceRequest(sdid,sharecode);
        ApiService.getRouterServiceApi().AddSmartDevice(SharePreferentService.getToken(MainActivity.this),request)
                .enqueue(new Callback<AddSmartDeviceResponse>() {
                    @Override
                    public void onResponse(Call<AddSmartDeviceResponse> call, Response<AddSmartDeviceResponse> response) {

                        ptrDialog.dismiss();

                        if (response !=null && response.isSuccessful()){

                            AddSmartDeviceResponse smartDevice = response.body();

                            if (smartDevice.err == 200){
                                handleAddSmartDeviceSuccessfully(smartDevice);
                            }else if (smartDevice.err == 404){
                                handleAddSmartDeviceFailure("No Device in Homething system , Please try again ");
                            }else if (smartDevice.err == 400){
                                handleAddSmartDeviceFailure("This smart device is already registered");
                            }else {
                                handleAddSmartDeviceFailure("No device or Device code invalid, please try again");
                            }

                        }else {
                            handleAddSmartDeviceFailure("Something Wrong , Please try again");
                        }


                    }

                    @Override
                    public void onFailure(Call<AddSmartDeviceResponse> call, Throwable t) {
                        ptrDialog.dismiss();
                        handleAddSmartDeviceFailure(t.getMessage());

                    }
                });



    } // addd smart device

    private void handleAddSmartDeviceFailure(String message) {

        AlertDialogService.ShowAlertDialog(MainActivity.this,"Searching Device Failure",message);

    } // add smart device failure

    private void handleAddSmartDeviceSuccessfully(AddSmartDeviceResponse smartDevice) {

        AddSmartDeviceResponse.Device sd = smartDevice.device;

            SmartDeviceTable deviceTable = new SmartDeviceTable(sd.sdid, sd.sharecode, sd.type, sd.name);
            deviceTable.save();

            FillSmartdeviceToLists(); //fill data smart device to lists

    } // add smart device successfully

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
                SmartDeviceTable deviceTable = new SmartDeviceTable(device.sdid, device.sharecode, device.type, device.nicname);
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
            device.setType(deviceTable.type);
            device.setDeviceCode(deviceTable.sharecode);

            deviceModels.add(device);

        }

        if (noDeviceContainer.getVisibility() == View.VISIBLE){
            noDeviceContainer.setVisibility(View.INVISIBLE);
        }

        if (smartdeviceLists.getVisibility() == View.INVISIBLE){
            smartdeviceLists.setVisibility(View.VISIBLE);
        }

        if (deviceModels.size() <=0 ){
           if (noDeviceContainer.getVisibility() == View.INVISIBLE || noDeviceContainer.getVisibility() == View.GONE){
               noDeviceContainer.setVisibility(View.VISIBLE);
           }
        }

        deviceAdapter.notifyDataSetChanged();

    } // fill data to lists

    private void setupSmartDeviceLists() {

        deviceAdapter = new SmartDeviceAdapter(MainActivity.this, deviceModels);
        deviceAdapter.setItemClickListener(new SmartDeviceAdapter.onItemClickListener() {
            @Override
            public void itemClick(int position) {

                 Intent intent;
                switch (deviceModels.get(position).getType()){
                    
                    case SMART_SWITCH:

                        intent = new Intent(MainActivity.this,SmartSwitchActivity.class);
                        intent.putExtra(SDID_KEY_EXTRA,deviceModels.get(position).getSdid());
                        startActivity(intent);

                        break;
                    
                    case TEMP_AND_HUMI :
                         intent = new Intent(MainActivity.this,TempHumiActivity.class);
                        intent.putExtra(SDID_KEY_EXTRA,deviceModels.get(position).getSdid());
                        startActivity(intent);
                        break;
                    
                    case GASS_SENSOR :
                        intent = new Intent(MainActivity.this,GassSensorActivity.class);
                        intent.putExtra(SDID_KEY_EXTRA,deviceModels.get(position).getSdid());
                        startActivity(intent);
                        break;
                    
                    case SMART_ALARM :
                        intent = new Intent(MainActivity.this,SmartAlarmActivity.class);
                        intent.putExtra(SDID_KEY_EXTRA,deviceModels.get(position).getSdid());
                        startActivity(intent);
                        break;
                    
                }
                
                
            }
        });

        deviceAdapter.setOnSettingClickListener(new SmartDeviceAdapter.onSettingClickListener() {
            @Override
            public void onClick(final int position) {

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.hide();
                        handleDeleteItemClick(position);
                    }
                });

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.hide();
                        handleEditItem(position);
                    }
                });

                device_code.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.hide();
                        handleShowDeviceCode(position);
                    }
                });

                bottomSheetDialog.show(); // show bottom sheet

            }
        });

        deviceAdapter.setOnUpdatestatusListener(new SmartDeviceAdapter.onUpdatestatusListener() {
            @Override
            public void onUpdate(final SmartDeviceAdapter.viewholder holder, int position) {

                DatabaseReference root = database.getReference(deviceModels.get(position).getSdid());
                root.keepSynced(true);

                final DatabaseReference uplink = root.child("active").child("uplink");
                uplink.keepSynced(true);
                final DatabaseReference ack = root.child("active").child("ack");
                uplink.keepSynced(true);
                final Handler uplinkhandler = new Handler();
                final Handler ackHandler = new Handler();

                uplinkhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        final int uplinkdata = RandomService.getRandomNumber();
                        uplink.setValue(uplinkdata)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        ackHandler.postDelayed(new Runnable() {
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

                        uplinkhandler.postDelayed(this, 300000); // delay 5 mn


                    }
                }, 2000); //delay 2 sec


            }
        });

        smartdeviceLists.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        smartdeviceLists.setHasFixedSize(true);
        smartdeviceLists.setAdapter(deviceAdapter);

    } // setup lists

    private void handleShowDeviceCode(int position) {
        AlertDialogService.ShowAlertDialog(MainActivity.this,deviceModels.get(position).getName(),
                getResources().getString(R.string.show_smartdevice_security_code)+"Device Code : "+deviceModels.get(position).getDeviceCode());
    } //show device code

    private void handleEditItem(final int position) {


        View dialoginputview = getLayoutInflater().inflate(R.layout.layout_change_smatrdevice_name,null,false);
        final EditText device_name = (EditText)dialoginputview.findViewById(R.id.input_smart_device_name);
        device_name.setText(deviceModels.get(position).getName());

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Change Device Name")
                .setView(dialoginputview)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String strname = device_name.getText().toString();
                        if (strname.trim().length() <=0 ){
                            Snackbar.make(container,"Device Name is empty, please try again",Snackbar.LENGTH_LONG).show();
                            return;
                        }

                        ptrDialog = Depending.showDependingProgressDialog(MainActivity.this,"Editing ...");

                        EditSmartDeviceRequest request = new EditSmartDeviceRequest(deviceModels.get(position).getSdid(),strname);
                        ApiService.getRouterServiceApi().EditSmartDevice(SharePreferentService.getToken(MainActivity.this),request)
                                .enqueue(new Callback<EditSmartDeviceResponse>() {
                                    @Override
                                    public void onResponse(Call<EditSmartDeviceResponse> call, Response<EditSmartDeviceResponse> response) {

                                        ptrDialog.dismiss();

                                        if (response!=null && response.isSuccessful()){

                                            EditSmartDeviceResponse editSmartDevice = response.body();
                                            if (editSmartDevice.err == 0){
                                                handleEditSmartDeviceSuccessfully(strname,deviceModels.get(position).getSdid());
                                            }else {
                                                handleEditSmartDeviceFailure("");
                                            }

                                        }else {
                                            handleEditSmartDeviceFailure("");
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<EditSmartDeviceResponse> call, Throwable t) {
                                        ptrDialog.dismiss();
                                        handleEditSmartDeviceFailure(t.getMessage());
                                    }
                                });

                    }
                }).show();


    } // edit item smart device

    private void handleEditSmartDeviceFailure(String msg) {

        Snackbar.make(container,"Update device name Failure, Please try again",Snackbar.LENGTH_LONG).show();

    } //handle edit smart device failure

    private void handleEditSmartDeviceSuccessfully(String strname, String sdid) {

        List<SmartDeviceTable> deviceTables = SmartDeviceTable.find(SmartDeviceTable.class,"sdid = ?",sdid);
        deviceTables.get(0).name = strname;
        deviceTables.get(0).save();

        FillSmartdeviceToLists(); // update lists

        Snackbar.make(container,"Update device name successfully",Snackbar.LENGTH_LONG).show();


    } //handle edit smart device successfully

    private void handleDeleteItemClick(final int position) {


        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete Smart device")
                .setMessage("Do you want to delete " + deviceModels.get(position).getName()+" ?")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ptrDialog = Depending.showDependingProgressDialog(MainActivity.this,"Deleting ...");
                handleDeleteItem(position);
            }
        }).show();


    } // delete item

    private void handleDeleteItem(final int position) {


        DeleteSmartDeviceRequest request = new DeleteSmartDeviceRequest(deviceModels.get(position).getSdid());
        ApiService.getRouterServiceApi().DeleteSmartDevice(SharePreferentService.getToken(MainActivity.this),request)
                .enqueue(new Callback<DeleteSmartDeviceResponse>() {
                    @Override
                    public void onResponse(Call<DeleteSmartDeviceResponse> call, Response<DeleteSmartDeviceResponse> response) {

                        ptrDialog.dismiss();

                        if (response != null && response.isSuccessful()){

                            DeleteSmartDeviceResponse deleteSmartDevice = response.body();
                            if (deleteSmartDevice.err == 0 ){
                                handleDeleteSmartDeviceSuccess(deviceModels.get(position).getSdid());
                            }else {

                                handleAddSmartDeviceFailure("Can not delete smart device, please try again");
                            }

                        }else{
                            handleAddSmartDeviceFailure("something wrong, please try again");
                        }

                    }

                    @Override
                    public void onFailure(Call<DeleteSmartDeviceResponse> call, Throwable t) {
                        ptrDialog.dismiss();
                        handleDeleteSmartDeviceFailure(t.getMessage());
                    }
                });


    } // handle delete smart device from database

    private void handleDeleteSmartDeviceFailure(String message) {
        AlertDialogService.ShowAlertDialog(MainActivity.this,"Delete Smart device",message);
    }// delete smart device failure

    private void handleDeleteSmartDeviceSuccess(String sdid) {

            int deleteitem = SmartDeviceTable.deleteAll(SmartDeviceTable.class,"sdid = ?",sdid);
            if (deleteitem > 0){
                FillSmartdeviceToLists();
                DeleteNotificationTokenById(sdid);
            }

    } //delete smart device successfully

    private void DeleteNotificationToken(){

        Iterator<FcmTable> fcms = FcmTable.findAll(FcmTable.class);
        while (fcms.hasNext()){
            FcmTable fcm = fcms.next();
           deletNotiTokenFromFirebase(fcm.sdid);
        }
        FcmTable.deleteAll(FcmTable.class); // delete token
    } // delete noti token when logout


    private void DeleteNotificationTokenById(String sdid){

        List<FcmTable> fcm = FcmTable.find(FcmTable.class,"sdid = ?",sdid);
        if (!fcm.isEmpty()){
            deletNotiTokenFromFirebase(fcm.get(0).sdid);
            fcm.get(0).delete();
        }

    } //delete noti token by id when delete device item


    private void deletNotiTokenFromFirebase(String sdid){

        database.getReference(sdid).child("sensor").child("alert").child(IdentifierService.getDeivceId(MainActivity.this)).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DLEET TOKEN","delete token suucessfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DLEET TOKEN","delete token failure");
                    }
                });

    } //delete notify token from firebase

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
