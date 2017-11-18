package com.example.ssdeol.suggestdemo;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    GoogleMap mGoogleMaps;
    MapView mMapView;
    View mView;
    FusedLocationProviderClient mFusedLocationProviderClient;

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int MY_LOCATION_REQUEST_CODE = 1;

    private static final String TAG = "MapFragment";
    private static final float defaultZoom = 15f;

    private static final int ERROR_DIALOG_REQUEST = 9001;   // If the user phone doesn't have the correct version. Give this error code.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLocationPermissions();
        if(isServicesOk()){
            Log.d("Services", "Service is Ok.");
        }
    }


    public boolean isServicesOk(){
        Log.d("Services", "isServicesOk: checking Google Services version.");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS){
            // The user has the correct version and the Google API can run.
            Log.d("Services", "isServiceOk: Google Play Services is supported.");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // An error has occurred but it is fixiable. For example, the user has the wrong version.
            Log.d("Services", "isServiceOk: an error has occured. Check for solution.");

            // Get the error dialog from Google and display it to the user.
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Google Map will not work.", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void getLocationPermissions(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                prepareMap();
            }else{
                android.support.v4.app.ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            android.support.v4.app.ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mLocationPermissionsGranted = false;

        // Check the permission to see if they were granted. For Loop because multiple permission in array.

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted  = false;
                            Log.d("Permissions", "onRequestPermission has failed.");
                            return;
                        }
                    }
                    Log.d("Permissions", "onRequestPermissionsResult permission has been granted");
                    mLocationPermissionsGranted = true;
                    // Start the Map
                    prepareMap();
                }
            }
        }
    }

    private void prepareMap(){
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_LONG).show();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapsFragment);

        mapFragment.getMapAsync(MainActivity.this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMaps = googleMap;

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {



            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

                dlgAlert.setMessage("We Need Location to Suggest Places");
                dlgAlert.setTitle("Error Message...");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //check permission

                            }
                        });
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission
                        .ACCESS_FINE_LOCATION},MY_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_LOCATION_REQUEST_CODE);
            }

        } else {
            mGoogleMaps.setMyLocationEnabled(true);
   //         mGoogleMaps.setOnMyLocationButtonClickListener(this);
    //        mGoogleMaps.setOnMyLocationChangeListener(this);
        }


    }

    public void directionButtonClicked(View view) {
        Intent intent = new Intent(this, DirectionActivity.class);
        startActivity(intent);
    }

    public void placesButtonClicked(View view) {
        Intent intent = new Intent(this, PlacesActivity.class);
        startActivity(intent);
    }

}
