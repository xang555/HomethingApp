package com.xang.laothing;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {


    private static final int REQURE_SCAN_QRCODE = 500;
    @BindView(R.id.maintoolbar)
    Toolbar maintoolbar;
    @BindView(R.id.container)
    CoordinatorLayout container;
    @BindView(R.id.center_title)
    TextView centerTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        maintoolbar.setNavigationIcon(R.drawable.user_profile);
        maintoolbar.setTitle("");
        centerTitle.setText(R.string.center_title);
        setSupportActionBar(maintoolbar);

    }


    @Override
    protected void onResume() {
        super.onResume();
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
