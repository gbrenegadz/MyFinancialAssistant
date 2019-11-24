package com.gbrenegadzdev.financeassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gbrenegadzdev.financeassistant.MainActivity;
import com.gbrenegadzdev.financeassistant.R;
import com.gbrenegadzdev.financeassistant.utils.SnackbarUtils;

import java.util.Collections;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private SnackbarUtils snackbarUtils = new SnackbarUtils();
    private ActionBar mActionBar;

    private LoginButton loginButton;

    // Facebook Authentication
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        initUI();
        initFacebookButton();
    }

    private void initUI () {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.hide();
        }

    }

    private void initFacebookButton() {
        loginButton = findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions(Collections.singletonList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d(TAG, "Login Success!!!");
                Log.d(TAG, "Result : " + loginResult.getAccessToken());
                Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                // App code
                Log.d(TAG, "Login Cancelled!!!");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG, "Login Failed!!!");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
