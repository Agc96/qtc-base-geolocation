package com.qtcteam.scharff.geolocalization.presenter;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.qtcteam.scharff.geolocalization.R;
import com.qtcteam.scharff.geolocalization.utils.Utils;
import com.qtcteam.scharff.geolocalization.view.LoginActivity;

public class LoginPresenter implements Presenter {

    private final static String TAG = "QTC_GEO_LOGIN_PRES";

    private LoginActivity view;

    public LoginPresenter (LoginActivity view) {
        this.view = view;
    }

    public boolean validateCredentials (String email, String password) {
        if (view == null) {
            return false;
        }
        if (email.length() == 0) {
            Utils.showMessage(view, R.string.login_msg_email_empty);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Utils.showMessage(view, R.string.login_msg_email_invalid);
            return false;
        }
        if (password.length() == 0) {
            Utils.showMessage(view, R.string.login_msg_password_empty);
            return false;
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            view.startTracking();
                        } else {
                            view.showAuthErrorMessage(task.getException());
                        }
                    }
                });
        return true;
    }

    public String getAuthErrorMessage (Exception exception) {
        if (view == null) {
            return exception.getLocalizedMessage();
        }
        if (exception == null) {
            return view.getString(R.string.firebase_err_unknown);
        }
        if (exception instanceof FirebaseNetworkException) {
            return view.getString(R.string.firebase_err_network);
        }
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return view.getString(R.string.firebase_err_password_invalid);
        }
        if (exception instanceof FirebaseAuthException) {
            String errorCode = ((FirebaseAuthException) exception).getErrorCode();
            switch (errorCode) {
                case "ERROR_USER_NOT_FOUND":
                    return view.getString(R.string.firebase_err_email_not_found);
                case "ERROR_USER_DISABLED":
                    return view.getString(R.string.firebase_err_email_disabled);
                default:
                    Log.d(TAG, errorCode);
            }
        }
        Log.d(TAG, exception.getClass().getName());
        return exception.getLocalizedMessage();
    }


    @Override
    public void onDestroy() {
        this.view = null;
    }

}
