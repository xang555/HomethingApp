package com.xang.laothing.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.github.ajalt.reprint.core.Reprint;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.xang.laothing.Api.ApiService;
import com.xang.laothing.Api.request.LoginRequest;
import com.xang.laothing.R;
import com.xang.laothing.Service.Depending;
import com.xang.laothing.Service.SharePreferentService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FingerPrintSettingActivity extends AppCompatActivity {

    @BindView(R.id.center_title)
    TextView centerTitle;
    @BindView(R.id.checkBox_enabel_fingerprint)
    CheckBox checkBoxEnabelFingerprint;
    @BindView(R.id.confirm_passwd_fingerprint)
    EditText confirmPasswdFingerprint;
    @BindView(R.id.btn_save_setting_fingerprint)
    Button btnSaveSettingFingerprint;
    @BindView(R.id.maintoolbar)
    Toolbar maintoolbar;
    @BindView(R.id.textInputLayout_confirmpasswd)
    TextInputLayout textInputLayoutConfirmpasswd;

    private FirebaseAuth auth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_figter_print_setting);
        ButterKnife.bind(this);
        Reprint.initialize(FingerPrintSettingActivity.this);

        centerTitle.setText("Setting FingerPrint");
        maintoolbar.setNavigationIcon(R.drawable.cancel);
        maintoolbar.setTitle("");
        setSupportActionBar(maintoolbar);

        auth = FirebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InitActivity();
        setupEnabelFingerPrint(); //setup ui handle setting fingerprint

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkfingerPrintAvaiabel(); // check device hash finger print
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_save_setting_fingerprint)
    public void onSaveClicked() {

        if (SharePreferentService.getFingerPrint(FingerPrintSettingActivity.this).trim().length() > 0 ){
            SharePreferentService.saveEmail(FingerPrintSettingActivity.this,"");
            SharePreferentService.setSaveFingerPrint(FingerPrintSettingActivity.this,"");
            finish();
        }else {
            handlesaveSettingFingerPrint(confirmPasswdFingerprint.getText().toString().trim());
        }

    }

    private void InitActivity(){

        if (SharePreferentService.getFingerPrint(FingerPrintSettingActivity.this).trim().length() > 0){
            checkBoxEnabelFingerprint.setChecked(true);
            textInputLayoutConfirmpasswd.setVisibility(View.INVISIBLE);
        }

    } //init activity

    private void checkfingerPrintAvaiabel() {

        if (!Reprint.hasFingerprintRegistered()) {

            new AlertDialog.Builder(FingerPrintSettingActivity.this)
                    .setTitle("Homething - FingerPrint")
                    .setMessage("Your Device not support FingerPrint if you have fingerPrint Reader Please Enable it, And try again")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }


    } // check finger print

    private void setupEnabelFingerPrint(){

        checkBoxEnabelFingerprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (SharePreferentService.getFingerPrint(FingerPrintSettingActivity.this).trim().length() > 0 && !isChecked){
                    btnSaveSettingFingerprint.setEnabled(true);
                    btnSaveSettingFingerprint.setAlpha(1f);
                }else {
                    btnSaveSettingFingerprint.setEnabled(false);
                    btnSaveSettingFingerprint.setAlpha(0.5f);
                }

                if (isChecked){
                    confirmPasswdFingerprint.setEnabled(true);
                    textInputLayoutConfirmpasswd.setAlpha(1f);
                }else {
                    confirmPasswdFingerprint.setEnabled(false);
                    textInputLayoutConfirmpasswd.setAlpha(0.5f);
                }
            }
        });

        confirmPasswdFingerprint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() >=8){
                    btnSaveSettingFingerprint.setAlpha(1f);
                    btnSaveSettingFingerprint.setEnabled(true);
                }else {
                    btnSaveSettingFingerprint.setAlpha(0.5f);
                    btnSaveSettingFingerprint.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    } //setup setting fingerprint

    private void handlesaveSettingFingerPrint(final String passwd){

        progressDialog = Depending.showDependingProgressDialog(FingerPrintSettingActivity.this,"Confirm ...");

        auth.signInWithEmailAndPassword(auth.getCurrentUser().getEmail(),passwd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.dismiss();
                        SharePreferentService.saveEmail(FingerPrintSettingActivity.this,authResult.getUser().getEmail());
                        SharePreferentService.setSaveFingerPrint(FingerPrintSettingActivity.this,passwd);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                textInputLayoutConfirmpasswd.setError(e.getMessage());
            }
        });


    } //handle save setting


}
