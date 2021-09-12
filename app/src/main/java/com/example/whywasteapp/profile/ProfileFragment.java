package com.example.whywasteapp.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.ValueIterator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.whywasteapp.Functions;
import com.example.whywasteapp.MainActivity;
import com.example.whywasteapp.R;
import com.example.whywasteapp.home.FoodAdapter;
import com.example.whywasteapp.login.LoginActivity;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Queue;

import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView btn_userInfo,btn_donates,btn_review;
    TextView txtContact,txtEmail,txtAddress,txtState,txtCity,edit_Contact,edit_Address,userName,txtGender,txtInfo,txtFoodDonate;
    LottieAnimationView noDonates,noReview,LoadingDonate,LoadingReview;
    Spinner State,City;

    RecyclerView recyclerFoodView,recyclerReviewView;

    ProgressDialog progressDialog ;

    TextInputLayout editLMoNo,editLHomeAdd;
    TextInputEditText editContact,editHomeAdd;

    LinearLayout userInfo,donates,review,LayoutCity,LayoutState;
    RelativeLayout LCitySpinner,LStateSpinner;

    String OldContactNo,NewContactNo,HomeAddress,SelectedState,SelectedCity,UserKey;

    ArrayList<DonateModel> donateModels = new ArrayList<DonateModel>();
    DonateAdapter donateAdapter;
    DonateModel donateCard;

    ArrayList<ReviewModel> reviewModels = new ArrayList<ReviewModel>();
    ReviewAdapter reviewAdapter;
    ReviewModel reviewCard;

    SharedPreferences prefrence;

    //Contact_Id && Address_Id [EDIT = 0 || SAVE = 1]
    Integer Conatct_Id=0,Address_Id=0;

    DatabaseReference dbRef,ref,reference,db,dataRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Inflate the layout for this fragment
        btn_userInfo = view.findViewById(R.id.btn_userInfo);
        btn_donates = view.findViewById(R.id.btn_donate);
        btn_review = view.findViewById(R.id.btn_review);
        txtContact = view.findViewById(R.id.txtContact);
        txtEmail = view.findViewById(R.id.txtMail);
        txtGender = view.findViewById(R.id.txtGen);
        txtAddress = view.findViewById(R.id.txtAddress);
        txtInfo = view.findViewById(R.id.txtInfo);
        txtState = view.findViewById(R.id.txtState);
        txtCity = view.findViewById(R.id.txtCity);
        txtFoodDonate = view.findViewById(R.id.FoodDonate);
        State = view.findViewById(R.id.State);
        City = view.findViewById(R.id.City);

        noDonates = view.findViewById(R.id.NoDonations);
        noReview = view.findViewById(R.id.NoReview);
        LoadingDonate = view.findViewById(R.id.loadingDonates);
        LoadingReview = view.findViewById(R.id.loadingReview);

        recyclerFoodView = view.findViewById(R.id.recyclerFoodView);
        recyclerReviewView = view.findViewById(R.id.recyclerReviewView);

        userName = view.findViewById(R.id.Name);
        userInfo = view.findViewById(R.id.userInfo);
        donates = view.findViewById(R.id.donates);
        review = view.findViewById(R.id.review);
        LayoutCity = view.findViewById(R.id.LayoutCity);
        LayoutState = view.findViewById(R.id.LayoutState);
        LCitySpinner = view.findViewById(R.id.LCitySpinner);
        LStateSpinner = view.findViewById(R.id.LStateSpinner);

        edit_Contact = view.findViewById(R.id.edit_Contact);
        edit_Address = view.findViewById(R.id.edit_Address);

        editLMoNo = view.findViewById(R.id.editLMoNo);
        editLHomeAdd = view.findViewById(R.id.editLHomeAdd);
        editContact = view.findViewById(R.id.edit_MobileNo);
        editHomeAdd = view.findViewById(R.id.edit_HomeAdd);

        btn_userInfo.setTextColor(Color.parseColor("#FF3333"));

        UserKey = Functions.getUserID();

       /*
       recyclerFoodView.setLayoutManager(
                new LinearLayoutManager(getContext()));

        prefrence = Functions.getPref(getActivity());
        String tmp = prefrence.getString("Name","");


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Food_Donate").child(UserKey);
        Query queries = ref.orderByChild("donatorName").equalTo(tmp);
        queries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toasty.success(getActivity(),tmp,Toasty.LENGTH_LONG).show();

                    FirebaseRecyclerOptions<DonateModel> options =
                            new FirebaseRecyclerOptions.Builder<DonateModel>()
                                    .setQuery(FirebaseDatabase.getInstance().getReference("Food_Donate").child(UserKey), DonateModel.class)
                                    .build();

                    donateAdapter = new DonateAdapter(options);
                    recyclerFoodView.setAdapter(donateAdapter);
                }
                else{
                    Toasty.success(getActivity(),"Moo",Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */

        try {
            loadUserData();
        }catch (Exception e){
            Toasty.warning(getContext(),"Login to continue use all features..!!!", Snackbar.LENGTH_LONG);
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return null;
        }

        //StateCheck Outside Form Spinner Fill Code
        ArrayList<String> StateCheckList = Functions.getState("state.json",getActivity());
        StateCheckList.add(0,"Select State");
        ArrayAdapter<String> stateCheckAdapter = new ArrayAdapter<String>(getContext(),R.layout.statespinner_layout,R.id.State,StateCheckList);
        State.setAdapter(stateCheckAdapter);
        ArrayList<String> CityCheckList= new ArrayList<String>();
        CityCheckList.add(0,"Select City");
        ArrayAdapter<String> cityCheckAdapter = new ArrayAdapter<String>(getContext(), R.layout.cityspinner_layout, R.id.City, CityCheckList);
        City.setAdapter(cityCheckAdapter);

        State.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (int i=0;i<36;i++)
                {
                    if(position==i+1)
                    {
                        //City Spinner Fill Code
                        ArrayList<String> CityCheckList = Functions.getCity("state.json",i,getActivity());
                        CityCheckList.add(0,"Select City");
                        ArrayAdapter<String> cityCheckAdapter = new ArrayAdapter<String>(getContext(), R.layout.cityspinner_layout, R.id.City, CityCheckList);
                        City.setAdapter(cityCheckAdapter);
                        LayoutCity.setVisibility(View.VISIBLE);
                        LCitySpinner.setVisibility(View.VISIBLE);
                        txtCity.setVisibility(View.GONE);
                        City.performClick();
                    }
                }
                SelectedState = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        City.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectedCity = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        edit_Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Conatct_Id==0){ //Edit
                    OldContactNo = txtContact.getText().toString();
                    int ConLen = OldContactNo.length();
                    OldContactNo = OldContactNo.substring(4,ConLen);
                    txtContact.setVisibility(View.GONE);
                    editLMoNo.setVisibility(View.VISIBLE);
                    editContact.setText(OldContactNo);
                    edit_Contact.setText("Save \uD83D\uDCBE");
                    Conatct_Id = 1;
                }else { //Save
                    NewContactNo = editContact.getText().toString();
                    int ConLen = NewContactNo.length();
                    if (ConLen==0){
                        editContact.setError("Mobile Number Required..!!");
                        editContact.requestFocus();
                        return;
                    }else if (ConLen>0 && ConLen <10){
                        editContact.setError("10 Digits Required..!!");
                        editContact.requestFocus();
                        return;
                    }else {
                        if (OldContactNo.equals(NewContactNo)){
                            Toast.makeText(getContext(), "Mobile number is same..!!!", Toast.LENGTH_SHORT).show();
                            setContactDetails();
                        }else {
                            String UID = Functions.getUserID();
                            dbRef = FirebaseDatabase.getInstance().getReference("Registration").child(UID);
                            dbRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    dbRef.child("MobileNo").setValue(NewContactNo);
                                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Mobile number updated successfully..!!!", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                    setContactDetails();
                                    updatePreference();
                                }

                                private void updatePreference() {
                                    SharedPreferences pref = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("MobNo",NewContactNo);
                                    editor.commit();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }

            private void setContactDetails() {
                txtContact.setText("+91 "+NewContactNo);
                txtContact.setVisibility(View.VISIBLE);
                editLMoNo.setVisibility(View.GONE);
                edit_Contact.setText("\u270E Edit");
                Conatct_Id = 0;
                Functions.KeyboardDown(getActivity());
                editContact.clearFocus();
            }
        });

        edit_Address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Address_Id==0){ //Edit
                    txtInfo.setVisibility(View.GONE);
                    txtAddress.setVisibility(View.GONE);
                    txtState.setVisibility(View.GONE);
                    editLHomeAdd.setVisibility(View.VISIBLE);
                    LayoutState.setVisibility(View.VISIBLE);
                    LayoutCity.setVisibility(View.GONE);
                    LStateSpinner.setVisibility(View.VISIBLE);
                    HomeAddress = txtAddress.getText().toString();
                    editHomeAdd.setText(HomeAddress);
                    edit_Address.setText("Save \uD83D\uDCBE");
                    Address_Id = 1;
                }else { //Save
                    HomeAddress = editHomeAdd.getText().toString();
                    int HomeAddLen = HomeAddress.length();
                    if (TextUtils.isEmpty(HomeAddress)){
                        editHomeAdd.setError("Address Required..!!");
                        editHomeAdd.requestFocus();
                        return;
                    }else if (HomeAddLen>0 && HomeAddLen<6){
                        editHomeAdd.setError("Enter Valid Address..!!");
                        editHomeAdd.requestFocus();
                        return;
                    }else {
                        if (SelectedState.equals("Select State")){
                            Toasty.normal(getActivity(),"Select State To Continue..!!!",Toast.LENGTH_SHORT).show();
                            return;
                        } else{
                            if (SelectedCity.equals("Select City")){
                                Toasty.normal(getActivity(), "Select City To Continue..!!!", Toast.LENGTH_SHORT).show();
                                return;
                            }else {
                                String UID = Functions.getUserID();
                                dbRef = FirebaseDatabase.getInstance().getReference("Registration").child(UID);
                                dbRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        dbRef.child("Address").setValue(HomeAddress);
                                        dbRef.child("City").setValue(SelectedCity);
                                        dbRef.child("State").setValue(SelectedState);
                                        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Address updated successfully..!!!", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        txtAddress.setVisibility(View.VISIBLE);
                                        txtState.setVisibility(View.VISIBLE);
                                        txtCity.setVisibility(View.VISIBLE);
                                        editLHomeAdd.setVisibility(View.GONE);
                                        LStateSpinner.setVisibility(View.GONE);
                                        LCitySpinner.setVisibility(View.GONE);
                                        txtAddress.setText(HomeAddress);
                                        txtState.setText(SelectedState + " - INDIA");
                                        txtCity.setText(SelectedCity);
                                        edit_Address.setText("\u270E Edit");
                                        Address_Id = 0;
                                        //Functions.KeyboardDown(getActivity());
                                        editHomeAdd.clearFocus();
                                        updatePreference();
                                    }

                                    private void updatePreference() {
                                        SharedPreferences pref = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("Address", HomeAddress);
                                        editor.putString("City", SelectedCity);
                                        editor.putString("State", SelectedState);
                                        editor.commit();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                }
            }
        });

        btn_userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_userInfo.setTextColor(Color.parseColor("#FF3333"));
                btn_donates.setTextColor(Color.GRAY);
                btn_review.setTextColor(Color.GRAY);
                donates.setVisibility(View.GONE);
                review.setVisibility(View.GONE);
                userInfo.setVisibility(View.VISIBLE);
                recyclerReviewView.setVisibility(View.GONE);
                recyclerFoodView.setVisibility(View.GONE);
            }
        });

        btn_donates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_donates.setTextColor(Color.parseColor("#FF3333"));
                btn_userInfo.setTextColor(Color.GRAY);
                btn_review.setTextColor(Color.GRAY);
                userInfo.setVisibility(View.GONE);
                review.setVisibility(View.GONE);
                donates.setVisibility(View.VISIBLE);
                recyclerFoodView.setVisibility(View.GONE);
                recyclerReviewView.setVisibility(View.GONE);

                donateModels = getMyDonateList();
                recyclerFoodView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerFoodView.setHasFixedSize(true);

                donateAdapter = new DonateAdapter(donateModels, getActivity());
                recyclerFoodView.setAdapter(donateAdapter);

                /*
                donateModels = getMyDonateList();
                recyclerFoodView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerFoodView.setHasFixedSize(true);

                donateAdapter = new DonateAdapter(donateModels, getActivity());
                recyclerFoodView.setAdapter(donateAdapter);
                */

                android.os.Handler handler = new Handler();
                int delay = 3400; //1000 milliseconds == 1 second

                //progressDialog = new ProgressDialog(getActivity());
                //progressDialog = ProgressDialog.show(getActivity(),"Please Wait","While We Are Fetching Data");
                LoadingDonate.setVisibility(View.VISIBLE);

                handler.postDelayed(new Runnable() {
                    public void run() {
                        //progressDialog.dismiss();
                        LoadingDonate.setVisibility(View.GONE);
                        // Do your work here
                        if (donateModels.size()>0) {
                            //Sorting
                            Collections.sort(donateModels, new Comparator<DonateModel>() {
                                @Override
                                public int compare(DonateModel obj1, DonateModel obj2) {
                                    return obj1.getDate().compareToIgnoreCase(obj2.getDate());
                                }
                            });

                            donateModels.add(donateCard);
                            donateAdapter = new DonateAdapter(donateModels, getActivity());
                            recyclerFoodView.setVisibility(View.VISIBLE);
                            recyclerFoodView.setAdapter(donateAdapter);
                            donateAdapter.notifyDataSetChanged();
                            //                            donateModels.add(donateCard);
                            //                            donateAdapter = new DonateAdapter(donateModels, getActivity());
                            //                            recyclerFoodView.setAdapter(donateAdapter);
                            //                            donateAdapter.notifyDataSetChanged();
                        }
                    }
                }, delay);



            }
        });

        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_review.setTextColor(Color.parseColor("#FF3333"));
                btn_userInfo.setTextColor(Color.GRAY);
                btn_donates.setTextColor(Color.GRAY);
                donates.setVisibility(View.GONE);
                userInfo.setVisibility(View.GONE);
                review.setVisibility(View.VISIBLE);
                recyclerFoodView.setVisibility(View.GONE);
                recyclerReviewView.setVisibility(View.GONE);

                reviewModels = getMyReviewList();
                recyclerFoodView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerFoodView.setHasFixedSize(true);

                reviewAdapter = new ReviewAdapter(reviewModels,getActivity());
                recyclerReviewView.setAdapter(reviewAdapter);

                android.os.Handler handler = new Handler();
                int delay = 1800; //1000 milliseconds == 1 second

