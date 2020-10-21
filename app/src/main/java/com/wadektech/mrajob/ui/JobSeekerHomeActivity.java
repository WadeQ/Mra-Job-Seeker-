package com.wadektech.mrajob.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.wadektech.mrajob.R;
import com.wadektech.mrajob.utils.Constants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class JobSeekerHomeActivity extends AppCompatActivity {

  private AppBarConfiguration mAppBarConfiguration;
  DrawerLayout drawer;
  NavigationView navigationView;
  NavController navController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_job_seeker_home);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    drawer = findViewById(R.id.drawer_layout);
    navigationView = findViewById(R.id.nav_view);
    // Passing each menu ID as a set of Ids because each
    // menu should be considered as top level destinations.
    mAppBarConfiguration = new AppBarConfiguration.Builder(
        R.id.nav_home)
        .setDrawerLayout(drawer)
        .build();
    navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
    NavigationUI.setupWithNavController(navigationView, navController);

    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_sign_out) {
          AlertDialog.Builder builder = new AlertDialog.Builder(JobSeekerHomeActivity.this);
          builder.setTitle("Sign Out")
              .setMessage("Are you sure you want to sign out?")
              .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
              .setPositiveButton("SIGN OUT", (dialog, which) -> JobSeekerHomeActivity.this.signOutUser())
              .setCancelable(false);
          AlertDialog dialog = builder.create();
          dialog.setOnShowListener(dialog1 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(getResources().getColor(R.color.colorAccent));
          });
          dialog.show();
        }
        return true;
      }
    });

    View view = navigationView.getHeaderView(0);
    TextView username = view.findViewById(R.id.tv_user_name);
    TextView userPhone = view.findViewById(R.id.tv_user_phone_number);
    TextView rating = view.findViewById(R.id.tv_user_rating);

    username.setText(Constants.userWelcomeBanner());
    userPhone.setText(Constants.currentUser != null ? Constants.currentUser.getPhoneNumber(): "");
    rating.setText(Constants.currentUser != null ? String.valueOf(Constants.currentUser.getRating()): "0.0");
  }

  private void signOutUser() {
    FirebaseAuth.getInstance().signOut();
    Intent intent = new Intent(JobSeekerHomeActivity.this, SplashActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.job_seeker_home, menu);
    return true;
  }

  @Override
  public boolean onSupportNavigateUp() {
    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    return NavigationUI.navigateUp(navController, mAppBarConfiguration)
        || super.onSupportNavigateUp();
  }
}