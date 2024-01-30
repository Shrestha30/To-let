package com.example.to_let;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.to_let.databinding.ActivityMapsAddHouseBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;

public class MapsAddHouseActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private ActivityMapsAddHouseBinding binding;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Geocoder geocoder;
    private String streetAddress;

    private Button mCurrentLocationBtn;
    private Button mMarkerLocationBtn;

    private Location mLocation;
    private MarkerOptions mMarkerOptions;
    private LatLng mLatlng;

    private HouseInfo mHouseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsAddHouseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Adding client to use fused location
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Transfer the info to next Activity
        mHouseInfo = new HouseInfo();

        //Permission for fine location access
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(MapsAddHouseActivity.this, "Cannot continue as permission has been denied", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        //initializing geocoder
        geocoder = new Geocoder(this);

        //Add house to Current location button
        mCurrentLocationBtn = (Button) findViewById(R.id.addc_urrent_button);
        mCurrentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MapsAddHouseActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsAddHouseActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                //getting location
                Task<Location> locationTask = mFusedLocationProviderClient.getLastLocation();
                locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        try {
                            //storing to Houseinfo to tranfer it to next activity
                            mHouseInfo.setLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
                            mHouseInfo.currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());

                            Intent intent= new Intent(MapsAddHouseActivity.this,EditHouseMapsActivity.class);
                            startActivity(intent);
                            finish();
                        }catch (Exception e){
                            Toast.makeText(MapsAddHouseActivity.this,"Please turn on your location",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                locationTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsAddHouseActivity.this,"Couldn't get your current location",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        //Button for Marker Location
        mMarkerLocationBtn = (Button) findViewById(R.id.selected_location_btn);
        mMarkerLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMarkerOptions!=null) {
                    if (ActivityCompat.checkSelfPermission(MapsAddHouseActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsAddHouseActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    //getting current location
                    Task<Location> locationTask = mFusedLocationProviderClient.getLastLocation();
                    locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            try {
                                //setting current location and marker selected location
                                mHouseInfo.currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                                mHouseInfo.setLatLng(mLatlng);

                                Intent intent = new Intent(MapsAddHouseActivity.this, EditHouseMapsActivity.class);
                                startActivity(intent);
                                finish();
                            }catch (Exception e){
                                Toast.makeText(MapsAddHouseActivity.this,"Please turn on your location",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    locationTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MapsAddHouseActivity.this,"Couldn't get your current location",Toast.LENGTH_LONG).show();
                        }
                    });
                }else Toast.makeText(MapsAddHouseActivity.this,"Please add a marker first",Toast.LENGTH_LONG).show();
            }
        });
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

        //adding top right current location button
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }

    public void getmylocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Task<Location> task = mFusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                //MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("You are here...!!");

                ///mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
            }
        });
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
       mLatlng = latLng;

       //getting street address from geocoder with location
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude, 1);
            if(addresses.size()>0){
                Address address = addresses.get(0);
                streetAddress = address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //replace marker if it was already added
        if(mMarkerOptions!=null){
            mMarkerOptions.position(latLng);
            mMarkerOptions.title(streetAddress);
        }else {
            mMarkerOptions = new MarkerOptions().position(latLng).title(streetAddress).draggable(true);
            mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.house_location_small));
            mMap.addMarker(mMarkerOptions);
        }
        Log.d("longClick: ", mLatlng.toString());
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mLatlng = marker.getPosition();

        //changing the street address of the dragged marker correspondong to new location
        try {
            List<Address> addresses = geocoder.getFromLocation(mLatlng.latitude,mLatlng.longitude, 1);
            if(addresses.size()>0){
                Address address = addresses.get(0);
                streetAddress = address.getAddressLine(0);
                marker.setTitle(streetAddress);
                mMarkerOptions.title(streetAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("dragend: ",mLatlng.toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(MapsAddHouseActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        return super.onKeyDown(keyCode, event);
    }
}