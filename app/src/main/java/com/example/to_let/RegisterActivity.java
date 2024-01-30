package com.example.to_let;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;
    private Toolbar mToolbar;
    private ProgressDialog mCreatAccountProgrees;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
//<<<<<<< Updated upstream
    final String databaseAddress = "https://to-let-8cd6d-default-rtdb.asia-southeast1.firebasedatabase.app";
//=======
   // final String databaseAddress = "https://to-let-8cd6d-default-rtdb.asia-southeast1.firebasedatabase.app";
    private FirebaseDatabase mdatabase;
//>>>>>>> Stashed changes
    private DatabaseReference mDatabaseReferance;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //setting Location Provider
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mCreatAccountProgrees = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        //tool bar here
        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDisplayName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateBtn = (Button) findViewById(R.id.eg_creat_btn);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //network connection check
                if(!HouseInfo.isConnected(RegisterActivity.this)) return;

                //check current location and calls the method of the activities of button
                currentLocationCheckAndCall();
            }
        });


    }

    private void register_user(String display_namee, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser currentUser = mAuth.getInstance().getCurrentUser();
                    String userId = currentUser.getUid();
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //saving user information in database and Current Location
                                mdatabase = FirebaseDatabase.getInstance(databaseAddress);
                                mDatabaseReferance = mdatabase.getReference().child("Users").child(userId);
                                User user = new User(display_namee, "Hi There!", "default",mLastLocation.getLatitude(),mLastLocation.getLongitude());

                                mDatabaseReferance
                                        .child("profile").setValue(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    mCreatAccountProgrees.dismiss();
                                                   //after registering process it go to login activity
                                                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(loginIntent);
                                                    Toast.makeText(RegisterActivity.this,"Please Verify your email and Login ",Toast.LENGTH_LONG).show();
                                                    finish();
                                                }
                                            }
                                        });

                            }
                            else  Toast.makeText(RegisterActivity.this, "You got some error", Toast.LENGTH_LONG).show();

                        }
                    });

                }
                else {
                    mCreatAccountProgrees.hide();
                    Toast.makeText(RegisterActivity.this, "You got some error", Toast.LENGTH_LONG).show();
                }

            } });
    }

    private void currentLocationCheckAndCall(){
        if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationTask = mFusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                try {
                    mLastLocation = location;
                    if(mLastLocation==null) {
                        Toast.makeText(RegisterActivity.this,"Please turn on your location",Toast.LENGTH_LONG).show();
                        return;
                    }
                    mLastLocation.getLatitude();
                    onClickButtonFunc();
                }catch (Exception e){
                    Toast.makeText(RegisterActivity.this,"Something related to getting your current location went wrong",Toast.LENGTH_LONG).show();
                }
            }
        });
        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this,"Couldn't get your current location",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onClickButtonFunc(){
        String display_namee = mDisplayName.getEditText().getText().toString().trim();
        String email = mEmail.getEditText().getText().toString().trim();
        String password = mPassword.getEditText().getText().toString().trim();

        if (!TextUtils.isEmpty(display_namee) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {

            mCreatAccountProgrees.setTitle("Creating Account");
            mCreatAccountProgrees.setMessage("Please wait a while");
            mCreatAccountProgrees.setCanceledOnTouchOutside(false);
            mCreatAccountProgrees.show();

            register_user(display_namee, email, password);
        }else Toast.makeText(RegisterActivity.this,"Name/e-mail/password cannot be empty",Toast.LENGTH_LONG).show();
    }
/*

                   Shrestha dadar Code
                  //Saving userId in database
                  String userId = mAuth.getCurrentUser().getUid();
                  DatabaseReference currentUseIdRef = FirebaseDatabase.getInstance(databaseAddress).getReference().child("Users").child(userId);
                  currentUseIdRef.setValue(true);
                  currentUseIdRef.child("Name").setValue(mDisplayName.getEditText().getText().toString());

 */
}