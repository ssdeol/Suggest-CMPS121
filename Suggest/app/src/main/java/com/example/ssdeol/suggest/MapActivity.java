package com.example.ssdeol.suggest;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssdeol.suggest.models.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.util.Log.i;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapLongClickListener {

    GoogleMap mGoogleMaps;
    FusedLocationProviderClient mFusedLocationProviderClient;


    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int MY_LOCATION_REQUEST_CODE = 1;

    private static final String TAG = "MapFragment";
    private static final float defaultZoom = 15f;
    private static final int PLACE_PICKER_REQUEST = 1;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mGooglePlace;
    private Marker mGoogleMarker;
    double latitude, longitude;
    LatLng latLngList;
    String place;
    String placeName;
    String address = "";
    int NEARBY_RADIUS = 10000;

    // We need 2 coordinates as Bounds that basically encompasses the entire world.
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private static final int ERROR_DIALOG_REQUEST = 9001;   // If the user phone doesn't have the correct version. Give this error code.

    // Widgets
    private AutoCompleteTextView mGoogleSearchText;
    private ImageView mGoogleMyLocation;
    private ImageView mGoogleInfo;
    private ImageView mGooglePlacesIcon;
    private Button placesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mGoogleSearchText = (AutoCompleteTextView) findViewById(R.id.searchInput);
        mGoogleMyLocation = (ImageView) findViewById(R.id.myLocationGPSIcon);
        mGoogleInfo = (ImageView) findViewById(R.id.myInfomrationIcon);
        mGooglePlacesIcon = (ImageView) findViewById(R.id.myPlacesIcon);

        placesButton = (Button) findViewById(R.id.placesButton);

        String url = getUrl(latitude, longitude, place);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapsFragment);
        mapFragment.getMapAsync(this);

        getLocationPermissions();
        if (isServicesOk()) {
            Log.d("Services", "Service is Ok.");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public boolean isServicesOk() {
        Log.d("Services", "isServicesOk: checking Google Services version.");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            // The user has the correct version and the Google API can run.
            Log.d("Services", "isServiceOk: Google Play Services is supported.");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // An error has occurred but it is fixiable. For example, the user has the wrong version.
            Log.d("Services", "isServiceOk: an error has occured. Check for solution.");

            // Get the error dialog from Google and display it to the user.
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Google Map will not work.", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void getLocationPermissions() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                prepareMap();
            } else {
                android.support.v4.app.ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            android.support.v4.app.ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mLocationPermissionsGranted = false;

        // Check the permission to see if they were granted. For Loop because multiple permission in array.

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
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

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + NEARBY_RADIUS);
        googlePlaceUrl.append("&type=stores");
        googlePlaceUrl.append("&keyword=" + place);
        googlePlaceUrl.append("&key=" + "AIzaSyAQ0tHxeLZE3pElkuH5xiTl12lrBhMoSlw");

        Log.d("MapsActivity", "url = " + googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    private void prepareMap() {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_LONG).show();
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapsFragment);
//
//        mapFragment.getMapAsync(MapActivity.this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMaps = googleMap;

        mGoogleMaps.setOnMapLongClickListener(this);

        Intent intent = getIntent();

        if (intent.getIntExtra("placeNumber", 0) == 0) {
            if (mLocationPermissionsGranted) {
                getDeviceLocation();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mGoogleMaps.setMyLocationEnabled(true);
                mGoogleMaps.getUiSettings().setMyLocationButtonEnabled(false);
                searchForTerm();
                hideKeyboard();
            }
        } else {
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MyListActivity.locations.get(intent.getIntExtra("placeNumber", 0)).latitude);
            placeLocation.setLongitude(MyListActivity.locations.get(intent.getIntExtra("placeNumber", 0)).longitude);

            moveCamera(new LatLng(placeLocation.getLatitude(), placeLocation.getLongitude()), defaultZoom, "Your Place");
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        latLngList = latLng;

        try {

            List<Address> listAddresses = geocoder.getFromLocation(latLngList.latitude, latLngList.longitude, 1);

            if (listAddresses != null && listAddresses.size() > 0) {

                if (listAddresses.get(0).getThoroughfare() != null) {

                    if (listAddresses.get(0).getSubThoroughfare() != null) {

                        address += listAddresses.get(0).getSubThoroughfare() + " ";

                    }

                    address += listAddresses.get(0).getThoroughfare();

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Name: ");
        alertDialog.setMessage("Enter Name of the place");

        final EditText input = new EditText(MapActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("Add Place",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        placeName = input.getText().toString();
                        if (address.equals("")) {

//            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");
//
                            address = placeName;
//
                        }

                        mGoogleMaps.addMarker(new MarkerOptions().position(latLngList).title(address));

                        MyListActivity.places.add(address);
                        MyListActivity.locations.add(latLngList);
                    }
                }
        );

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );

        alertDialog.show();

    //    MyListActivity.arrayAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();
    }



    private void searchForTerm(){
        Log.d("Services", "Init function: Initializing.");


        // Google Places API (Directly from google documentation)
        if(mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            try {
                mGoogleApiClient = new GoogleApiClient
                        .Builder(this)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .enableAutoManage(this, this)
                        .build();
            } catch (Exception e) {
                Log.e("Maps", "Already working with one client");
            }

            mGoogleSearchText.setOnItemClickListener(mAutocompleteClickListener);

            // An instance of the class taken from google github page (AutoCompleteList)
            mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                    LAT_LNG_BOUNDS, null);

            mGoogleSearchText.setAdapter(mPlaceAutocompleteAdapter);

            mGoogleSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                            || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                        //execute our method for searching
                        geoLocate();
                    }

                    return false;
                }
            });

            mGoogleMyLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Maps", "My Location Icon clicked, getting device location.");
                    getDeviceLocation();
                }
            });

            mGoogleInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Maps", "My Info Icon clicked, getting Information.");

                    try {
                        if (mGoogleMarker.isInfoWindowShown()) {
                            mGoogleMarker.hideInfoWindow();
                        } else {
                            Log.d("Maps", mGooglePlace.toString());
                            mGoogleMarker.showInfoWindow();
                        }
                    } catch (NullPointerException e) {
                        Log.d("Maps", "Infomratino was null.");
                    }
                }
            });

            mGooglePlacesIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Google Places API (Directly from there) Pick Places

                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(MapActivity.this), PLACE_PICKER_REQUEST);
                        mGoogleMaps.clear();
                    } catch (GooglePlayServicesRepairableException e) {
                        Log.e("Places", "GooglePlayServicesRepairableException: " + e.getMessage());
                    } catch (GooglePlayServicesNotAvailableException e) {
                        Log.e("Places", "GooglePlayServicesNotAvailableException: " + e.getMessage());
                    }
                }
            });

            placesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Google Places API (Directly from there) Pick Places
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        Intent intent = builder.build(MapActivity.this);
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);
                        mGoogleMaps.clear();
                    } catch (GooglePlayServicesRepairableException e) {
                        Log.e("Places", "GooglePlayServicesRepairableException: " + e.getMessage());
                    } catch (GooglePlayServicesNotAvailableException e) {
                        Log.e("Places", "GooglePlayServicesNotAvailableException: " + e.getMessage());
                    }
                }
            });

            hideKeyboard();
        }
    }

    // Google Places API (Directly from there) Pick Places
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallBack);
            }
        }
    }

    public void geoLocate(){
        Log.d("Maps", "GeoLocation");

        String searchString = mGoogleSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e("Maps", "GeoLocation failed due to IOException: " + e.getMessage());
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d("Maps", "Location Found: " + address);

            // The Location has been found. So move the camera to that Location. AddressLine = Name of the city to be
            // displayed on the marker.

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), defaultZoom, address.getAddressLine(0));
        }
    }

    public void getDeviceLocation(){

        Log.d("Maps", "Getting device Location.");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if(mLocationPermissionsGranted){
                final com.google.android.gms.tasks.Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                        if(task.isSuccessful()){
                            Log.d("Maps", "Location Found");
                            Location currentLocation = task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), defaultZoom, "My Location");
                        } else {
                            Log.d("Maps", "Current Location is null.");
                            Toast.makeText(MapActivity.this, "Unable to get current location.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } catch (SecurityException e){
            Log.e("Maps", "getDeviceLocation: Security Exception: " + e.getMessage());

        }
    }


    // Move the camera and set a custom title
    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d("Maps", "Moving the camera to current location with lat: " + latLng.latitude + " lng: " + latLng.longitude);
        mGoogleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        // Don't drop the marker at "My Location"
        if(!title.equals("My Location")){
            // Drop a pin at the searched Location.
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            mGoogleMaps.addMarker(options);
        }
        hideKeyboard();
    }

    // Move the camera but this all the information from Google Places
    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo){
        Log.d("Maps", "Moving the camera to current location with lat: " + latLng.latitude + " lng: " + latLng.longitude);
        mGoogleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mGoogleMaps.clear();    // Clear the markers

        mGoogleMaps.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity.this));

        if(placeInfo != null){
            try {
                String placeInfomrationSnippet = "Address" + mGooglePlace.getAddress() + "\n" +
                        "Phone Number" + mGooglePlace.getPhoneNumber() + "\n" +
                        "Website" + mGooglePlace.getWebsiteUri() + "\n" +
                        "Rating" + mGooglePlace.getRating() + "\n";

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(mGooglePlace.getName())
                        .snippet(placeInfomrationSnippet);

                mGoogleMarker = mGoogleMaps.addMarker(options);
            } catch (NullPointerException e){
                Log.e("Places", "Moving Camera, some of the information was null.");
            }
        } else {
            mGoogleMaps.addMarker(new MarkerOptions().position(latLng));
        }

        hideKeyboard();
    }

    private void hideKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    // ----------------------------------Google Places API -------------------------------------------

    // This function will give us a place object, which will have all kind of information such as
    // address, phone number, title and so on. This is Google Places Api.


    // This function will submit the request to Google to get the place
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallBack);
        }
    };

    // This function will give us all the information on the place.
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallBack = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d("Places", "PlacesCallBack: Places request could not complete: " + places.getStatus().toString());
                places.release();       // IMPORTANT, Need this to prevent memory leaks, releases the object.
                return;
            }
            final Place place = places.get(0);

            try{
                mGooglePlace = new PlaceInfo();
                mGooglePlace.setName(place.getName().toString());
                mGooglePlace.setAddress(place.getAddress().toString());
                //               mGooglePlace.setAttributions(place.getAttributions().toString());
                mGooglePlace.setPhoneNumber(place.getPhoneNumber().toString());
                mGooglePlace.setLatLng(place.getLatLng());
                mGooglePlace.setId(place.getId());
                mGooglePlace.setRating(place.getRating());
                mGooglePlace.setWebsiteUri(place.getWebsiteUri());

                Log.d("Places", "Place Information: " + mGooglePlace);
            } catch (NullPointerException e){
                Log.e("Places", "Some of the information about a place was null: " + e.getMessage());
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude), defaultZoom, mGooglePlace);
            places.release();
        }
    };

}

