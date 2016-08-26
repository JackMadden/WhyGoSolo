package com.reraisedesign.whygosolo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private ArrayList<LatLng> locations;
    private GoogleApiClient mGoogleApiClient = null;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private LocationRequest mLocationRequest;
    private final static int MY_PERMISSIONS_REQUEST = 1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private Marker mMarker;
    private LatLng mUserLocation;

    // flag that prevents the users location from being set on the map every time the activity
    // is resumed or a connection is made wit the google maps API.
    private static boolean mStartedFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // variables set up so we can tell what Activity requested the map,
        //TODO: make sure the functionality works for both the signup and createEvent.
        Intent i = new Intent(this, MapsActivity.class);
        String previousActivity = i.getStringExtra("FROM_ACTIVITY");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if(!mStartedFlag) {
            mStartedFlag = true;
            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    // when the users location changes handle the new location.
    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    // if the users location changes or hasn't been handled previously retrieve it and add it to the
    // map
    private void handleNewLocation(Location location) {
        double currentLat = location.getLatitude();
        double currentLng = location.getLongitude();
        LatLng userLocation = new LatLng(currentLat, currentLng);
        mUserLocation = userLocation;
        MarkerOptions options = new MarkerOptions()
                .position(userLocation)
                .title("I am here!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .draggable(true);

        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
    }

    // When a network connection with google maps is suspended.
    @Override
    public void onConnectionSuspended(int cause){
        mStartedFlag = true;
    }

    //In the event of network connection failing during the map.
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    // Makes sure that the user has location permission enabled.
    private boolean checkLocationPermission(){
        String permissionFine = "android.permission.ACCESS_FINE_LOCATION";
        String permissionCoarse = "android.permission.ACCESS_COARSE_LOCATION";

        int resultCoarse = getApplicationContext().checkCallingOrSelfPermission(permissionCoarse);
        int resultFine = getApplicationContext().checkCallingOrSelfPermission(permissionFine);
        if(resultCoarse == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        if(resultFine == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mUiSettings = mMap.getUiSettings();

        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setMapToolbarEnabled(true);

        locations = new ArrayList<LatLng>();

        //Add Locations to the locations ArrayList. DUMMY DATA this will be replaced with a JSON request.
        locations.add(new LatLng(53.3761705, -2.9245209000000614));
        locations.add(new LatLng(53.3778821, -2.922889400000031));
        locations.add(new LatLng(53.37792880000001, -2.9238224000000628));
        locations.add(new LatLng(53.4126056, -2.9683542000000216));
        locations.add(new LatLng(53.3914749, -2.891935999999987));
        locations.add(new LatLng(53.4079029, -2.9718577999999525));
        locations.add(new LatLng(53.40387279999999, -2.9821145999999317));
        locations.add(new LatLng(53.3999578, -2.9672998999999436));
        locations.add(new LatLng(53.4086908, -2.973349999999982));
        locations.add(new LatLng(53.4020917, -2.9679650000000493));
        locations.add(new LatLng(53.40280689999999, -2.9684475000000248));

        for (LatLng location : locations) {
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title("Marker")
            );
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(locations.get(1)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                mMarker = marker;
                String pos = marker.getPosition().toString();
                String title = marker.getTitle();
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                selectedResidenceLocation(mMarker);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                            case DialogInterface.BUTTON_NEUTRAL:
                                directions(mMarker);
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setMessage("Are you sure you want to select " + title + "?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).setNeutralButton("Directions", dialogClickListener).show();
                return true;
            }
        });

        // Any markers on the map with .draggable(true) will be effected by this code.
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.setTitle("Custom Location");
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                marker.setDraggable(true);

                MarkerOptions options = new MarkerOptions()
                        .position(mUserLocation)
                        .title("I am here!")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mMap.addMarker(options);
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                marker.getPosition();
            }
        });
    }


    //Creates the option menu for the toolbar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            //when back button is pressed return the user to the activity that started it.
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_next:
                //TODO EventCreation: when the user has set a location for an event along with everything else the nect ubtton should create the event.
                Toast.makeText(this,"NextButton Pressed", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Creates directions for The users location to the selected marker via google maps.
    public void directions(Marker marker){

        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr="
                +mUserLocation.latitude+","+mUserLocation.longitude+"&daddr="
                +marker.getPosition().latitude+","+marker.getPosition().longitude);

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        // Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);
    }

    // send the selected pins LatLng and title to be sent to the SignUpActivity.
    public void selectedResidenceLocation(Marker marker){
        Intent i = new Intent(this, SignUpActivity.class);
        String title = marker.getTitle();
        double lat = marker.getPosition().latitude;
        double lng = marker.getPosition().longitude;
        i.putExtra("EXTRA_LAT", lat);
        i.putExtra("EXTRA_LONG", lng);
        i.putExtra("EXTRA_TITLE", title);
        setResult(Activity.RESULT_OK,i);
        finish();
    }

    // A method which gets and handles the users last location.
    public void getLastLocation(){
        mLocationRequest=new LocationRequest();
        if(checkLocationPermission()) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (lastLocation == null) {
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, this);
            } else {
                handleNewLocation(lastLocation);
            }
        }
        else{
            Toast.makeText(this, "Please enable Location permissions.",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        }
    }

    // Code to be run when the activity resumes.
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    // Code to be run when the activity pauses.
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    // Code to be run when the activity starts.
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    // Code to be run when the activity is stopped
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    // Code to be run when the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            mStartedFlag = false;
        }
    }
}
