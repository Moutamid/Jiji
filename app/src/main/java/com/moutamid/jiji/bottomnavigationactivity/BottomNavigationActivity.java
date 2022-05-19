package com.moutamid.jiji.bottomnavigationactivity;

import static com.moutamid.jiji.utils.Stash.toast;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.moutamid.jiji.BuildConfig;
import com.moutamid.jiji.MainActivity;
import com.moutamid.jiji.R;
import com.moutamid.jiji.databinding.ActivityBottomNavigationBinding;
import com.moutamid.jiji.utils.Constants;

public class BottomNavigationActivity extends AppCompatActivity {

    private ActivityBottomNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBottomNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if (Constants.auth().getCurrentUser() == null){
            startActivity(new Intent(BottomNavigationActivity.this, MainActivity.class));
            finish();
            return;
        }

        BottomNavigationView navView = findViewById(com.moutamid.jiji.R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_favourite, R.id.navigation_sell)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom_navigation);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        getUserLocation();
    }

    public void getUserLocation() {
        Dexter.withActivity(BottomNavigationActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Dexter.withActivity(BottomNavigationActivity.this)
                                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {
                                        FusedLocationProviderClient fusedLocationProviderClient;
                                        LocationRequest locationRequest;
                                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(BottomNavigationActivity.this);

                                        locationRequest = LocationRequest.create();
                                        locationRequest.setInterval(500);
                                        locationRequest.setFastestInterval(500);
                                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                                        if (ActivityCompat.checkSelfPermission(BottomNavigationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BottomNavigationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            toast("Permission not granted!");
                                            return;
                                        }

                                        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                                        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                                Constants.databaseReference()
                                                        .child(Constants.USERS)
                                                        .child(Constants.auth().getUid())
                                                        .child("current_location")
                                                        .setValue(latLng);
                                            }
                                        });
                                        locationTask.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                toast(e.getMessage().toString());
                                            }
                                        });
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {
                                        if (response.isPermanentlyDenied()) {
                                            // open device settings when the permission is
                                            // denied permanently
                                            toast("You need to provide permission!");

                                            Intent intent = new Intent();
                                            intent.setAction(
                                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package",
                                                    BuildConfig.APPLICATION_ID, null);
                                            intent.setData(uri);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                }).check();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            toast("You need to provide permission!");

                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }
}