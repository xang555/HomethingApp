package com.xang.laothing.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.xang.laothing.Api.ApiService;
import com.xang.laothing.Api.reponse.SignUpAndLoginResponse;
import com.xang.laothing.Api.request.LoginRequest;
import com.xang.laothing.FactorySnipView.AlertDialogService;
import com.xang.laothing.FunctionService.SharePreferentService;
import com.xang.laothing.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.emailinputbox)
    EditText emailinputbox;
    @BindView(R.id.passwdinputbox)
    EditText passwdinputbox;
    @BindView(R.id.scroll_login)
    ScrollView scrollLogin;


    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        scrollLogin.setHorizontalScrollBarEnabled(false);
        scrollLogin.setVerticalScrollBarEnabled(false);

        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {



            }
        };


    }


    @Override
    protected void onStart() {
        super.onStart();
        if (authStateListener!=null){
            auth.addAuthStateListener(authStateListener);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }



    @OnClick({R.id.login_button, R.id.signup, R.id.forgot_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                handleUserLogin();
                break;
            case R.id.signup:
                goToSignUpActivity(); // let go to sign up
                break;
            case R.id.forgot_password:
                break;
        }
    }

    private void handleUserLogin() {

        String email = emailinputbox.getText().toString();
        String passwd = passwdinputbox.getText().toString();

        if (!checkInputValidation(email,passwd)){
            return;
        }

        Login(email,passwd); //login

    }


    private boolean checkInputValidation(String email, String passwd){

        boolean isvalid = true;
        String msgerror= "Can't not empty";

        if (email.trim().length() <=0 ){
            emailinputbox.setError(msgerror);
            isvalid = false;
        }else if (passwd.trim().length() <=0 ){
            passwdinputbox.setError(msgerror);
            isvalid = false;
        }else if (passwd.trim().length() < 8 ){
            passwdinputbox.setError("Password must have 8 character");
            isvalid = false;
        }

        return isvalid;

    }

    private void Login(String email, String passwd){

        auth.signInWithEmailAndPassword(email,passwd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                    LoginWitthApi(authResult.getUser().getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        handleLoginFailuer(e.getMessage());
                    }
                });


    } // login

    private void LoginWitthApi(String uid){

        LoginRequest loginRequest = new LoginRequest(uid);
        ApiService.getRouterServiceApi().Login(loginRequest)
                .enqueue(new Callback<SignUpAndLoginResponse>() {
                    @Override
                    public void onResponse(Call<SignUpAndLoginResponse> call, Response<SignUpAndLoginResponse> response) {

                        progressDialog.dismiss();

                        if (response!=null && response.isSuccessful()){

                            SignUpAndLoginResponse loginresponse = response.body();
                            if (loginresponse.err != 1){
                                handleLginSuccess(loginresponse);
                            }else {
                                handleLoginFailuer("Login Failure Please try again");
                            }

                        }else{
                            handleLoginFailuer("Login Failure Please try again");
                        }

                    }

                    @Override
                    public void onFailure(Call<SignUpAndLoginResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        handleLoginFailuer(t.getMessage());
                    }
                });

    } //login with api

    private void handleLoginFailuer(String message) {
        AlertDialogService.ShowAlertDialog(LoginActivity.this,"Login Failure",message);
    } // login failure

    private void handleLginSuccess(SignUpAndLoginResponse loginResponse) {

        SharePreferentService.SaveToken(LoginActivity.this,loginResponse.token);
        gotoMainActivity();

    } //login successfully


    private void gotoMainActivity(){
        Intent mainActivityIntent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    } // go to main activity

    private void goToSignUpActivity() {
        Intent signupintent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(signupintent);
    } //go to sig up activity


}
