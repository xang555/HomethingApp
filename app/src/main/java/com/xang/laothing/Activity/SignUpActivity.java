package com.xang.laothing.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ScrollView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.xang.laothing.Api.ApiService;
import com.xang.laothing.Api.reponse.SignUpAndLoginResponse;
import com.xang.laothing.Api.request.RequestSignup;
import com.xang.laothing.Service.AlertDialogService;
import com.xang.laothing.Service.Depending;
import com.xang.laothing.Service.SharePreferentService;
import com.xang.laothing.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.scroll_signup)
    ScrollView scrollSignup;
    @BindView(R.id.uname)
    EditText unameinputbox;
    @BindView(R.id.password)
    EditText passwordinputbox;
    @BindView(R.id.fname)
    EditText fnameinputbox;
    @BindView(R.id.lname)
    EditText lnameinputbox;
    @BindView(R.id.email)
    EditText emailinputbox;

    private FirebaseAuth auth;
    private Call<SignUpAndLoginResponse> call;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        scrollSignup.setVerticalScrollBarEnabled(false);
        scrollSignup.setHorizontalScrollBarEnabled(false);

        auth = FirebaseAuth.getInstance();

    }


    @OnClick(R.id.signup_button)
    public void onSignupClicked() {

        String uname = unameinputbox.getText().toString();
        String fname = fnameinputbox.getText().toString();
        String lname = lnameinputbox.getText().toString();
        String email = emailinputbox.getText().toString();
        String password = passwordinputbox.getText().toString();

     if (!CheckInputValidation(uname,fname,lname,email,password)){
         return;
     }

    progressDialog = Depending.showDependingProgressDialog(SignUpActivity.this,"Sign up ..."); //show progress

    SignUpUserWithFirebase(uname,fname,lname,email,password); //sign up with firebase


    } //sign up



    private boolean CheckInputValidation(final String uname, final String fname, final String lname , final String email, String password){

        boolean isvalid = true;
        String messageError = "can't Not Empty";

        if (uname.trim().length() <=0){
            unameinputbox.setError(messageError);
            isvalid = false;

        }else if (fname.trim().length() <=0 ){
            fnameinputbox.setError(messageError);
            isvalid = false;

        }else if (lname.trim().length() <=0 ){
            lnameinputbox.setError(messageError);
            isvalid = false;
        }else if (email.trim().length() <=0 ){
            emailinputbox.setError(messageError);
            isvalid = false;
        }else if (password.trim().length() <=0 ){
            passwordinputbox.setError(messageError);
            isvalid = false;
        }else if (password.trim().length() < 8){
            passwordinputbox.setError("Password must have 8 character");
            isvalid = false;
        }

       return isvalid;

    } // validation input

    private void SignUpUserWithFirebase(final String uname, final String fname, final String lname , final String email, String password){


      auth.createUserWithEmailAndPassword(email,password)
              .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                  @Override
                  public void onSuccess(AuthResult authResult) {
                      SignUpUserWithApi(uname,authResult.getUser().getUid(),fname,lname,email);
                  }
              })
              .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      progressDialog.dismiss();
                        handleSignUpFailure(e.getMessage());
                  }
              });

    } //sign up with firebase


    private void SignUpUserWithApi(String uname , String uid, String fname, String lname , String email){

        RequestSignup requestSignup = new RequestSignup(uname,lname,fname,email,uid);

        call =  ApiService.getRouterServiceApi().Sigup(requestSignup);
        call.enqueue(new Callback<SignUpAndLoginResponse>() {
            @Override
            public void onResponse(Call<SignUpAndLoginResponse> call, Response<SignUpAndLoginResponse> response) {

                progressDialog.dismiss(); //dismiss progress dialog

                if (response!=null && response.isSuccessful()){

                    SignUpAndLoginResponse signUp = response.body();
                    if (signUp.err != 1){

                        handleSignUpSuccessFully(signUp);

                    }else {
                        handleSignUpFailure("Sign Up Failure. Please Try again");
                    }

                }else {
                    handleSignUpFailure("Sign Up Failure. Please Try again");
                }


            }

            @Override
            public void onFailure(Call<SignUpAndLoginResponse> call, Throwable t) {
                progressDialog.dismiss();
                handleSignUpFailure(t.getMessage());
            }
        });


    } // sign up with api

    private void handleSignUpSuccessFully(SignUpAndLoginResponse signUp) {

        SharePreferentService.SaveToken(SignUpActivity.this,signUp.token);
        goToMainActivity();

    } // handle sign up Successfully

    private void goToMainActivity() {

        Intent mainActivityIntent = new Intent(SignUpActivity.this,MainActivity.class);
        startActivity(mainActivityIntent);
        finish();

    } //redirect to main activity


    private void handleSignUpFailure(String messg) {
        AlertDialogService.ShowAlertDialog(SignUpActivity.this,"Sign Up Failure",messg);
    } //handel sign up error



}
