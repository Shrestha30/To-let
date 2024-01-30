package com.example.to_let;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
//import android.widget.Toolbar;
//import android.support.v7.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity implements ChangeFragmentListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private Button mAddBtn;
    private Button mSearchBtn;
    private ViewPager mViewPager;
    private SectionPagerAddapter mSectionPagerAddapter;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creating instance of DatabaseInfoHelper class to get continuously updated data snapshot
        HouseInfo.mDatabaseInfohelper = new DatabaseInfoHelper();

        mAuth = FirebaseAuth.getInstance();
        mToolbar= findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("TO-LET");


        ////tabs
        mViewPager=(ViewPager) findViewById(R.id.main_tap_pager) ;
        mSectionPagerAddapter = new SectionPagerAddapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPagerAddapter);

        mTabLayout=(TabLayout) findViewById(R.id.main_page_tabs);
        mTabLayout.setupWithViewPager(mViewPager);


        //button for add new house
        mAddBtn = (Button) findViewById(R.id.add);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this,MapsAddHouseActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Button to search House
        mSearchBtn = (Button) findViewById(R.id.search);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this,MapsSearchHouseActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
         //currentUser=null;
        //updateUI(currentUser);

        if(currentUser==null)
        {
            sentToStart();
        }
        //below segment code do apps crash
        //That why this segment are commented
    /*
        if(!(mAuth.getCurrentUser().isEmailVerified())){
            sentToStart();
        }
    */


    }
     //go to staring page
    private void sentToStart()
    {
        Intent startIntent= new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    //Menu options
    public boolean onCreateOptionsMenu(Menu menu) {
       super.onCreateOptionsMenu(menu);
       getMenuInflater().inflate(R.menu.main_manu,menu);

       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);
         //sing out option menu item here
         if(item.getItemId() == R.id.main_logout_btn){
             FirebaseAuth.getInstance().signOut();
             sentToStart();
         }
           //account setting menu item here
         if(item.getItemId() ==R.id.main_account_setting){
             Intent SettingIntent = new Intent(MainActivity.this,SettingActivity.class);
             startActivity(SettingIntent);
         }

         return true;
    }


    @Override
    public void changeFragment() {
        mViewPager.setCurrentItem(1);
    }
}