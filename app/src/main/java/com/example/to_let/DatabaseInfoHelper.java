/*
DatabaseInfoHelper.java and its class DatabaseInfoHelper always keeps updated database snapshots.
Its instance is created at the start of the application and is stored in the member
mDatabaseInfoHelper of HouseInfo class.
So, it can be easily accessed at anytime and be used to get updated snapshots of database
 */

package com.example.to_let;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatabaseInfoHelper {
    private String HttpAddress = HouseInfo.databaseAddress;
    DatabaseReference mDatabaseReference;

    //private Context mFragmentActivity;
    private DataSnapshot mDataSnapshot;
    private boolean isAccessed = false;
    //private Thread mThread = Thread.currentThread();

    public DatabaseInfoHelper(/*Context fragmentActivity*/){
        //mFragmentActivity = fragmentActivity;
        mDatabaseReference = FirebaseDatabase.getInstance(HouseInfo.databaseAddress).getReference().child("Users");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //always updating mDataSnapshot
                mDataSnapshot = snapshot;
                //System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                //System.out.println(snapshot.getValue().toString());
                isAccessed = true;
                //Toast.makeText(mFragmentActivity,"Successfully accessed data",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(mFragmentActivity, "Database Info retrieve failed!", Toast.LENGTH_LONG).show();
                isAccessed=false;
            }
        });
    }

    public String getUserName(String userId){
        if(mDataSnapshot.exists()&&mDataSnapshot.getChildrenCount()>0) return mDataSnapshot.child(userId).child("profile").child("name").getValue().toString();
        return "";
    }

    public String getUserPhone(String userId){
        if(mDataSnapshot.child("Phone").exists()&&mDataSnapshot.getChildrenCount()>0) return mDataSnapshot.child(userId).child("profile").child("Phone").getValue().toString();
        return "";
    }

    public String getHouseAddress(String userId, String houseName){
        if(mDataSnapshot.exists()&&mDataSnapshot.getChildrenCount()>0&&mDataSnapshot.child(userId).child("Houses").child(houseName).exists()
                &&mDataSnapshot.child(userId).child("Houses").child(houseName).getChildrenCount()>0)
            return mDataSnapshot.child(userId).child("Houses").
                    child(houseName).child("StreetAddress").getValue().toString();
        return "";
    }

    public String getHouseDetail(String userId, String houseName){
        if(mDataSnapshot.exists()&&mDataSnapshot.getChildrenCount()>0&&mDataSnapshot.child(userId).child("Houses").child(houseName).exists()
                &&mDataSnapshot.child(userId).child("Houses").child(houseName).getChildrenCount()>0)
            return mDataSnapshot.child(userId).child("Houses").
                    child(houseName).child("Detail").getValue().toString();
        return "";
    }

    public House getHouse(String userId,String houseName){

        if(mDataSnapshot.exists()&&mDataSnapshot.getChildrenCount()>0&&mDataSnapshot.child(userId).child("Houses").child(houseName).exists()
                &&mDataSnapshot.child(userId).child("Houses").child(houseName).getChildrenCount()>0)
            return mDataSnapshot.child(userId).child("Houses").child(houseName).getValue(House.class);
        return new House();
    }

    public User getUser(String userId){
        if(mDataSnapshot.exists()&&mDataSnapshot.getChildrenCount()>0&&mDataSnapshot.child(userId).child("profile").exists()
                &&mDataSnapshot.child(userId).child("profile").getChildrenCount()>0)
            return mDataSnapshot.child(userId).child("profile").getValue(User.class);
        return new User();
    }
}
