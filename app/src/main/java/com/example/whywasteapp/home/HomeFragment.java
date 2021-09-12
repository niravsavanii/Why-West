package com.example.whywasteapp.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.whywasteapp.Functions;
import com.example.whywasteapp.R;
import com.example.whywasteapp.donate.DonateMoneyActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageSlider sliderView;
    LottieAnimationView Loading;

    //Database variable
    DatabaseReference dbRef;

    RecyclerView recyclerView; //,recyclerViewVertical;
    FoodAdapter foodAdapter;

    ArrayList<FoodModel> foodModels = new ArrayList<FoodModel>();

    ProgressDialog progressDialog ;
    FoodModel foodCard;

    TextView ViewMore;
    ImageView covidInfo,GoTo_DonateMoney;

    LinearLayout lblDonation;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Why Waste Foods");

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        Functions.getPref(getActivity());

        sliderView = (ImageSlider) view.findViewById(R.id.imageSlider);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        // recyclerViewVertical = (RecyclerView)view.findViewById(R.id.recyclerViewVertical);
        lblDonation = (LinearLayout)view.findViewById(R.id.lblDonations);

        covidInfo = view.findViewById(R.id.covidInfo);
        GoTo_DonateMoney = view.findViewById(R.id.GoTo_DonateMoney);
        Loading = view.findViewById(R.id.Loading);

        ViewMore = (TextView)view.findViewById(R.id.ViewMore);

        loadSliderImages(view);

        //Set Gradient COLOR
//        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffd4cbe5, 0xffd4cbe5});
//        GradientDrawable gradient2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffd4cbe5, 0xffd4cbe5});
//        GradientDrawable gradient1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xff7adccf, 0xff7adccf});
//        GradientDrawable gradient3 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xfff7c59f, 0xFFf7c59f});
//        GradientDrawable gradient4 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffb8d7f5, 0xFFf7c59f});

        //view.setBackground(gradient4);

        //Food Code
        foodModels = getMyDonatedFoodList();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);

        foodAdapter = new FoodAdapter(foodModels, getActivity());
        recyclerView.setAdapter(foodAdapter);

        android.os.Handler handler = new Handler();
        int delay = 4000; // 1000 milliseconds == 1 second

        try {
            //progressDialog = new ProgressDialog(getActivity());
            //progressDialog = ProgressDialog.show(getActivity(),"Please Wait","While We Are Fetching Data");
            //recyclerView.setVisibility(View.GONE);
            Loading.setVisibility(View.VISIBLE);
            handler.postDelayed(new Runnable() {
                public void run() {
                    //progressDialog.dismiss();
                    Loading.setVisibility(View.GONE);
              //      recyclerView.setVisibility(View.VISIBLE);
                    // Do your work here
                    if (foodModels.size()>0) {
                        foodModels.add(foodCard);
                        foodAdapter = new FoodAdapter(foodModels, getActivity());
                        recyclerView.setAdapter(foodAdapter);
                    }
                }
            }, delay);
        }catch (Exception e){

        }

        covidInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new CovidInfoFragment()).commit();
                getActivity().overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);

            }
        });

        ViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(getActivity(),"No More Donation Founds..!!!",Toasty.LENGTH_LONG).show();
                /*
                    sliderView.setVisibility(View.GONE);
                    lblDonation.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    recyclerViewVertical.setVisibility(View.VISIBLE);

                    //Food Code
                    foodModels = getMyDonatedFoodList();
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
                    recyclerView.setHasFixedSize(true);

                    foodAdapter = new FoodAdapter(foodModels, getActivity());
                    recyclerView.setAdapter(foodAdapter);

                    android.os.Handler handler = new Handler();
                    int delay = 3000; // 1000 milliseconds == 1 second

                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog = ProgressDialog.show(getActivity(),"Please Wait","While We Are Fetching Data");
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            // Do your work here
                            if (foodModels.size()>0) {
                                foodModels.add(foodCard);
                                foodAdapter = new FoodAdapter(foodModels, getActivity());
                                recyclerView.setAdapter(foodAdapter);
                            }
                        }
                    }, delay);
                */
            }
        });

        GoTo_DonateMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DonateMoneyActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
            }
        });

        return view;
    }

    private void loadSliderImages(View view) {

        List<SlideModel> slideModels = new ArrayList<>();

        dbRef = FirebaseDatabase.getInstance().getReference("Admin").child("Images");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    String URL = dataSnapshot.getValue().toString();
                    slideModels.add(new SlideModel(URL));
                }
                sliderView.setImageList(slideModels,true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private ArrayList<FoodModel> getMyDonatedFoodList() {
        ArrayList<FoodModel> foodModels = new ArrayList<>();
        foodModels.clear();

        dbRef = FirebaseDatabase.getInstance().getReference("Food_Donate");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String UserKey = dataSnapshot.getKey();
                    DatabaseReference dbKey = dbRef.child(UserKey);
                    dbKey.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            for (DataSnapshot dataSnap:snap.getChildren()){
                                String DonationNode = dataSnap.getKey();
                                DatabaseReference dbNode = dbKey.child(DonationNode);
                                dbNode.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot ss) {
                                        foodCard = new FoodModel();
                                        foodCard.setUserKey(UserKey);
                                        foodCard.setDonatorNode(DonationNode);
                                        String FoodName = ss.child("foodName").getValue().toString();
                                        String Image = ss.child("imageName").getValue().toString();
                                        String DonatorName = ss.child("donatorName").getValue().toString();
                                        String date = ss.child("date").getValue().toString();
                                        String time = ss.child("time").getValue().toString();
                                        String Time = "Food Donated On "+date+" At "+time;
                                        foodCard.setFoodName(FoodName);
                                        foodCard.setDonatorName(DonatorName);
                                        foodCard.setFoodImage(Image);
                                        foodCard.setDonationTime(Time);
                                        foodModels.add(foodCard);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return foodModels;
    }
}