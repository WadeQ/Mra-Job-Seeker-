package com.wadektech.mrajob.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.rpc.context.AttributeContext;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wadektech.mrajob.R;
import com.wadektech.mrajob.utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
  HomeViewModel homeViewModel;
  GoogleMap mMap;
  SupportMapFragment mapFragment;
  FusedLocationProviderClient fusedLocationProviderClient;
  LocationCallback locationCallback;
  LocationRequest locationRequest;

  DatabaseReference onlineStatusRef, userRef, jobSeekerLocationRef;
  GeoFire geoFire;
  ValueEventListener onlineStatusValueEventListener = new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
      if (snapshot.exists() && userRef != null)
        userRef.onDisconnect().removeValue();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
      Snackbar.make(mapFragment.requireView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
    }
  };

  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    View root = inflater.inflate(R.layout.fragment_home, container, false);

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    mapFragment = (SupportMapFragment) getChildFragmentManager()
        .findFragmentById(R.id.map);
    assert mapFragment != null;
    mapFragment.getMapAsync(this);
    initLocation();
    return root;

  }

  private void initLocation() {
    onlineStatusRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");

    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      Snackbar.make(mapFragment.requireView(),getString(R.string.permission_required),
          Snackbar.LENGTH_LONG).show();
      return;
    }

    locationRequest = new LocationRequest();
    locationRequest.setSmallestDisplacement(10f);
    locationRequest.setInterval(5000);
    locationRequest.setFastestInterval(3000);
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    locationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);

        LatLng latLng = new LatLng(locationResult.getLastLocation().getLatitude(),
            locationResult.getLastLocation().getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addressList ;
        try {
          addressList = geocoder.getFromLocation(locationResult.getLastLocation().getLatitude(),
              locationResult.getLastLocation().getLongitude(),1);
          String cityName = addressList.get(0).getLocality();

          jobSeekerLocationRef = FirebaseDatabase
              .getInstance()
              .getReference(Constants.JOB_SEEKER_LOCATION_REFERENCE)
              .child(cityName);

          userRef = jobSeekerLocationRef.child(Objects.requireNonNull(
              FirebaseAuth
                  .getInstance()
                  .getCurrentUser())
              .getUid());

          geoFire = new GeoFire(jobSeekerLocationRef);
          geoFire.setLocation(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),
              new GeoLocation(locationResult.getLastLocation().getLatitude(),
                  locationResult.getLastLocation().getLongitude()), (key, error) -> {
                if (error != null) {
                  Snackbar.make(mapFragment.requireView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
                } else {
                  Snackbar.make(mapFragment.requireView(), "You are online...", Snackbar.LENGTH_LONG).show();
                }
              });

          updateSeekerOnlineStatus();

        } catch (IOException e) {
          e.printStackTrace();
          Snackbar.make(mapFragment.requireView(), ""+e.getMessage(), Snackbar.LENGTH_SHORT).show();
        }

          }

    };

    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      Snackbar.make(mapFragment.requireView(),getString(R.string.permission_required),
          Snackbar.LENGTH_LONG).show();
      return;
    }
    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    Dexter.withContext(getContext())
        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        .withListener(new PermissionListener() {
          @Override
          public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
              Snackbar.make(mapFragment.requireView(),getString(R.string.permission_required),
                  Snackbar.LENGTH_LONG).show();
              return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setOnMyLocationButtonClickListener(() -> {
              fusedLocationProviderClient.getLastLocation()
                  .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(),
                      Toast.LENGTH_SHORT).show())
                  .addOnSuccessListener(location -> {
                       LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                       mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18f));
                      });
              return false;
            });
            View view = ((View) mapFragment.requireView().findViewById(Integer.parseInt("1")).getParent())
                .findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0,0,0,50);
          }

          @Override
          public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
            Toast.makeText(getContext(), "Permission "+permissionDeniedResponse.getPermissionName()+" was denied!",
                Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

          }
        }).check();

    try {
      boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mra_maps_style));
      if (!success)
        Timber.e("Error parsing map");
    } catch (Resources.NotFoundException notFoundException){
      Timber.e("Exception %s", notFoundException.getMessage());
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    updateSeekerOnlineStatus();
  }

  private void updateSeekerOnlineStatus() {
    onlineStatusRef.addValueEventListener(onlineStatusValueEventListener);
  }

  @Override
  public void onDestroy() {
    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    geoFire.removeLocation(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    onlineStatusRef.removeEventListener(onlineStatusValueEventListener);
    super.onDestroy();
  }
}