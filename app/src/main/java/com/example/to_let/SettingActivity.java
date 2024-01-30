package com.example.to_let;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    public static final String TAG = SettingActivity.class.getSimpleName();

     private DatabaseReference mUserDatabase;
     private FirebaseUser mCurrentUser;
    //Database address
    final String databaseAddress = "https://to-let-8cd6d-default-rtdb.asia-southeast1.firebasedatabase.app";
    //mDisplayimag is For the Profile picture
    // CircleImageView is another library code , source hdodenhof/CircleImageView
     private CircleImageView mDisplayimag;
     private TextView mName;
     private TextView mStatus;
     private Toolbar mToolbar;
     private Button updateBtn;
     FirebaseStorage F_storage;
     FirebaseAuth auth;
     FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        F_storage=FirebaseStorage.getInstance();

        mDisplayimag=(CircleImageView) findViewById(R.id.profile_picture);
        mName=(TextView) findViewById(R.id.setting_display_name);
        mStatus=(TextView) findViewById(R.id.account_status);
        updateBtn=(Button) findViewById(R.id.update_btn);

        ///toobar here
        mToolbar= (Toolbar) findViewById(R.id.account_settingToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         //Glide is another external resource
        Log.d(TAG, "onCreate: "+auth.getCurrentUser().getPhotoUrl());
        Glide.with(this)
                .load(auth.getCurrentUser().getPhotoUrl())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(600, 600)
                .into(mDisplayimag);

         //image click
        mDisplayimag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,33);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


       // mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String Curren_uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserDatabase= FirebaseDatabase.getInstance(databaseAddress).getReference().child("Users").child(Curren_uid).child("profile");
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                Log.d(TAG, "onDataChange: "+user.toString());

                mName.setText(user.getName());
                mStatus.setText(user.getStatus());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    //For getting Image extension
    public String getExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }
    private void upDateUserProfile(String imageUri) {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(imageUri))
                    .build();

            user.updateProfile(request)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "onSuccess: kam hoiche");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: kam hoi nai baal");
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData() != null){
            Uri profileUri=data.getData();
            FirebaseUser user = auth.getCurrentUser();

            Log.d(TAG, "onActivityResult: "+user.getPhotoUrl());

                mDisplayimag.setImageURI(profileUri);
            final StorageReference reference= F_storage.getReference().child("profilePics")
                    .child(System.currentTimeMillis()+"."+getExtension(profileUri));

            reference.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SettingActivity.this,"Uploaded",Toast.LENGTH_LONG).show();

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());

                    String imageUri = uriTask.getResult().toString();
                    Log.d(TAG, "onSuccess: "+imageUri);
                    upDateUserProfile(imageUri);
                }
            });
        }
    }
}