package com.example.to_let;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.to_let.databinding.ActivityEditHouseMapsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class EditHouseMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityEditHouseMapsBinding binding;
    private LatLng mLatLng;
    private MarkerOptions mMarkerOptions;

    private String streetAddress;

    private Geocoder geocoder;

    private Button mAddBtn;
    private EditText mNameEdt;
    private EditText mAddressEdt;
    private EditText mDetailsEdt, mRentEdt,mBedroomEdt, mFloorEdt, mBathroomEdt,mSizeEdt;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private final String databaseAddress = "https://to-let-8cd6d-default-rtdb.asia-southeast1.firebasedatabase.app";
    private String userId;
    private DatabaseReference mDatabaseHouseReference;
    private ValueEventListener mValueEventListener;

    private String address, name, detail;
    private int rent,floor,bedroom,bathroom,size;

    private boolean isHouseNameOk = false;//its true if the House name as same as the input already exists in database
    private boolean isEventListenerAdded = false;//to check whether event listener was added to remove it after the activity ends

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //viewing current longitude and latitude
        mLatLng = HouseInfo.latLng;

        binding = ActivityEditHouseMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //initializing geocoder
        geocoder = new Geocoder(this);

        //getting current street address
        try {
            List<Address> addresses = geocoder.getFromLocation(mLatLng.latitude,mLatLng.longitude, 1);
            if(addresses.size()>0){
                Address address = addresses.get(0);
                streetAddress = address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //initializing firebase info
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance(databaseAddress).getReference();
        userId = mAuth.getCurrentUser().getUid();

        //setting House path in database
        mDatabaseHouseReference = mDatabaseReference.child("Users").child(userId).child("Houses");
        //mDatabaseHouseReference.setValue(true);

        //creating database listener for checking if current house name exists
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.getChildrenCount()>0) isHouseNameOk = true;
                else isHouseNameOk = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };


        //setting references of EditText and button
        setId();
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //connection check
                if(HouseInfo.isConnected(EditHouseMapsActivity.this)==false) return;

                try{
                //retrieving all the info
                address = mAddressEdt.getText().toString().trim();
                name = mNameEdt.getText().toString().trim();
                detail = mDetailsEdt.getText().toString().trim();
                rent = Integer.parseInt(mRentEdt.getText().toString().trim());
                bedroom = Integer.parseInt(mBedroomEdt.getText().toString().trim());
                floor = Integer.parseInt(mFloorEdt.getText().toString().trim());
                bathroom = Integer.parseInt(mBathroomEdt.getText().toString().trim());
                size = Integer.parseInt(mSizeEdt.getText().toString().trim());}catch (Exception e){

                }

                //clear all error
                clearError();

                //adding event listener to check if the house name is unique or not among the houses owned by the user
                //Unique house name is necessary as UserId along with Unique house name created a Unique ID for the house?to-let
                mDatabaseHouseReference.child(name).addValueEventListener(mValueEventListener);
                isEventListenerAdded = true;

                //check validity of info
                if(isInfoValid()){
                    mDatabaseHouseReference.child(name).setValue(new House(detail,name,address,
                            mLatLng.latitude,mLatLng.longitude,isVerified(),rent,bedroom,floor,bathroom,size));


                    Toast.makeText(EditHouseMapsActivity.this,"House Added",Toast.LENGTH_LONG).show();

                    //Update info in Geofire
                    addGeoFireData();

                    Intent mainIntent= new Intent(EditHouseMapsActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        });
    }

    private boolean isInfoValid(){
        boolean logic = true;

        if(address.isEmpty()){
            mAddressEdt.setError("This field cannot be empty");
            logic = false;
        }

        if(name.isEmpty()){
            mNameEdt.setError("This field cannot be empty");
            logic = false;
        }else if(isHouseNameOk) {
            mNameEdt.setError("House Name Already exists");
            logic = false;
        }

        if(mRentEdt.getText().toString().trim().isEmpty()){
            mRentEdt.setError("This field cannot be empty");
            logic = false;
        }else if(rent<0||rent>10000000){
            mRentEdt.setError("Value outside of range");
            logic = false;
        }

        if(mBedroomEdt.getText().toString().trim().isEmpty()){
            mBedroomEdt.setError("This field cannot be empty");
            logic = false;
        }else if(bedroom<0||bedroom>100){
            mBedroomEdt.setError("Value outside of range");
            logic = false;
        }

        if(mFloorEdt.getText().toString().trim().isEmpty()){
            mFloorEdt.setError("This field cannot be empty");
            logic = false;
        }else if(floor<0||floor>99){
            mFloorEdt.setError("Value outside of range");
            logic = false;
        }

        if(mBathroomEdt.getText().toString().trim().isEmpty()){
            mBathroomEdt.setError("This field cannot be empty");
            logic = false;
        }else if(bathroom<0||bathroom>100){
            mBathroomEdt.setError("Value outside of range");
            logic = false;
        }

        if(mSizeEdt.getText().toString().trim().isEmpty()){
            mSizeEdt.setError("This field cannot be empty");
            logic = false;
        }else if(size<10||size>100000){
            mSizeEdt.setError("Value outside of range");
            logic = false;
        }

        return logic;
    }

    private void clearError(){
        mAddressEdt.setError(null);
        mAddressEdt.setError(null);
        mNameEdt.setError(null);
        mDetailsEdt.setError(null);
        mRentEdt.setError(null);
        mBedroomEdt.setError(null);
        mFloorEdt.setError(null);
        mBathroomEdt.setError(null);
        mSizeEdt.setError(null);
    }

    private void addGeoFireData(){
        GeoFire geoFire = new GeoFire(mDatabaseReference.child("GeoFireLocations"));
        //Generating unique House ID for each house and storing it as the geofire coded location in database
        geoFire.setLocation(userId+"("+name, new GeoLocation(mLatLng.latitude,mLatLng.longitude));
    }

    private void setId(){
        mAddBtn = (Button) findViewById(R.id.save_btn);
        mAddressEdt = (EditText) findViewById(R.id.address_edtxt);
        mAddressEdt.setText(streetAddress);
        mNameEdt = (EditText) findViewById(R.id.house_name);
        mDetailsEdt = (EditText) findViewById(R.id.option_edtxt);
        mRentEdt = (EditText) findViewById(R.id.rent_edtxt);
        mBedroomEdt = (EditText) findViewById(R.id.bedrooms_edtxt);
        mFloorEdt = (EditText) findViewById(R.id.floor_number);
        mBathroomEdt = (EditText) findViewById(R.id.bathrooms_edtxt);
        mSizeEdt = (EditText) findViewById(R.id.size_edtxt);
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

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(mLatLng).title(streetAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.house_location_small)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng,15));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isEventListenerAdded) mDatabaseHouseReference.child(name).removeEventListener(mValueEventListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(EditHouseMapsActivity.this, MapsAddHouseActivity.class);
        startActivity(intent);
        finish();
        return super.onKeyDown(keyCode, event);
    }

    //method to check whether the house added is to be marked as verified or not
    //depending on the distance between house location and the location of the user while adding the house
    private int isVerified(){
        float results[] = new float[10];
        Location.distanceBetween(mLatLng.latitude,mLatLng.longitude,HouseInfo.currentLatLng.latitude,
                HouseInfo.currentLatLng.longitude,results);
        if(results[0]<=50) return 1;
        return 0;
    }
}