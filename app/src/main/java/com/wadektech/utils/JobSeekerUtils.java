package com.wadektech.utils;

import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.wadektech.mrajob.utils.Constants;
import java.util.Map;

public class JobSeekerUtils {
  public static void updateJobSeekerCredentials(View view, Map<String, Object> map){
    FirebaseDatabase
        .getInstance()
        .getReference(Constants.JOB_SEEKER_INFO_REFERENCE)
        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
        .updateChildren(map)
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Snackbar.make(view, "Error updating profile!" + e.getMessage(), Snackbar.LENGTH_LONG).show();
          }
        })
        .addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {
            Snackbar.make(view, "Profile updated..", Snackbar.LENGTH_LONG).show();
          }
        });
  }
}
