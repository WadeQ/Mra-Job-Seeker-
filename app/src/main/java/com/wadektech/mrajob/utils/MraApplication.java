package com.wadektech.mrajob.utils;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by WadeQ on 22/10/2020.
 */
public class MraApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    Timber.plant( new Timber.DebugTree());
  }
}
