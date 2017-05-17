package com.xang.laothing.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.xang.laothing.Api.ApiService;
import com.xang.laothing.Api.reponse.ProfileResponse;
import com.xang.laothing.Database.SmartDeviceTable;
import com.xang.laothing.Database.SmartSwitchTable;
import com.xang.laothing.Database.userTabel;
import com.xang.laothing.R;
import com.xang.laothing.Service.Depending;
import com.xang.laothing.Service.LogoutAppService;
import com.xang.laothing.Service.SharePreferentService;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_toolbar)
    Toolbar profileToolbar;
    @BindView(R.id.profile_name_label)
    TextView profileNameLabel;
    @BindView(R.id.profile_email)
    TextView profileEmail;
    @BindView(R.id.profile_container)
    CoordinatorLayout profileContainer;

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        profileToolbar.setTitle("");
        setSupportActionBar(profileToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FillProfileInfomation(); //load profile

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick({R.id.finger_print_button, R.id.abount_button, R.id.logout_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.finger_print_button:
                handle_setting_fingerprint();
                break;
            case R.id.abount_button:
                break;
            case R.id.logout_button:
                handleLogout();
                break;
        }
    }

    private void LoadProfile() {

        progressDialog = Depending.showDependingProgressDialog(ProfileActivity.this,"Loading Profile ...");

        ApiService.getRouterServiceApi().getprofile(SharePreferentService.getToken(ProfileActivity.this))
                .enqueue(new Callback<ProfileResponse>() {
                    @Override
                    public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {

                        progressDialog.dismiss();

                        if (response != null && response.isSuccessful()) {

                            ProfileResponse profile = response.body();
                            if (profile.err != 1) {
                                handleLoadProfileSuccessfully(profile);
                            }else {
                                handleLoadProfileFailure("Can not load profile, please try again");
                            }

                        }else {
                            handleLoadProfileFailure("Can not load profile, please try again");
                        }

                    }

                    @Override
                    public void onFailure(Call<ProfileResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        handleLoadProfileFailure(t.getMessage());
                    }

                });

    } // load profile

    private void handleLoadProfileFailure(String message) {
        Snackbar.make(profileContainer,message,Snackbar.LENGTH_LONG).show();
    }//load profile failure

    private void handleLoadProfileSuccessfully(ProfileResponse profile) {

        userTabel user = new userTabel(profile.user.uname, profile.user.fname, profile.user.lname, profile.user.email);
        user.save();

        FillProfileInfomation();

    }//load profile successfully

    private void FillProfileInfomation() {

        Iterator<userTabel> users = userTabel.findAll(userTabel.class);
        if (users.hasNext()){
            while (users.hasNext()) {
                userTabel user = users.next();

                profileToolbar.setTitle(user.uname);
                profileNameLabel.setText(user.fname + "  " + user.lname);
                profileEmail.setText(user.email);

            }
        }else {
            LoadProfile();
        }

    }

    private void handleLogout(){

        LogoutAppService logoutAppService = new LogoutAppService(database,auth,ProfileActivity.this);
        logoutAppService.Logout(new LogoutAppService.onLogoutListener() {
            @Override
            public void Logouted() {
                Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
                startActivity(intent);
                setResult(RESULT_OK);
                finish();
            }
        });

    } //log out

    private void handle_setting_fingerprint(){
        Intent intent =new Intent(ProfileActivity.this,FingerPrintSettingActivity.class);
        startActivity(intent);
    } // handle setting fingerprint

}
