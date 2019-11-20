package com.gbrenegadzdev.financeassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.gbrenegadzdev.financeassistant.MainActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = SplashScreenActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if Access token is active
        if (AccessToken.isCurrentAccessTokenActive()) {
            Log.d(TAG, "Logged In");
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "Logged Out");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
