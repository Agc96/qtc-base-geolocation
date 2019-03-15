package com.qtcteam.scharff.geolocalization.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.qtcteam.scharff.geolocalization.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login (View v) {
        Intent intent = new Intent(this, TrackingActivity.class);
        startActivity(intent);
        finish();
    }
}
