/*
HouseInfo.java and its class HouseInfo is intended for exchange of infoemation between activities.
 As all its members and methods are static, one can access it at anytime.
 */

package com.example.to_let;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.provider.Settings;

import com.google.android.gms.maps.model.LatLng;

public class HouseInfo {
    public static LatLng latLng = new LatLng(0,0);
    public static LatLng currentLatLng = new LatLng(0,0);
    public static int radiusInKm,maxRent,minBedRoom,minFloor,minToilet,minSize,maxFloor;
    public static DatabaseInfoHelper mDatabaseInfohelper;

    public static final String geoFireDatabase = "GeoFireLocations";
    public static final String realtimeDatabaseUsers = "Users";
    public static final String databaseAddress = "https://to-let-8cd6d-default-rtdb.asia-southeast1.firebasedatabase.app";

    public static String userId,houseName;

    public static int getMinBedRoom() {
        return minBedRoom;
    }

    public static void setMinBedRoom(int minBedRoom) {
        HouseInfo.minBedRoom = minBedRoom;
    }

    public static int getMinFloor() {
        return minFloor;
    }

    public static void setMinFloor(int minFloor) {
        HouseInfo.minFloor = minFloor;
    }

    public static int getMinToilet() {
        return minToilet;
    }

    public static void setMinToilet(int minToilet) {
        HouseInfo.minToilet = minToilet;
    }

    public static int getMinSize() {
        return minSize;
    }

    public static void setMinSize(int minSize) {
        HouseInfo.minSize = minSize;
    }

    public static int getMaxFloor() {
        return maxFloor;
    }

    public static void setMaxFloor(int maxFloor) {
        HouseInfo.maxFloor = maxFloor;
    }

    public static int getMaxRent() {
        return maxRent;
    }

    public static void setMaxRent(int maxRent) {
        HouseInfo.maxRent = maxRent;
    }

    public static int getRadiusInKm() {
        return radiusInKm;
    }

    public static void setRadiusInKm(int radiusInKm) {
        HouseInfo.radiusInKm = radiusInKm;
    }

    public static LatLng getLatLng() {
        return latLng;
    }

    public static void setLatLng(LatLng latLng) {
        HouseInfo.latLng = latLng;
    }

    public static String GeoKeyToUID(String key){
        int pos= key.indexOf("(");
        return key.substring(0,pos);
    }

    public static String GeoKeyToHouseName(String key){
        int pos= key.indexOf("(");
        return key.substring(pos+1);
    }

    public static void GeoKeyToUIDHN(String key){
        int pos= key.indexOf("(");
        userId=key.substring(0,pos);
        houseName=key.substring(pos+1);
    }

    public static int markerTitleToMarkerInfosIndex(String markerTitle){
        int pos=markerTitle.indexOf(".");
        return Integer.parseInt(markerTitle.substring(0,pos));
    }

    //connection checker which creates dialog in the given context
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
            return true;
        } else {
            showDialog(context);
            return false;
        }
    }
    // creation of dialog box
    private static void showDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Please connect to the internet to proceed further.")
                .setCancelable(false)
                .setPositiveButton("Connect to WIFI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //((Activity)context).finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
