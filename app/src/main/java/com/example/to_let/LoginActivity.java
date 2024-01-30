package com.example.to_let;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mLoginBtn;
    private ProgressDialog mLoginProgress;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private FirebaseAuth mAuth;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mLoginProgress= new ProgressDialog(this);

        mToolbar =(Toolbar) findViewById(R.id.login_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mEmail=(TextInputLayout) findViewById(R.id.login_email);
        mPassword=(TextInputLayout) findViewById(R.id.login_password);
        mLoginBtn=(Button) findViewById(R.id.login_button);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=mEmail.getEditText().getText().toString();
                String password=mPassword.getEditText().getText().toString();
                  //checking the text file are empty or not?
                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password))
                {
                    mLoginProgress.setTitle("Logging in");
                    mLoginProgress.setMessage("Please Wait a while");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    loginUser(email,password);
                }


            }

        });

    }
      private void loginUser(String email, String password) {
         //Email verification here
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                 //if email are valid and get verification from email then it go to main page otherwise it will give error toast
                if(task.isSuccessful())
                {
                    mLoginProgress.dismiss();
                    if(mAuth.getCurrentUser().isEmailVerified())
                    {
                        Intent mainIntent= new Intent(LoginActivity.this,MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    }
                    else
                        Toast.makeText(LoginActivity.this,"Please Verify your email first",Toast.LENGTH_LONG).show();

                }
                else
                {
                    mLoginProgress.hide();
                   Toast.makeText(LoginActivity.this,"Cannot Sign in, Please try again",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    /*
    private void loginUser(String email, String password) {

         mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {

                 if(task.isSuccessful())
                 {
                     mLoginProgress.dismiss();
                     Intent mainIntent= new Intent(LoginActivity.this,MainActivity.class);
                     startActivity(mainIntent);
                     finish();
                 }
                 else{
                     mLoginProgress.hide();
                     Toast.makeText(LoginActivity.this,"Cannot Sign in, Please try again",Toast.LENGTH_LONG).show();
                 }

             }
         });
    }*/

}