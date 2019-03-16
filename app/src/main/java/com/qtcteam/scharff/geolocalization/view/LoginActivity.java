package com.qtcteam.scharff.geolocalization.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.qtcteam.scharff.geolocalization.R;

public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "QTC_GEO_LOGIN";

    private EditText mEmail;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login_title);

        mEmail = findViewById(R.id.login_input_email);
        mPassword = findViewById(R.id.login_input_password);
    }

    public void login (View v) {
        // Get email and validate
        String email = mEmail.getText().toString();
        if (!validateEmail(email)) return;
        // Get password and validate
        String password = mPassword.getText().toString();
        if (!validatePassword(password)) return;

        // Iniciar sesión usando Firebase Authentication
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startTracking();
                        } else {
                            showAuthErrorMessage(task.getException());
                        }
                    }
                });
        // Mostrar mensaje de inicio de sesión al usuario
        Toast.makeText(this, R.string.login_msg_validating, Toast.LENGTH_SHORT).show();
    }

    private boolean validateEmail (String email) {
        if (email.length() == 0) {
            Toast.makeText(this, R.string.login_msg_email_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.login_msg_email_invalid, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePassword (String password) {
        if (password.length() == 0) {
            Toast.makeText(this, R.string.login_msg_password_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void startTracking () {
        Intent intent = new Intent(LoginActivity.this, TrackingActivity.class);
        startActivity(intent);
        finish();
    }

    private void showAuthErrorMessage (Exception exception) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                .setTitle(R.string.login_dialog_auth_title)
                .setMessage(getAuthErrorMessage(exception))
                .setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    private String getAuthErrorMessage (Exception exception) {
        if (exception == null) {
            return getString(R.string.firebase_error_unknown);
        }
        if (exception instanceof FirebaseNetworkException) {
            return getString(R.string.firebase_error_network);
        }
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return getString(R.string.firebase_auth_password_invalid);
        }
        if (exception instanceof FirebaseAuthException) {
            String errorCode = ((FirebaseAuthException) exception).getErrorCode();
            switch (errorCode) {
                case "ERROR_USER_NOT_FOUND":
                    return getString(R.string.firebase_auth_email_not_found);
                case "ERROR_USER_DISABLED":
                    return getString(R.string.firebase_auth_email_disabled);
                default:
                    Log.d(TAG, errorCode);
            }
        }
        Log.d(TAG, exception.getClass().getName());
        return exception.getLocalizedMessage();
    }

}
