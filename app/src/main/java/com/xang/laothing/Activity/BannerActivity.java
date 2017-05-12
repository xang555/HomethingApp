package com.xang.laothing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
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

public class BannerActivity extends AppCompatActivity {

    @BindView(R.id.banner_img)
    ImageView bannerImg;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();

            new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CheckAuthentication(); //check authentication
                    }
            }, 5000);


    }


    private void CheckAuthentication(){


      if (auth.getCurrentUser() !=null){
          //have login
          String uid = auth.getCurrentUser().getUid();
          ApiService.getRouterServiceApi().Login(new LoginRequest(uid))
                  .enqueue(new Callback<SignUpAndLoginResponse>() {
                      @Override
                      public void onResponse(Call<SignUpAndLoginResponse> call, Response<SignUpAndLoginResponse> response) {

                          if (response!=null && response.isSuccessful()){

                              SignUpAndLoginResponse signIn = response.body();
                              if (signIn.err != 1){
                                  SharePreferentService.SaveToken(BannerActivity.this,signIn.token);
                              }
                          }
                          RedirectToMainActivity(); // go to mainactivity
                      }

                      @Override
                      public void onFailure(Call<SignUpAndLoginResponse> call, Throwable t) {
                          RedirectToMainActivity(); // go to mainactivity
                      }
                  });

      }else {
          //no login
          RedirectoLoginActivity();
      }


    } //check authentication


    private void RedirectoLoginActivity(){
        Intent intent = new Intent(BannerActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    } //go to login

    private void RedirectToMainActivity(){
        Intent intent = new Intent(BannerActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }



    }



