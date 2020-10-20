package com.wadektech.mrajob;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    initSplash();
  }

  @SuppressLint("CheckResult")
  private void initSplash() {
    Completable.timer(5, TimeUnit.SECONDS,
        AndroidSchedulers.mainThread())
        .subscribe(() -> Toast.makeText(getApplicationContext(), "Splash Started", Toast.LENGTH_SHORT).show());
  }
}