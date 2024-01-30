package com.example.to_let;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.zip.Inflater;

       // this section is creating post for renting house

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Create_TOLET#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Create_TOLET extends Fragment {

    public static final String TAG = Create_TOLET.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    final String databaseAddress = "https://to-let-8cd6d-default-rtdb.asia-southeast1.firebasedatabase.app";
    private DatabaseReference mDatabaseReferance;
    private Button mCreatePostBtn;
    private TextInputLayout mPostDetails;

    private ChangeFragmentListener listener;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Create_TOLET() {
        // Required empty public constructor
        //  mCreatePostBtn=(Button) findViewById(R.id.create_post_btn);
        //  mCreatePostBtn=(Button) findViewById(R.id.eg_creat_btn);

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Create_TOLET.
     */
    // TODO: Rename and change types and number of parameters
    public static Create_TOLET newInstance(String param1, String param2) {
        Create_TOLET fragment = new Create_TOLET();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        listener = (ChangeFragmentListener) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create__t_o_l_e_t, container, false);
        mPostDetails = view.findViewById(R.id.post_details_InputTxt);
        mCreatePostBtn = view.findViewById(R.id.post_creat_btn);

        mCreatePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String userId = currentUser.getUid();

                DatabaseReference dbRef = FirebaseDatabase.getInstance(databaseAddress).getReference()
                        .child("Users").child(userId).child("Post");
                  ///Storing post details in database
                Post post = new Post(dbRef.push().getKey(), mPostDetails.getEditText().getText().toString());

                dbRef.child(post.getId())
                        .setValue(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "onSuccess: Kam hoiche");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Kam hoi nai");
                            }
                        });

                listener.changeFragment();
            }
        });


        // return inflater.inflate(R.layout.fragment_create__t_o_l_e_t, container, false);

        return view;
    }
}