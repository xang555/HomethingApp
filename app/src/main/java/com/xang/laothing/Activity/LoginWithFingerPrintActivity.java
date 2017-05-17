package com.xang.laothing.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;
import com.github.ajalt.reprint.core.Reprint;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.xang.laothing.Api.ApiService;
import com.xang.laothing.Api.reponse.SignUpAndLoginResponse;
import com.xang.laothing.Api.request.LoginRequest;
import com.xang.laothing.R;
import com.xang.laothing.Service.SharePreferentService;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginWithFingerPrintActivity extends AppCompatActivity {

    @BindView(R.id.img_can_finger)
    ImageView imgCanFinger;
    @BindView(R.id.loading_progress_scan_finger)
    ProgressBar loadingProgressScanFinger;
    @BindView(R.id.message_label)
    TextView messageLabel;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_finger_print);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        Reprint.initialize(LoginWithFingerPrintActivity.this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        AuthenticationUsingFingerprint();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Reprint.cancelAuthentication();
    }


    private void AuthenticationUsingFingerprint(){

        Reprint.authenticate(new AuthenticationListener() {
            @Override
            public void onSuccess(int moduleTag) {
                handleScanfingerPrintScuessfully();
            }

            @Override
            public void onFailure(AuthenticationFailureReason failureReason, boolean fatal, CharSequence errorMessage, int moduleTag, int errorCode) {
                handleScanfingerPrintFailure(failureReason, errorMessage);
            }
        });

    }

    private void handleScanfingerPrintFailure(AuthenticationFailureReason failureReason, CharSequence errorMessage) {
        imgCanFinger.setImageResource(R.drawable.info_filure);
        messageLabel.setText(errorMessage);
    } //scan fingerprint failure

    private void handleScanfingerPrintScuessfully() {

        messageLabel.setText("Finger Print is Corrected");
        imgCanFinger.setVisibility(View.GONE);
        loadingProgressScanFinger.setVisibility(View.VISIBLE);
        String email = SharePreferentService.getUserEmail(LoginWithFingerPrintActivity.this);
        String passwd = SharePreferentService.getFingerPrint(LoginWithFingerPrintActivity.this);

        Login(email, passwd); // login

    } // scan fingerprint sucessfully

    private void Login(String email, String passwd) {

        auth.signInWithEmailAndPassword(email, passwd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        LoginWithApi(authResult.getUser().getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        handleLoginFailuer(e.getMessage());
                    }
                });


    } // login

    private void LoginWithApi(String uid) {

        LoginRequest loginRequest = new LoginRequest(uid);
        ApiService.getRouterServiceApi().Login(loginRequest)
                .enqueue(new Callback<SignUpAndLoginResponse>() {
                    @Override
                    public void onResponse(Call<SignUpAndLoginResponse> call, Response<SignUpAndLoginResponse> response) {

                        loadingProgressScanFinger.setVisibility(View.GONE);

                        if (response != null && response.isSuccessful()) {

                            SignUpAndLoginResponse loginresponse = response.body();
                            if (loginresponse.err != 1) {
                                handleLoginSuccess(loginresponse);
                            } else {
                                handleLoginFailuer("Login Failure Please try again");
                            }

                        } else {
                            handleLoginFailuer("Login Failure Please try again");
                        }

                    }

                    @Override
                    public void onFailure(Call<SignUpAndLoginResponse> call, Throwable t) {
                        handleLoginFailuer(t.getMessage());
                    }
                });

    } //login with api

    private void handleLoginFailuer(String message) {
        loadingProgressScanFinger.setVisibility(View.GONE);
        imgCanFinger.setImageResource(R.drawable.fingerprint_max_larg);
        imgCanFinger.setVisibility(View.VISIBLE);
        messageLabel.setText(message);

        AuthenticationUsingFingerprint(); // re authentication with fingerprint

    } // login failure 3

    private void handleLoginSuccess(final SignUpAndLoginResponse loginResponse) {

        imgCanFinger.setVisibility(View.VISIBLE);
        messageLabel.setTextColor(Color.WHITE);
        messageLabel.setText("Login Successfully");
        imgCanFinger.setImageResource(R.drawable.success);

       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               SharePreferentService.SaveToken(LoginWithFingerPrintActivity.this, loginResponse.token);
               gotoMainActivity();
           }
       },1000);

    } //login successfully


    private void gotoMainActivity() {
        Intent mainActivityIntent = new Intent(LoginWithFingerPrintActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    } // go to main activity


}
