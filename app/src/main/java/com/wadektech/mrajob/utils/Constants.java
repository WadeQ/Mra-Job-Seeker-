package com.wadektech.mrajob.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.wadektech.mrajob.R;
import com.wadektech.mrajob.models.JobSeeker;

public class Constants {
  public static final String JOB_SEEKER_INFO_REFERENCE = "JobSeekerInfo";
  public static final String JOB_SEEKER_LOCATION_REFERENCE = "JobSeekerLocation" ;
  public static final String TOKEN_REFERENCE = "JobSeekerToken";
  public static final String NOTIFICATIONS_TITLE = "NotificationsTitle";
  public static final String NOTIFICATION_BODY = "NotificationsBody";
  public static JobSeeker currentUser;

  public static String userWelcomeBanner() {
    if (Constants.currentUser != null){
      return new StringBuilder("Welcome ")
          .append(Constants.currentUser.getFirstName())
          .append(" ")
          .append(Constants.currentUser.getLastName()).toString();
    } else {
      return "";
    }
  }

  public static void showNotifications(Context context, int id, String title, String body, Intent intent) {
    PendingIntent pendingIntent = null ;
    if (intent != null){
      pendingIntent = PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
      String NOTIFICATION_CHANNEL_ID = "WadekTechnologies" ;
      NotificationManager notificationManager = (NotificationManager) context
          .getSystemService(Context.NOTIFICATION_SERVICE);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
            "Mra Job Seeker",
            NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription("Mra Job Seeker");
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
        notificationChannel.enableVibration(true);
        notificationManager.createNotificationChannel(notificationChannel);
      }
      NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
      builder
          .setContentTitle(title)
          .setContentText(body)
          .setAutoCancel(false)
          .setPriority(NotificationCompat.PRIORITY_HIGH)
          .setDefaults(Notification.DEFAULT_VIBRATE)
          .setSmallIcon(R.drawable.ic_notification_icon)
          .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_notification_icon));
      if (pendingIntent != null){
        builder.setContentIntent(pendingIntent);
      }
      Notification notification = builder.build();
      notificationManager.notify(id,notification);
    }
  }
}
