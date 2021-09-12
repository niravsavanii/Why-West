package com.example.whywasteapp.donate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whywasteapp.Functions;
import com.example.whywasteapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import es.dmoral.toasty.Toasty;

import static com.example.whywasteapp.Functions.NAME;

public class SelectAreaForDonate extends AppCompatActivity {

    Spinner Address;
    TextView Name,pincode,MatchedArea,OthersArea,txtAddress,Reselect,txtHeader;

    CircularProgressButton SelectAddress;

    int MatchArea=0,OtherArea=0,tmp=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_area_for_donate);

        Intent intent = getIntent();
        String Pincode = intent.getExtras().getString("Pincode");
        int Node = DonateFoodFragment.Node;

        Address = (Spinner)findViewById(R.id.Address);
        Name = (TextView)findViewById(R.id.Name);
        pincode = (TextView)findViewById(R.id.Pincode);
        MatchedArea = (TextView)findViewById(R.id.MatchedArea);
        OthersArea = (TextView)findViewById(R.id.OthersArea);
        txtAddress = (TextView)findViewById(R.id.txtAddress);
        txtHeader = (TextView)findViewById(R.id.txtHeader);
        Reselect = (TextView)findViewById(R.id.Reselect);

        SelectAddress = (CircularProgressButton)findViewById(R.id.SelectAddress);

        //Get Preference
        SharedPreferences prefrence = Functions.getPref(SelectAreaForDonate.this);

        Name.setText(NAME);
        pincode.setText(Pincode);

        List<String> spinnerArray =  new ArrayList<String>();
        List<String> ashramArray =  new ArrayList<String>();

        spinnerArray.add("Select Poverty Area");
        ashramArray.add("Select Ashram For Donation");

        Address.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Add = parent.getItemAtPosition(position).toString();
                if (!Add.equals("Select Poverty Area")){
                    Address.setVisibility(View.GONE);
                    txtAddress.setVisibility(View.VISIBLE);
                    Reselect.setVisibility(View.VISIBLE);
                    SelectAddress.setVisibility(View.VISIBLE);
                    txtHeader.setText("Selected Area");
                    txtAddress.setText(Add);
                    SelectAddress.setText("Address Selected");
                }
                if (tmp==0){
                    Reselect.performClick();
                    tmp++;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Food_Donate_Place");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String Key = dataSnapshot.getKey();
                    DatabaseReference db = dbRef.child(Key);
                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            for (DataSnapshot dataSnap:snap.getChildren()){

                                String AddressKey = dataSnap.getKey();
                                DatabaseReference ref = db.child(AddressKey);
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot ss) {
                                        String Code = ss.child("pincode").getValue().toString();
                                        if (Code.equals(Pincode)){
                                            String Address = "";
                                            MatchArea++;
                                            String Street = ss.child("street").getValue().toString();
                                            String Area = ss.child("area").getValue().toString();
                                            String City = ss.child("city").getValue().toString();
                                            String State = ss.child("state").getValue().toString();
                                            if (ss.child("landmark").exists()){
                                                String Landmark = ss.child("landmark").getValue().toString();
                                                Address = Street+", "+Area+", "+Landmark+", "+City+", "+State+" - "+Pincode;
                                            }else {
                                                Address = Street+", "+Area+", "+City+", "+State+" - "+Pincode;
                                            }

                                            spinnerArray.add(Address);
                                            //Toast.makeText(SelectAreaForDonate.this, Area, Toast.LENGTH_SHORT).show();
                                            MatchedArea.setText(MatchArea+" Area Founds");
                                        }else {
                                            OtherArea++;
                                            OthersArea.setText(OtherArea+" Area Founds");
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

        ArrayAdapter<String> AddressAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.addressspinner_layout, R.id.Address, spinnerArray);
        Address.setAdapter(AddressAdapter);

        SelectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = (String) Address.getSelectedItem();
                if (tmp.equals("Select Poverty Area")){
                    Address.performClick();
                    Toasty.warning(getApplicationContext(),"Select Poverty Area To Donate Foods..!!!",Toasty.LENGTH_LONG).show();
                }else {
                    String User = Functions.getUserID();
                    SelectAddress.startAnimation();
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Food_Donate").child(User).child(String.valueOf(Node)).child("Status");
                    HashMap<String,String> data = new HashMap<>();
                    data.put("Donated","Successful");
                    data.put("Address",txtAddress.getText().toString());
                    data.put("DonatedBy","Self");
                    dbRef.setValue(data);
                    finish();
                    Toasty.success(getApplicationContext(),"ThankYou For Donating Food On Why Waste",Toasty.LENGTH_LONG).show();
                }
            }
        });

        Reselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reselect.setVisibility(View.GONE);
                txtAddress.setVisibility(View.GONE);
                SelectAddress.setVisibility(View.GONE);
                Address.setVisibility(View.VISIBLE);
                txtHeader.setText("Poverty Area List");
                Address.setSelection(0);
            }
        });

    }
}