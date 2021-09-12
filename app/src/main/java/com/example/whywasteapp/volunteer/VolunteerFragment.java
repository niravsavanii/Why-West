package com.example.whywasteapp.volunteer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.whywasteapp.Functions;
import com.example.whywasteapp.R;
import com.example.whywasteapp.login.LoginActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VolunteerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VolunteerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText Name,Address,Area,Landmark,PincodeOfArea,FreeTime,MobNo;
    CircularProgressButton JoinVolunteer,RemoveFromVolunteer;
    Spinner State,City;

    ArrayAdapter<String> stateAdapter;

    String VolunteerName,MobileNo,StreetAddress,AreaOfAddress,LandmarkOfAddress,Pincode,SelectedState,SelectedCity,FreeTimeOfVolunteer;
    String Email;

    DatabaseReference db;

    int cityPosition;

    public VolunteerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VolunteerFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static VolunteerFragment newInstance(String param1, String param2) {
        VolunteerFragment fragment = new VolunteerFragment();
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Join As Volunteer");

        View view = inflater.inflate(R.layout.fragment_volunteer, container, false);

        // Inflate the layout for this fragment
        Name = view.findViewById(R.id.Name);
        Address = view.findViewById(R.id.Address);
        Area = view.findViewById(R.id.Area);
        Landmark = view.findViewById(R.id.Landmark);
        State = view.findViewById(R.id.State);
        City = view.findViewById(R.id.City);
        PincodeOfArea = view.findViewById(R.id.Pincode);
        MobNo = view.findViewById(R.id.MobNo);
        FreeTime = view.findViewById(R.id.FreeTime);
        JoinVolunteer = view.findViewById(R.id.JoinVolunteer);
        RemoveFromVolunteer = view.findViewById(R.id.RemoveFromVolunteer);

        try {
            CheckVolunteerDataIsExistOrNot();
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


        //State Spinner Fill Code
        ArrayList<String> StateList = Functions.getState("state.json",getActivity());
        StateList.add(0,"Select State");
        stateAdapter = new ArrayAdapter<String>(getContext(),R.layout.statespinner_layout,R.id.State,StateList);
        State.setAdapter(stateAdapter);
        ArrayList<String> CityList= new ArrayList<String>();
        CityList.add(0,"Select City");
        ArrayAdapter<String>[] cityAdapter = new ArrayAdapter[]{new ArrayAdapter<String>(getContext(), R.layout.cityspinner_layout, R.id.City, CityList)};
        City.setAdapter(cityAdapter[0]);

        State.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (int i=0;i<36;i++)
                {
                    if(position==i+1)
                    {
                        //City Spinner Fill Code
                        ArrayList<String> CityList = Functions.getCity("state.json",i,getActivity());
                        CityList.add(0,"Select City");
                        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(getContext(), R.layout.cityspinner_layout, R.id.City, CityList);
                        City.setAdapter(cityAdapter);
                        //City.performClick();
                    }
                }
                SelectedState = parent.getItemAtPosition(position).toString();
                Log.d("STATES",SelectedState);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        City.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectedCity = parent.getItemAtPosition(position).toString();
                Log.d("CITYS",SelectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        JoinVolunteer.setOnClickListener(v -> {
            //Get Textbox Value
            VolunteerName = Name.getText().toString();
            MobileNo = MobNo.getText().toString();
            StreetAddress = Address.getText().toString();
            AreaOfAddress = Area.getText().toString();
            LandmarkOfAddress = Landmark.getText().toString();
            Pincode = PincodeOfArea.getText().toString();
            FreeTimeOfVolunteer = FreeTime.getText().toString();

            //Perform Validation
            if (TextUtils.isEmpty(VolunteerName))
            {
                Name.setError("Name Is Required For Joining As A Volunteer");
                Name.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(MobileNo))
            {
                MobNo.setError("Mobile Number Required For Joining Volunteer");
                MobNo.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(StreetAddress))
            {
                Address.setError("Address Is Required For Joining As A Volunteer");
                Address.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(AreaOfAddress))
            {
                Area.setError("Area Is Required For Address");
                Area.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(LandmarkOfAddress))
            {
                LandmarkOfAddress = "";
            }
            if (SelectedState.equals("Select State"))
            {
                AlertDialog.Builder alertDialog  = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("State Error");
                alertDialog.setMessage("Please Select State");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        State.performClick();
                    }
                });
                alertDialog.show();
                return;
            }

            if (SelectedCity.equals("Select City"))
            {
                AlertDialog.Builder alertDialog  = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("City Error");
                alertDialog.setMessage("Please Select City");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        City.performClick();
                    }
                });
                alertDialog.show();
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
                Toasty.error(getActivity(),"Enter Valid PINCODE..!!!",Toasty.LENGTH_LONG).show();
                return;
            }

            if (TextUtils.isEmpty(FreeTimeOfVolunteer))
            {
                FreeTime.setError("Free Time Is Required For Become A Volunteer");
                FreeTime.requestFocus();
                return;
            }

            try {
                Email = Functions.getUserEmailID();
            }catch (Exception e) {
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
                return;
            }

            if (JoinVolunteer.getText().equals("Update Details")){
                HashMap<String,Object> data = new HashMap<>();
                data.put("email",Email);
                data.put("name",VolunteerName);
                data.put("mobile",MobileNo);
                data.put("address",StreetAddress);
                data.put("area",AreaOfAddress);
                data.put("landmark",LandmarkOfAddress);
                data.put("state",SelectedState);
                data.put("city",SelectedCity);
                data.put("pincode",Pincode);
                data.put("freeTime",FreeTimeOfVolunteer);
                data.put("isVerify","No");
                String CurrentDate = Functions.getCurrentDate();
                String CurrentTime = Functions.getCurrentTime();
                data.put("date",CurrentDate);
                data.put("time",CurrentTime);

                String UserID = Functions.getUserID();
                db = FirebaseDatabase.getInstance().getReference("Volunteer_Details");
                db.child(String.valueOf(UserID)).setValue(data);

                Toasty.success(getActivity(),"Volunteers Details updated successfully..!!!",Toasty.LENGTH_LONG).show();
                return;
            }else {
                AddVolunteerData();
            }

            try {
                CheckVolunteerDataIsExistOrNot();
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


            Name.getText().clear();
            Address.getText().clear();
            Area.getText().clear();
            Landmark.getText().clear();
            //State.setSelection(0);
            //City.setSelection(0);
            PincodeOfArea.getText().clear();
            FreeTime.getText().clear();
            Log.e("EndOfFun","Success");
        });

        RemoveFromVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String User = Functions.getUserID();
                db = FirebaseDatabase.getInstance().getReference("Volunteer_Details").child(User);
                db.removeValue();
                Toasty.info(getActivity(),"You're Successfully Removed From Volunteers",Toasty.LENGTH_LONG).show();
                RemoveFromVolunteer.setVisibility(View.GONE);
                JoinVolunteer.setText("Become A Volunteer");
                Name.getText().clear();
                MobNo.getText().clear();
                Address.getText().clear();
                Area.getText().clear();
                Landmark.getText().clear();
                PincodeOfArea.getText().clear();
                FreeTime.getText().clear();
                return;
            }
        });

        return view;
    }

    private void loadUserData() {
        Name.setText(Functions.NAME);
        MobNo.setText(Functions.MOBILE);
        if (Functions.ADDRESS!=null){
            Address.setText(Functions.ADDRESS);
        }
    }

    private void CheckVolunteerDataIsExistOrNot() {
        db = FirebaseDatabase.getInstance().getReference().child("Volunteer_Details");
        Email = Functions.getUserEmailID();
        if (!Email.isEmpty()){
            db.orderByChild("email").equalTo(Email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Toasty.info(getActivity(),"You Are Already Joined As A Volunteer",Toasty.LENGTH_SHORT).show();
                                String User = Functions.getUserID();
                                Name.setText(snapshot.child(User).child("name").getValue().toString());
                                MobNo.setText(snapshot.child(User).child("mobile").getValue().toString());
                                Address.setText(snapshot.child(User).child("address").getValue().toString());
                                Area.setText(snapshot.child(User).child("area").getValue().toString());
                                if (snapshot.child(User).child("area").exists()){
                                    Landmark.setText(snapshot.child(User).child("landmark").getValue().toString());
                                }

                                String StateValue = snapshot.child(User).child("state").getValue().toString();
                                String CityValue = snapshot.child(User).child("city").getValue().toString();

                                int statePosition = stateAdapter.getPosition(StateValue);
                                State.setSelection(statePosition);

                                ArrayList<String> CityList = Functions.getCity("state.json",statePosition,getActivity());
                                CityList.add(0,"Select City");

                                ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(getContext(), R.layout.cityspinner_layout, R.id.City, CityList);
                                int cityPosition = cityAdapter.getPosition(CityValue);
                                City.setSelection(cityPosition);

                                Log.d("Adaasd", String.valueOf(cityPosition));
//                            City.setAdapter(cityAdapter);



//                            Log.d("Cityy",CityValue);
//                            cityPosition = cityAdapter.getPosition(CityValue);
//
//                            Log.d("CityyPoss", String.valueOf(cityPosition));
//                            City.setSelection(cityPosition);

                                FreeTime.setText(snapshot.child(User).child("freeTime").getValue().toString());
                                PincodeOfArea.setText(snapshot.child(User).child("pincode").getValue().toString());
                                JoinVolunteer.setText("Update Details");
                                RemoveFromVolunteer.setVisibility(View.VISIBLE);
                                return;
                            }else {
                                loadUserData();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void AddVolunteerData(){

        db = FirebaseDatabase.getInstance().getReference("Volunteer_Details");

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String,Object> data = new HashMap<>();
                data.put("email",Email);
                data.put("name",VolunteerName);
                data.put("mobile",MobileNo);
                data.put("address",StreetAddress);
                data.put("area",AreaOfAddress);
                data.put("landmark",LandmarkOfAddress);
                data.put("state",SelectedState);
                data.put("city",SelectedCity);
                data.put("pincode",Pincode);
                data.put("freeTime",FreeTimeOfVolunteer);
                data.put("isVerify","No");
                String CurrentDate = Functions.getCurrentDate();
                String CurrentTime = Functions.getCurrentTime();
                data.put("date",CurrentDate);
                data.put("time",CurrentTime);

                String UserID = Functions.getUserID();
                db = FirebaseDatabase.getInstance().getReference("Volunteer_Details");
                db.child(String.valueOf(UserID)).setValue(data);

                Toasty.success(getActivity(),"You Are Joining As A Volunteer Successfully..!!!",Toasty.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new VolunteerFragment()).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}