package com.wadektech.mrajob.services;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wadektech.mrajob.utils.Constants;
import com.wadektech.mrajob.utils.JobSeekerUtils;

import java.util.Map;
import java.util.Random;

public class JobSeekerMessageService extends FirebaseMessagingService {

  @Override
  public void onNewToken(@NonNull String s) {
    super.onNewToken(s);
    if (FirebaseAuth.getInstance().getCurrentUser() != null){
      JobSeekerUtils.updateToken(this, s);
    }

  }

  @Override
  public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
      Map<String,String> msg = remoteMessage.getData();
      if (msg!=null){
        Constants.showNotifications(
            this,
            new Random().nextInt(),
            msg.get(com.wadektech.mrajob.utils.Constants.NOTIFICATIONS_TITLE),
            msg.get(com.wadektech.mrajob.utils.Constants.NOTIFICATION_BODY),
            null
        );
      }
  }
}
