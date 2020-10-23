package com.wadektech.mrajob.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.wadektech.mrajob.models.Token;

import java.util.Map;


public class JobSeekerUtils {
  public static void updateJobSeekerCredentials(View view, Map<String, Object> map){
    FirebaseDatabase
        .getInstance()
        .getReference(Constants.JOB_SEEKER_INFO_REFERENCE)
        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
        .updateChildren(map)
        .addOnFailureListener(e -> Snackbar.make(view, "Error updating profile!" + e.getMessage(),
            Snackbar.LENGTH_LONG).show())
        .addOnSuccessListener(aVoid -> Snackbar.make(view, "Profile updated successfully..",
            Snackbar.LENGTH_LONG).show());
  }

  public static void updateToken(Context context, String token) {
    Token userToken = new Token(token);
    FirebaseDatabase
        .getInstance()
        .getReference(Constants.TOKEN_REFERENCE)
        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
        .setValue(userToken)
        .addOnFailureListener(e ->
            Toast.makeText(context, "Error saving token "+e.getMessage(),Toast.LENGTH_SHORT)
                .show()).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void aVoid) {

      }
    });
  }
}