//                progressDialog = new ProgressDialog(getActivity());
//                progressDialog = ProgressDialog.show(getActivity(),"Please Wait","While We Are Fetching Data");
                LoadingReview.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        //progressDialog.dismiss();
                        LoadingReview.setVisibility(View.GONE);
                        // Do your work here
                        if (reviewModels.size()>0) {
//                            reviewModels.add(reviewCard);
//                            reviewAdapter = new ReviewAdapter(reviewModels, getActivity());
//                            recyclerReviewView.setAdapter(reviewAdapter);
//                            reviewAdapter.notifyDataSetChanged();
                            recyclerReviewView.setVisibility(View.GONE);
                            noReview.setVisibility(View.VISIBLE);
                        }else {
                            noReview.setVisibility(View.VISIBLE);
                        }
                    }
                }, delay);
            }
        });

        return view;
    }

    private ArrayList<DonateModel> getMyDonateList() {
        ArrayList<DonateModel> foodModels = new ArrayList<>();
        foodModels.clear();

        dbRef = FirebaseDatabase.getInstance().getReference("Food_Donate");
        String UID = Functions.getUserID();
        DatabaseReference db = dbRef.child(UID);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    recyclerFoodView.setVisibility(View.VISIBLE);
                    noDonates.setVisibility(View.GONE);
                    for (DataSnapshot data:snapshot.getChildren()){
                        String FoodName = data.child("foodName").getValue().toString();
                        String FoodQty = data.child("foodQty").getValue().toString();
                        String FoodImg = data.child("imageName").getValue().toString();
                        String DonateDate = data.child("date").getValue().toString();
                        String DonateTime = data.child("time").getValue().toString();
                        donateCard = new DonateModel();
                        donateCard.setFoodName(FoodName);
                        donateCard.setFoodQty(FoodQty);
                        donateCard.setImageName(FoodImg);
                        String Date = DonateDate +" At "+DonateTime;
                        donateCard.setDate(Date);
                        foodModels.add(donateCard);
                    }
                }else {
                    recyclerFoodView.setVisibility(View.GONE);
                    noDonates.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return foodModels;

//        ArrayList<DonateModel> foodModels = new ArrayList<>();
//        ArrayList<ReviewModel> reviewModels = new ArrayList<>();
//        reviewModels.clear();
//        UserKey = Functions.getUserID();
//
//        dbRef = FirebaseDatabase.getInstance().getReference("Food_Donate");
//        dbRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot:snapshot.getChildren()){   //dataSnapshot (UserKey)
//
//                    String Key = dataSnapshot.getKey();
//                    ref = dbRef.child(Key);
//                    ref.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snap) {
//
//                            for (DataSnapshot dataSnap:snap.getChildren()){   //dataSnap (Donate Node Number)
//                                String DonateKey = dataSnap.getKey();
//                                reference = FirebaseDatabase.getInstance().getReference("Food_Donate").child(Key).child(DonateKey);
//                                reference.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot ss) {
//
//                                        String ImgUrl = ss.child("imageName").getValue().toString();
//                                        db = FirebaseDatabase.getInstance().getReference("Food_Donate").child(Key).child(DonateKey).child("Review");
//                                        db.addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot snapss) {
//                                                for (DataSnapshot dataShot:snapss.getChildren()){
//
//                                                    String ReviewKey = dataShot.getKey();
//                                                    dataRef = FirebaseDatabase.getInstance().getReference("Food_Donate").child(Key).child(DonateKey).child("Review").child(ReviewKey);
//                                                    dataRef.addValueEventListener(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot ssShot) {
//                                                            String ReviewKey = ssShot.getKey();
//                                                            if (UserKey.equals(ReviewKey)){
//                                                                String mydemo= "asdasdas";
//                                                                String reviewww = "dasdasd";
//                                                                String Review = ssShot.child("review").getValue().toString();
//                                                                //     Toast.makeText(getActivity(), Review, Toast.LENGTH_LONG).show();
//
//                                                                donateCard = new DonateModel();
//                                                                donateCard.setFoodName(Review);
//                                                                donateCard.setDonatorName(ImgUrl);
//                                                                donateCard.setFoodImg("FoodImg");
//                                                                donateCard.setDate("Date");
//                                                                foodModels.add(donateCard);
//                                                                Toast.makeText(getActivity(), Review +" " + ImgUrl, Toast.LENGTH_SHORT).show();
//
//
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError error) {
//                                                            Log.d("Error1",error.toString());
//                                                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    });
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//                                                Log.d("Error1",error.toString());
//                                                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//                                        Log.d("Error1",error.toString());
//                                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Log.d("Error1",error.toString());
//                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d("Error1",error.toString());
//                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        return donateModels;

    }

    private ArrayList<ReviewModel> getMyReviewList() {
        ArrayList<ReviewModel> reviewModels = new ArrayList<>();
        reviewModels.clear();

        dbRef = FirebaseDatabase.getInstance().getReference("Food_Donate");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){   //dataSnapshot (UserKey)

                    String Key = dataSnapshot.getKey();
                    ref = dbRef.child(Key);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {

                            for (DataSnapshot dataSnap:snap.getChildren()){   //dataSnap (Donate Node Number)
                                String DonateKey = dataSnap.getKey();
                                reference = FirebaseDatabase.getInstance().getReference("Food_Donate").child(Key).child(DonateKey);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot ss) {

                                        String ImgUrl = ss.child("imageName").getValue().toString();
                                        db = FirebaseDatabase.getInstance().getReference("Food_Donate").child(Key).child(DonateKey).child("Review");
                                        db.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapss) {
                                                for (DataSnapshot dataShot:snapss.getChildren()){

                                                    String ReviewKey = dataShot.getKey();
                                                    dataRef = FirebaseDatabase.getInstance().getReference("Food_Donate").child(Key).child(DonateKey).child("Review").child(ReviewKey);
                                                    dataRef.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot ssShot) {
                                                            if (ssShot.exists()){
                                                                recyclerReviewView.setVisibility(View.VISIBLE);
                                                                String ReviewKey = ssShot.getKey();
                                                                if (UserKey.equals(ReviewKey)){
                                                                    String Review = ssShot.child("review").getValue().toString();

                                                                    Log.d("Review",Review);
                                                                    Log.d("FoodImg",ImgUrl);

                                                                    reviewCard = new ReviewModel();
                                                                    reviewCard.setReview(Review);
                                                                    reviewCard.setFoodImg(ImgUrl);
                                                                    reviewModels.add(reviewCard);
                                                                }
                                                            }else {
                                                                recyclerReviewView.setVisibility(View.GONE);
                                                                noReview.setVisibility(View.VISIBLE);
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Log.d("Error1",error.toString());
                                                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.d("Error1",error.toString());
                                                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.d("Error1",error.toString());
                                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("Error1",error.toString());
                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error1",error.toString());
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return reviewModels;
    }

    private void loadUserData() {
        String User = Functions.getUserID();
        dataRef = FirebaseDatabase.getInstance().getReference("Food_Donate").child(User);
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int Data = (int) snapshot.getChildrenCount();
                    txtFoodDonate.setText(String.valueOf(Data));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        prefrence = Functions.getPref(getActivity());
        if (Functions.NAME.contains(" ")){
            String[] strArray = Functions.NAME.split(" ");
            StringBuilder builder = new StringBuilder();
            for (String s : strArray) {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                builder.append(cap + " ");
            }
            userName.setText(builder.toString());
        }else {
            String tmp = prefrence.getString("Name","");
            String Cap = tmp.substring(0,1).toUpperCase() + tmp.substring(1);
            userName.setText(Cap);
        }

        txtContact.setText("+91 "+Functions.MOBILE);
        txtEmail.setText(Functions.EMAIL);
        txtGender.setText(Functions.GENDER);

        if (TextUtils.isEmpty(Functions.ADDRESS)) {
            txtInfo.setVisibility(View.VISIBLE);
        }else {
            txtAddress.setVisibility(View.VISIBLE);
            txtAddress.setText(Functions.ADDRESS);
        }
        if (TextUtils.isEmpty(Functions.CITY)) {
            LayoutCity.setVisibility(View.GONE);
        }else {
            LayoutCity.setVisibility(View.VISIBLE);
            txtCity.setText(Functions.CITY);
        }
        if (TextUtils.isEmpty(Functions.STATE)) {
            LayoutState.setVisibility(View.GONE);
        }else {
            LayoutState.setVisibility(View.VISIBLE);
            txtState.setText(Functions.STATE+" - INDIA");
        }

    }


    /*@Override
    public void onStart() {
        try {
            super.onStart();
            donateAdapter.startListening();
        }catch (Exception e){
            noDonates.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        try {
            super.onStop();
            donateAdapter.stopListening();
        }catch (Exception e){
            noDonates.setVisibility(View.GONE);
        }
    }*/

}