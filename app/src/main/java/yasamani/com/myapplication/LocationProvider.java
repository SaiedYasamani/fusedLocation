package yasamani.com.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import javax.security.auth.callback.Callback;

public class LocationProvider {

    private Context context;
    private Location location;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private LocationCallback callback;
    private FusedLocationProviderClient client;

    public LocationProvider(final Context context) {
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    public void startUpdateLocation() {
        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                LocationRequest request = new LocationRequest();
                request.setFastestInterval(1000);
                request.setInterval(5000);
                LocationCallback callback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        location = locationResult.getLastLocation();
                    }
                };
                FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
                client.requestLocationUpdates(request, callback, null);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof ResolvableApiException){
                            ResolvableApiException resolvable = (ResolvableApiException)e;
                            try {
                                resolvable.startResolutionForResult((Activity) context,1000);
                            } catch (IntentSender.SendIntentException ex) {
                                Log.d("resolution error", "onFailure: " + ex.getLocalizedMessage());
                            }
                        }
                    }
                });
    }


    public Location getLocation() {
        return location;
    }
}
