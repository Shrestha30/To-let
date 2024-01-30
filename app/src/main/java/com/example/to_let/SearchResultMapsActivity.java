package com.example.to_let;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.to_let.databinding.ActivitySearchResultMapsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.connection.HostInfo;

public class SearchResultMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivitySearchResultMapsBinding binding;

    private EditText radiusEdTxt;
    private EditText rentEdTxt;
    private EditText bedEdTxt;
    private EditText toiletEdTxt;
    private EditText sizeEdTxt;
    private EditText minFloorEdTxt;
    private EditText maxFloorEdTxt;
    private Button searchBtn;

    private int radiusInKm = HouseInfo.radiusInKm;
    private LatLng mLatLng = HouseInfo.latLng;
    private int maxRent = HouseInfo.getMaxRent();
    private int minBed = HouseInfo.getMinBedRoom();
    private int minToilet = HouseInfo.getMinToilet();
    private int minSize = HouseInfo.getMinSize();
    private int minFloor = HouseInfo.getMinFloor();
    private int maxFloor = HouseInfo.getMaxFloor();

    private DatabaseReference mDatabaseReference;
    private DatabaseReference geoFireHouseDatabaseReference;
    private final String databaseAddress = "https://to-let-8cd6d-default-rtdb.asia-southeast1.firebasedatabase.app";

    private GeoFire mGeoFire;

    //class to store UserId and house name for each marker
    private class MarkerInfo{
        private String UserId,Housename;

        public String getUserId() {
            return UserId;
        }

        public MarkerInfo setUserId(String userId) {
            UserId = userId;
            return this;
        }

        public String getHousename() {
            return Housename;
        }

        public MarkerInfo setHousename(String housename) {
            Housename = housename;
            return this;
        }
    }
    //maximum supported markers is 100
    private MarkerInfo[] markerInfos = new MarkerInfo[101];
    private int markerInfoCnt=0;//total number of stored markers

    private TextView ownerNameTv;
    private TextView ownerNumberTv;
    private TextView houseNameTv;
    private TextView houseAddressTv;
    private TextView houseDetailTv;
    private LinearLayout visLinearLayout;
    private LinearLayout verifiedLinearLayout;
    private LinearLayout notVerifiedLinearLayout;
    private LinearLayout scrollLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchResultMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //All value check
        if(radiusInKm>8580) radiusInKm = 85870;
        if(maxRent>10000000) maxRent = 10000000;
        if(minBed>100) minBed=100;
        if(minFloor>99) minFloor=99;
        if(minToilet>100) minToilet=100;
        if(minSize>100000) minSize=100000;
        if(maxFloor>99) maxFloor=99;

        //set all values of editText fields
        radiusEdTxt = (EditText) findViewById(R.id.radius_edtTxt);
        radiusEdTxt.setText(Integer.toString(HouseInfo.radiusInKm));

        rentEdTxt = (EditText) findViewById(R.id.maxrent_edtxt);
        rentEdTxt.setText(Integer.toString(maxRent));

        bedEdTxt = (EditText) findViewById(R.id.minbedrooms_edtxt);
        bedEdTxt.setText(Integer.toString(minBed));

        toiletEdTxt = (EditText) findViewById(R.id.minbathrooms_edtxt);
        toiletEdTxt.setText(Integer.toString(minToilet));

        sizeEdTxt = (EditText) findViewById(R.id.minsize_edtxt);
        sizeEdTxt.setText(Integer.toString(minSize));

        minFloorEdTxt = (EditText) findViewById(R.id.minfloor_number);
        minFloorEdTxt.setText(Integer.toString(minFloor));

        maxFloorEdTxt = (EditText) findViewById(R.id.maxfloor_number);
        maxFloorEdTxt.setText(Integer.toString(HouseInfo.maxFloor));


        //setting referance of database and geofire
        mDatabaseReference = FirebaseDatabase.getInstance(databaseAddress).getReference();
        geoFireHouseDatabaseReference = mDatabaseReference.child(HouseInfo.geoFireDatabase);

        //initializing geofire
        mGeoFire = new GeoFire(geoFireHouseDatabaseReference);

        searchBtn = (Button) findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //connection check
                if(HouseInfo.isConnected(SearchResultMapsActivity.this)==false) return;

                //clear all previous set errors
                clearInputFieldError();

                //does nothing if the input is invalid
                if(!isInputFieldValid()) return;

                HouseInfo.setRadiusInKm( Integer.parseInt(radiusEdTxt.getText().toString().trim()) );

                Intent intent= new Intent(SearchResultMapsActivity.this,SearchResultMapsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ownerNameTv = (TextView) findViewById(R.id.owner_name);
        ownerNumberTv = (TextView) findViewById(R.id.owner_number);
        houseNameTv = (TextView)  findViewById(R.id.house_name);
        houseAddressTv = (TextView) findViewById(R.id.house_address);
        houseDetailTv = (TextView) findViewById(R.id.house_detail);
        visLinearLayout = (LinearLayout) findViewById(R.id.vis_layout);
        verifiedLinearLayout = (LinearLayout) findViewById(R.id.verified_yes_layout);
        notVerifiedLinearLayout = (LinearLayout) findViewById(R.id.verified_no_layout);
        scrollLinearLayout = (LinearLayout) findViewById(R.id.house_card_scroll);
    }

    private void clearInputFieldError(){
        rentEdTxt.setError(null);
        bedEdTxt.setError(null);
        toiletEdTxt.setError(null);
        sizeEdTxt.setError(null);
        maxFloorEdTxt.setError(null);
        minFloorEdTxt.setError(null);
        radiusEdTxt.setError(null);
    }

    private boolean isInputFieldValid(){
        boolean logic=true;

        //checking if any field is empty
        if(rentEdTxt.getText().toString().trim().isEmpty()) {
            setEditTextEmptyError(rentEdTxt);
            logic=false;}
        if(bedEdTxt.getText().toString().trim().isEmpty()){
            setEditTextEmptyError(bedEdTxt);
            logic=false;
        }
        if(toiletEdTxt.getText().toString().trim().isEmpty()){
            setEditTextEmptyError(toiletEdTxt);
            logic=false;
        }
        if(sizeEdTxt.getText().toString().trim().isEmpty()){
            setEditTextEmptyError(sizeEdTxt);
            logic=false;
        }
        if(maxFloorEdTxt.getText().toString().trim().isEmpty()){
            setEditTextEmptyError(maxFloorEdTxt);
            logic=false;
        }
        if(minFloorEdTxt.getText().toString().trim().isEmpty()){
            setEditTextEmptyError(minFloorEdTxt);
            logic=false;
        }
        if(radiusEdTxt.getText().toString().trim().isEmpty()){
            setEditTextEmptyError(radiusEdTxt);
            logic=false;
        }
        if(!logic) return logic;

        //Getting and setting all input field values
        try{
            HouseInfo.setMaxRent(Integer.parseInt(rentEdTxt.getText().toString().trim()));
            HouseInfo.setMinBedRoom(Integer.parseInt(bedEdTxt.getText().toString().trim()));
            HouseInfo.setMinToilet(Integer.parseInt(toiletEdTxt.getText().toString().trim()));
            HouseInfo.setMinSize(Integer.parseInt(sizeEdTxt.getText().toString().trim()));
            HouseInfo.setMaxFloor(Integer.parseInt(maxFloorEdTxt.getText().toString().trim()));
            HouseInfo.setMinFloor(Integer.parseInt(minFloorEdTxt.getText().toString().trim()));
        }catch (Exception e){
            Toast.makeText(SearchResultMapsActivity.this,"Some Input were invalid",Toast.LENGTH_LONG).show();
            logic = false;
        }
        if(!logic) return logic;

        //checking errors
        if(HouseInfo.getMaxRent()>10000000||HouseInfo.getMaxRent()<0) {
            rentEdTxt.setError("Value out of range(0~10000000)");
            logic=false;
        }
        if(HouseInfo.getMinBedRoom()>100||HouseInfo.getMinBedRoom()<0) {
            bedEdTxt.setError("Value out of range(0~100)");
            logic=false;
        }
        if(HouseInfo.getMinToilet()>100||HouseInfo.getMinToilet()<0) {
            toiletEdTxt.setError("Value out of range(0~100)");
            logic=false;
        }
        if(HouseInfo.getMinSize()>100000||HouseInfo.getMaxRent()<0) {
            sizeEdTxt.setError("Value out of range(0~100000)");
            logic=false;
        }
        if(HouseInfo.getMaxFloor()<HouseInfo.getMinFloor()){
            maxFloorEdTxt.setError("Max Floor cannot be less than min floor");
            minFloorEdTxt.setError("Max Floor cannot be less than min floor");
            logic=false;
        }
        if(HouseInfo.getMaxFloor()>99||HouseInfo.getMaxFloor()<0) {
            maxFloorEdTxt.setError("Value out of range(0~99)");
            logic=false;
        }
        if(HouseInfo.getMinFloor()>99||HouseInfo.getMinFloor()<0) {
            minFloorEdTxt.setError("Value out of range(0~99)");
            logic=false;
        }

        return  logic;
    }

    private void setEditTextEmptyError(EditText editText){
        editText.setError("This field cannot be empty");
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
        LatLng sydney = new LatLng(mLatLng.latitude,mLatLng.longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Your Selected Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.stand_man)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12));
        mMap.addCircle(new CircleOptions()
                .center(mLatLng)
                .radius(radiusInKm*1000)
                .strokeColor(Color.RED)
                .fillColor(0x220000FF)
                .strokeWidth(5)
        );

        //Searching started
        Toast.makeText(SearchResultMapsActivity.this, "Search Started", Toast.LENGTH_LONG).show();
        startMapSearch();

        //showing house details if marker is clicked
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String markerTitle = marker.getTitle();
                int index=1;

                //getting index for the markerInfos Array from the marker title
                // marker title consists of "index.houseName"
                try{index=HouseInfo.markerTitleToMarkerInfosIndex(markerTitle);}catch (Exception e){return false;}

                //getting targeted house from database
                MarkerInfo markerInfo = markerInfos[index-1];
                House house = HouseInfo.mDatabaseInfohelper.getHouse(markerInfo.getUserId(),markerInfo.getHousename());

                ownerNameTv.setText("Owner Name: "+HouseInfo.mDatabaseInfohelper.getUserName(markerInfo.getUserId()));
                ownerNumberTv.setText("Owner Contact Number: "+HouseInfo.mDatabaseInfohelper.getUserPhone(markerInfo.getUserId()));
                houseNameTv.setText("House Name: "+house.getName()/*markerInfo.getHousename()*/);
                houseAddressTv.setText("House Address: "+house.getStreetAddress()/*HouseInfo.mDatabaseInfohelper.getHouseAddress(markerInfo.getUserId(),markerInfo.getHousename())*/);
                houseDetailTv.setText("House Detail: "+house.getDetail()/*HouseInfo.mDatabaseInfohelper.getHouseDetail(markerInfo.getUserId(),markerInfo.getHousename())*/);


                //if verified
                if(house.getIsVerified()==1){
                    verifiedLinearLayout.setVisibility(View.VISIBLE);
                    notVerifiedLinearLayout.setVisibility(View.INVISIBLE);
                }else if(house.getIsVerified()==0){
                    verifiedLinearLayout.setVisibility(View.INVISIBLE);
                    notVerifiedLinearLayout.setVisibility(View.VISIBLE);
                }

                visLinearLayout.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    private void startMapSearch(){
        GeoQuery geoQuery = mGeoFire.queryAtLocation(new GeoLocation(mLatLng.latitude, mLatLng.longitude),radiusInKm);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //wont accept more than 100 markers
                if(markerInfoCnt==100) return;

                //getting full info from key
                HouseInfo.GeoKeyToUIDHN(key);

                //getting house info according to the info received
                House house = HouseInfo.mDatabaseInfohelper.getHouse(HouseInfo.userId,HouseInfo.houseName);

                //checking all info to be filtered
                if(house.getRent()>maxRent ||
                   house.getBedrooms()<minBed ||
                    house.getBathrooms()<minToilet ||
                    house.getSize()<minSize ||
                    house.getFloor()<minFloor ||
                    house.getFloor()>maxFloor) return;

                //if valid pushing to array
                markerInfos[markerInfoCnt++]=new MarkerInfo().setHousename(HouseInfo.houseName).setUserId(HouseInfo.userId);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(location.latitude,location.longitude));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.house_location_small));
                //setting the marker title as "Index. houseName" where index is the index of MarkerInfos
                //array where the userId and HouseName of this marker is stored
                markerOptions.title(Integer.toString(markerInfoCnt)+". "+HouseInfo.houseName);

                mMap.addMarker(markerOptions);

                //Adding house_card to scrollview
                addHouseCard(house,HouseInfo.userId,HouseInfo.houseName,markerInfoCnt);
            }

            private void addHouseCard(House house, String userId, String houseName, int index){
                //creatinf new scroll element and adding it to the scroll view
                View view = getLayoutInflater().inflate(R.layout.house_card,null);
                scrollLinearLayout.addView(view);

                //setting verified or not verified icon
                ImageView verifiedImg = (ImageView) view.findViewById(R.id.house_card_verified_img);
                if(house.getIsVerified()==0) verifiedImg.setImageResource(R.drawable.not_verified);

                ImageView detailsImg = (ImageView) view.findViewById(R.id.house_card_details_img);
                detailsImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Need to be implemented to show the detailed view of the To-let
                    }
                });

                TextView indexText = (TextView) view.findViewById(R.id.house_card_index);
                indexText.setText(Integer.toString(index)+". ");

                TextView ownerNameText = (TextView) view.findViewById(R.id.house_card_owner);
                ownerNameText.setText("Owner Name: "+
                        HouseInfo.mDatabaseInfohelper.getUser(userId).getName());

                TextView houseNameText = (TextView) view.findViewById(R.id.house_card_name);
                houseNameText.setText("House Name: "+house.getName());
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(SearchResultMapsActivity.this, MapsSearchHouseActivity.class);
        startActivity(intent);
        finish();
        return super.onKeyDown(keyCode, event);
    }
}