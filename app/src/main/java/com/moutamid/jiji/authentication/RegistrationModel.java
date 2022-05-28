package com.moutamid.jiji.authentication;

import static com.moutamid.jiji.utils.Stash.toast;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.moutamid.jiji.BuildConfig;
import com.moutamid.jiji.bottomnavigationactivity.BottomNavigationActivity;
import com.moutamid.jiji.bottomnavigationactivity.HomeActivity;
import com.moutamid.jiji.utils.Constants;
import com.moutamid.jiji.utils.Stash;

public class RegistrationModel {

    private RegistrationActivity activity;

    public RegistrationModel(RegistrationActivity activity) {
        this.activity = activity;
    }

    public boolean isEveryThingCompleted() {
        if (activity.b.emailEditText.getText().toString().isEmpty()) {
            toast("Email is empty!");
            return false;
        }
        if (activity.b.passwordEditText.getText().toString().isEmpty()) {
            toast("Password is empty!");
            return false;
        }

        if (activity.REGISTER_TYPE.equals(Constants.SIGN_UP)) {

            if (activity.b.nameEditText.getText().toString().isEmpty()) {
                toast("Name is empty!");
                return false;
            }

            if (activity.b.numberEditText.getText().toString().isEmpty()) {
                toast("Number is empty!");
                return false;
            }

            if (activity.userModel.user_type.equals(Constants.TYPE_SELLER)) {
                if (activity.userModel.id_card_link == null) {
                    toast("Please upload a id card!");
                    return false;
                }
                if (activity.userModel.tax_certificate_link == null) {
                    toast("Please upload a tax certificate!");
                    return false;
                }

            }
        }

        return true;
    }

    public void getUserLocation() {
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Dexter.withActivity(activity)
                                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {
                                        FusedLocationProviderClient fusedLocationProviderClient;
                                        LocationRequest locationRequest;
                                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

                                        locationRequest = LocationRequest.create();
                                        locationRequest.setInterval(500);
                                        locationRequest.setFastestInterval(500);
                                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            toast("Permission not granted!");
                                            return;
                                        }
                                        activity.progressDialog.show();
                                        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                                        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                activity.userModel.current_location = new LatLng(location.getLatitude(), location.getLongitude());
                                                if (activity.REGISTER_TYPE.equals(Constants.SIGN_UP)) {
                                                    // SIGN UP
                                                    activity.controller.signUp();
                                                } else {
                                                    // LOGIN
                                                    activity.controller.login();
                                                }
                                            }
                                        });
                                        locationTask.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                activity.progressDialog.dismiss();
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
                                            activity.startActivity(intent);
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
                            activity.startActivity(intent);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    public void uploadUserDetails() {
        activity.userModel.name = activity.b.nameEditText.getText().toString();
        activity.userModel.number = activity.b.numberEditText.getText().toString();
        activity.userModel.email = activity.b.emailEditText.getText().toString();

        Stash.put(Constants.USER_NUMBER, activity.userModel.number);
        Stash.put(Constants.CURRENT_USER_MODEL, activity.userModel);

        Constants.databaseReference()
                .child(Constants.USERS)
                .child(Constants.auth().getUid())
                .setValue(activity.userModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        activity.progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            toast("Sign up success!");
                            activity.finish();
                            activity.startActivity(new Intent(activity, BottomNavigationActivity.class));
                        } else {
                            toast(task.getException().getMessage());
                        }
                    }
                });
    }
}