package com.example.whywasteapp.addarea;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.whywasteapp.Functions;
import com.example.whywasteapp.MainActivity;
import com.example.whywasteapp.R;
import com.example.whywasteapp.donate.DonateMoneyActivity;
import com.example.whywasteapp.login.LoginActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddAreaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAreaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DatabaseReference dbRef,db;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser User =  firebaseAuth.getCurrentUser();
    String UserKey;

    LinearLayout CheckArea,PovertyArea;
    TextInputEditText StreetAddress,AreaAddress,LandmarkAddress,PincodeOfArea;
    Button AddPlace,CheckPlace,CheckAnotherPlace,PlaceNotInList;
    Spinner StateCheck,CityCheck,State,City;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;

    LottieAnimationView anim_addarea;

    ArrayList<PlaceModel> placeModels;
    PlaceCheckAdapter placeCheckAdapter;
    PlaceModel card;

    String Street,Area,Landmark,Pincode,SelectedCheckState=null,SelectedCheckCity=null;
    Integer tmp;
    SimpleDateFormat Date = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat Time = new SimpleDateFormat("HH:mm:ss");

    public AddAreaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddAreaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddAreaFragment newInstance(String param1, String param2) {
        AddAreaFragment fragment = new AddAreaFragment();
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add Poverty Area");
        View view = inflater.inflate(R.layout.fragment_add_area, container, false);

        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.recyclerView);
        CheckArea = view.findViewById(R.id.CheckArea);
        PovertyArea = view.findViewById(R.id.PovertyAreaList);
        StreetAddress = view.findViewById(R.id.Street);
        AreaAddress = view.findViewById(R.id.Area);
        LandmarkAddress = view.findViewById(R.id.Landmark);
        StateCheck = view.findViewById(R.id.StateCheck);
        CityCheck = view.findViewById(R.id.CityCheck);
        State = view.findViewById(R.id.State);
        City = view.findViewById(R.id.City);
        PincodeOfArea = view.findViewById(R.id.Pincode);
        CheckPlace = view.findViewById(R.id.CheckPlace);
        CheckAnotherPlace = view.findViewById(R.id.CheckAnotherPlace);
        PlaceNotInList = view.findViewById(R.id.PlaceNotInList);
        AddPlace = view.findViewById(R.id.AddPovertyArea);

        anim_addarea = view.findViewById(R.id.anim_addarea);
        try {
            UserKey = User.getUid();
        }catch (Exception e){
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Login to continue use all features..!!!", Snackbar.LENGTH_LONG);
            snackbar.setAction("Login", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            snackbar.show();
        }


        //StateCheck Outside Form Spinner Fill Code
        ArrayList<String> StateCheckList = Functions.getState("state.json", getActivity());
        StateCheckList.add(0,"Select State");
        ArrayAdapter<String> stateCheckAdapter = new ArrayAdapter<String>(getContext(),R.layout.statespinner_layout,R.id.State,StateCheckList);
        StateCheck.setAdapter(stateCheckAdapter);
        ArrayList<String> CityCheckList= new ArrayList<String>();
        CityCheckList.add(0,"Select City");
        ArrayAdapter<String> cityCheckAdapter = new ArrayAdapter<String>(getContext(), R.layout.cityspinner_layout, R.id.City, CityCheckList);
        CityCheck.setAdapter(cityCheckAdapter);

        StateCheck.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                        CityCheck.setAdapter(cityCheckAdapter);
                        CityCheck.performClick();
                    }
                }
                SelectedCheckState = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CityCheck.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectedCheckCity = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CheckPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean bolState = checkStateSpinner(SelectedCheckState);
                if (bolState){
                    boolean bolCity = checkCitySpinner(SelectedCheckState);
                    if (bolCity){
                        placeModels = getMyList();
                        android.os.Handler handler = new Handler();
                        int delay = 1000; // 1000 milliseconds == 1 second

                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Please Wait..");
                        progressDialog.show();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                if (placeModels.size()>0) {
                                    //Assigning Place List Adpter
                                    placeCheckAdapter = new PlaceCheckAdapter(placeModels, getActivity());
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                                    recyclerView.setAdapter(placeCheckAdapter);
                                    placeCheckAdapter.notifyDataSetChanged();
                                    CheckAnotherPlace.setVisibility(View.VISIBLE);
                                    anim_addarea.setVisibility(View.GONE);
                                    if (tmp==1){
                                        CheckArea.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        PlaceNotInList.setVisibility(View.VISIBLE);
                                        setFormCityAndState();
                                        tmp=0;
                                    }
                                }else {
                                    Snackbar snackbar = Snackbar.make(view, "No Place Found..!!!", Snackbar.LENGTH_LONG)
                                            .setAction("Add Place", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    CheckArea.setVisibility(View.GONE);
                                                    CheckAnotherPlace.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.GONE);
                                                    PlaceNotInList.setVisibility(View.GONE);
                                                    PovertyArea.setVisibility(View.VISIBLE);
                                                    anim_addarea.setVisibility(View.GONE);
                                                    setFormCityAndState();
                                                }
                                            });
                                    snackbar.show();
                                }
                            }
                        }, delay);
                    }
                }
            }
        });

        CheckAnotherPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckAnotherPlace.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                PlaceNotInList.setVisibility(View.GONE);
                CheckArea.setVisibility(View.VISIBLE);
                anim_addarea.setVisibility(View.VISIBLE);
            }
        });

        PlaceNotInList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckAnotherPlace.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                PlaceNotInList.setVisibility(View.GONE);
                PovertyArea.setVisibility(View.VISIBLE);
            }
        });

        AddPlace.setOnClickListener(v -> {
            //Getting Value Of All TextBox
            Street = StreetAddress.getText().toString();
            Area = AreaAddress.getText().toString();
            Landmark = LandmarkAddress.getText().toString();
            Pincode = PincodeOfArea.getText().toString();

            //Validation Check
            if (TextUtils.isEmpty(Landmark))
            {
                Landmark = "";
            }

            if (TextUtils.isEmpty(Street))
            {
                StreetAddress.setError("Street Is Required");
                StreetAddress.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(Area))
            {
                AreaAddress.setError("Area Is Required");
                AreaAddress.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(Pincode))
            {
                PincodeOfArea.setError("Pincode Is Required");
                PincodeOfArea.requestFocus();
                return;
            }

            if (Functions.isValidPinCode(Pincode))
            {
                Pincode = PincodeOfArea.getText().toString();
            }
            else
            {
                Toast.makeText(getActivity(),"Enter Vailed PINCODE",Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                AddPovertyArea();

                //Cleat TextBox
                StreetAddress.getText().clear();
                AreaAddress.getText().clear();
                LandmarkAddress.getText().clear();
                PincodeOfArea.getText().clear();

                //Clear Focus
                StreetAddress.clearFocus();
                AreaAddress.clearFocus();
                LandmarkAddress.clearFocus();
                PincodeOfArea.clearFocus();

                PlaceNotInList.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                PovertyArea.setVisibility(View.GONE);
                CheckArea.setVisibility(View.VISIBLE);
                anim_addarea.setVisibility(View.VISIBLE);

                StateCheck.setSelection(0);
                CityCheck.setSelection(0);
            }catch (Exception e) {
                Toast.makeText(getActivity(),"Re-Add The Place",Toast.LENGTH_SHORT).show();
                Log.d("AddPlaceError",e.toString());
            }
        });

        return view;
    }

    private ArrayList<PlaceModel> getMyList() {
        ArrayList<PlaceModel> placeModels = new ArrayList<>();
        placeModels.clear();

        dbRef = FirebaseDatabase.getInstance().getReference("Food_Donate_Place");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    String UserKey = dataSnapshot.getKey();
                    DatabaseReference dbKey = dbRef.child(UserKey);
                    dbKey.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            for (DataSnapshot dataSs:snap.getChildren()){
                                String NodeNo = dataSs.getKey();
                                DatabaseReference dbRefList = dbKey.child(NodeNo);
                                dbRefList.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot ss) {
                                        String State = ss.child("state").getValue().toString();
                                        if (State.equals(SelectedCheckState)){
                                            String City = ss.child("city").getValue().toString();
                                            if (City.equals(SelectedCheckCity)){
                                                card = new PlaceModel();

                                                String Street = ss.child("street").getValue().toString();
                                                String Area = ss.child("area").getValue().toString();
                                                String cardCity = ss.child("city").getValue().toString();
                                                String cardState = ss.child("state").getValue().toString();
                                                String Date = ss.child("date").getValue().toString();
                                                String Time = ss.child("time").getValue().toString();
                                                String FinalTime = "Place Added On "+Date+" At "+Time;

                                                card.setStreet(Street);
                                                card.setArea(Area);
                                                card.setCity(cardCity);
                                                card.setState(cardState);
                                                card.setDate(FinalTime);
                                                placeModels.add(card);

                                                tmp = 1;
                                            }
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return placeModels;
    }

    private boolean checkCitySpinner(String selectedCheckState) {
        if (SelectedCheckCity.equals("Select City"))
        {
            AlertDialog.Builder alertDialog  = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("City Error");
            alertDialog.setMessage("Please Select City");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    CityCheck.performClick();
                }
            });
            alertDialog.show();
            return false;
        }
        return true;
    }

    private Boolean checkStateSpinner(String selectedCheckState) {
        if (SelectedCheckState.equals("Select State"))
        {
            AlertDialog.Builder alertDialog  = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("State Error");
            alertDialog.setMessage("Please Select State");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    StateCheck.performClick();
                }
            });
            alertDialog.show();
            return false;
        }
        return true;
    }

    private void AddPovertyArea() {

        db = FirebaseDatabase.getInstance().getReference("Food_Donate_Place").child(UserKey);

        int modelSize = placeModels.size();
        if (modelSize==0){
            AddArea();
            return;
        }else {
            boolean area=false;
            for (int i=0;i<modelSize;i++){
                String tmpStreet = placeModels.get(i).getStreet();
                if (tmpStreet.equalsIgnoreCase(Street)){
                    String tmpArea = placeModels.get(i).getArea();
                    if (tmpArea.equalsIgnoreCase(Area)){
                        Toast.makeText(getActivity(), "Place Already Exist..!!!", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        AddArea();
                        return;
                    }
                }else {
                    area = true;
                }
            }
            if (area){
                AddArea();
                return;
            }
        }
    }

    private void AddArea() {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Add Data Into database Coding
                HashMap<String,String> information = new HashMap<>();
                information.put("street",Street);
                information.put("area",Area);
                information.put("landmark",Landmark);
                String tmpCity = City.getSelectedItem().toString();
                information.put("city",tmpCity);
                String tmpState = State.getSelectedItem().toString();
                information.put("state",tmpState);
                information.put("isVerify","No");
                information.put("pincode",Pincode);

                String CurrentDate = Date.format(new Date());
                String CurrentTime = Time.format(new Date());
                information.put("date",CurrentDate);
                information.put("time",CurrentTime);

                db = FirebaseDatabase.getInstance().getReference("Food_Donate_Place");
                db.child(UserKey).push().setValue(information);

                Toasty.success(getActivity(),"Add Place Successfully..!!!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setFormCityAndState() {
        State.setEnabled(false);
        State.setClickable(false);
        City.setEnabled(false);
        City.setClickable(false);

        ArrayList<String> StateList = new ArrayList<String>();
        StateList.add(0,SelectedCheckState);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getContext(),R.layout.statespinner_layout,R.id.State,StateList);
        State.setAdapter(stateAdapter);
        ArrayList<String> CityList= new ArrayList<String>();
        CityList.add(0,SelectedCheckCity);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(getContext(), R.layout.cityspinner_layout, R.id.City, CityList);
        City.setAdapter(cityAdapter);
    }

}