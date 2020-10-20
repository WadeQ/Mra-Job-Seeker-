package com.wadektech.mrajob;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

public class SplashActivity extends AppCompatActivity {
  private final static int LOGIN_REQUEST_CODE = 1988;
  private List<AuthUI.IdpConfig> authProviders ;
  private FirebaseAuth firebaseAuth ;
  private FirebaseAuth.AuthStateListener authStateListener ;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    init();
  }

  private void init() {
    authProviders = Arrays.asList(
        new AuthUI.IdpConfig.PhoneBuilder().build(),
        new AuthUI.IdpConfig.GoogleBuilder().build()) ;

    firebaseAuth = FirebaseAuth.getInstance();
    authStateListener = myFirebaseAuth -> {
      FirebaseUser user = myFirebaseAuth.getCurrentUser();
      if (user != null){
        Toast.makeText(this, "Welcome back "+user.getDisplayName(), Toast.LENGTH_SHORT).show();
      } else {
        displaySignInLayout();
      }
    };
  }

  private void displaySignInLayout() {
    AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout
        .Builder(R.layout.layout_sign_in)
        .setPhoneButtonId(R.id.btn_phone_sign_in)
        .setGoogleButtonId(R.id.btn_google_sign_in)
        .build() ;

    startActivityForResult(AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAuthMethodPickerLayout(authMethodPickerLayout)
        .setIsSmartLockEnabled(false)
        .setTheme(R.style.LoginTheme)
        .setAvailableProviders(authProviders)
        .build(), LOGIN_REQUEST_CODE);

  }

  @SuppressLint("CheckResult")
  private void initAuth() {
    Completable.timer(5, TimeUnit.SECONDS,
        AndroidSchedulers.mainThread())
        .subscribe(() ->
            firebaseAuth.addAuthStateListener(authStateListener)
            );
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == LOGIN_REQUEST_CODE){
      IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
      if (resultCode == RESULT_OK){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
      } else {
        Toast.makeText(getApplicationContext(), "[Error]: "+idpResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    initAuth();
  }

  @Override
  protected void onStop() {
    if (firebaseAuth != null && authStateListener != null){
      firebaseAuth.removeAuthStateListener(authStateListener);
    }
    super.onStop();
  }
}