package com.xang.laothing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import com.google.firebase.auth.FirebaseAuth;
import com.xang.laothing.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.emailinputbox)
    EditText emailinputbox;
    @BindView(R.id.passwdinputbox)
    EditText passwdinputbox;
    @BindView(R.id.scroll_login)
    ScrollView scrollLogin;


    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        scrollLogin.setHorizontalScrollBarEnabled(false);
        scrollLogin.setVerticalScrollBarEnabled(false);


    }


    @OnClick({R.id.login_button, R.id.signup, R.id.forgot_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                break;
            case R.id.signup:
                goToSignUpActivity(); // let go to sign up
                break;
            case R.id.forgot_password:
                break;
        }
    }


    private void goToSignUpActivity() {
        Intent signupintent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(signupintent);
    } //go to sig up activity


}
