package com.qtcteam.scharff.geolocalization.view;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.qtcteam.scharff.geolocalization.R;
import com.qtcteam.scharff.geolocalization.presenter.LoginPresenter;
import com.qtcteam.scharff.geolocalization.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "QTC_GEO_LOGIN_VIEW";

    private LoginPresenter presenter;
    private EditText mEmail;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login_title);

        this.presenter = new LoginPresenter(this);
        this.mEmail = findViewById(R.id.login_input_email);
        this.mPassword = findViewById(R.id.login_input_password);
    }

    public void login (View v) {
        // Get email and password
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        // Iniciar sesión usando Firebase Authentication
        if (presenter.validateCredentials(email, password)) {
            Utils.showMessage(this, R.string.login_msg_validating);
        }
    }

    public void startTracking () {
        Intent intent = new Intent(LoginActivity.this, TrackingActivity.class);
        startActivity(intent);
        finish();
    }

    public void showAuthErrorMessage (Exception exception) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                .setTitle(R.string.login_dlg_auth_title)
                .setMessage(presenter.getAuthErrorMessage(exception))
                .setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

}
