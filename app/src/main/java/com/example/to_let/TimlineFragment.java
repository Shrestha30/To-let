package com.example.to_let;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimlineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimlineFragment extends Fragment {

    public static final String TAG = TimlineFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    final String databaseAddress = "https://to-let-8cd6d-default-rtdb.asia-southeast1.firebasedatabase.app";
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance(databaseAddress).getReference()
            .child("Users");//.child(userId).child("Post");
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    ArrayList<TimeLineDataModel>DataHolder;
    public String Namex;
    User userinfo;

    public TimlineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimlineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimlineFragment newInstance(String param1, String param2) {
        TimlineFragment fragment = new TimlineFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_timline, container, false);
        recyclerView=view.findViewById(R.id.post_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               // for (DataSnapshot snap : snapshot.getChildren()) {
                 //   Log.d(TAG, "onDataChange: " + snap.getValue(Post.class));
                //}
                Post post=snapshot.child(userId).getValue(Post.class);
                Log.d(TAG, "onDataChange: "+post);
                userinfo=snapshot.child(userId).child("profile").getValue(User.class);
                Log.d(TAG,"onDataChange: " +snapshot.child(userId).child("profile").getValue(User.class));
                Log.d(TAG,"onDataChange: " +userinfo.getName().toString());


                //just checking Logcat
                Namex=userinfo.getName().toString();
                Log.d(TAG,"Namex: " +Namex);

                DataHolder=new ArrayList<>();

                //Here post details are given manually
                //Here should do retrieve the post details when user do post for renting house
                //but for some difficulties it's given manually

                TimeLineDataModel ob1=new TimeLineDataModel(R.drawable.default_avater,"Sujoy Kurmar","HouseName: Sujoy Tower\n" +
                        "Floor: 5th-A\n" +
                        "Location: 100/B bondor road, Lamabajar, sylhet\n" +
                        "Rent: 20k taka\n" +
                        "Type: To-let\n" +
                        "Extra Details: the house has 2 bedroom, 1 kitchen, 1 dining space, \n" +
                        "it hase parking fascility, \n" +
                        "and it for bachelor or family(prefered)."
                        + "\n Phone: 0123456789\n" +"\n email: abc@gmail.com\n");
                DataHolder.add(ob1);
                TimeLineDataModel ob2=new TimeLineDataModel(R.drawable.default_avater,"Shrestha Datta", "\n" +
                        "HouseName: Shrestha Garden\n" +
                        "Floor: 7th-A\n" +
                        "Location: 100/B house 20, Uttora 3.\n" +
                        "Rent: 20k taka\n" +
                        "Type: To-let\n" +
                        "Extra Details: the house has 3 bedroom, 1 kitchen, 1 dining space, \n" +
                        "it hase parking fascility, \n" +
                        "and it for bachelor or family(prefered).\n" + "\n Phone: 0123456789\n" +"\n email: abc@gmail.com\n");
                DataHolder.add(ob2);

                TimeLineDataModel ob3=new TimeLineDataModel(R.drawable.default_avater,userinfo.getName(),"HouseName: Sumon Complex\n" +
                        "Floor: 4th-A\n" +
                        "Location: 3/A Medical road, House 42, Modina Market, Jalalabad, Sylhet\n" +
                        "Rent: 16k taka\n" +
                        "Type: To-let\n" +
                        "Extra Details: the house has 3 bedroom, 1 kitchen, 1 dining space, \n" +
                        "it hase parking fascility, \n" +
                        "and it only for family\n"+
                        "\n Phone: 0123456789\n" +"\n email: abc@gmail.com\n");
                DataHolder.add(ob3);

                recyclerView.setAdapter(new TimelineAdapter(DataHolder));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: failure");
            }
        });

        //Namex=userinfo.getName().toString();

        Log.d(TAG,"hello : "+Namex);
        

        return view;
    }



}