package com.example.whywasteapp.donate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.whywasteapp.Functions;
import com.example.whywasteapp.R;
import com.example.whywasteapp.home.FoodAdapter;
import com.example.whywasteapp.login.LoginActivity;
import com.example.whywasteapp.settings.ChangePassFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class DonateMoneyActivity extends AppCompatActivity implements PaymentResultListener {

    EditText Amount;
    ImageView donateMoney;

    int DonateAmount;
    String Donate,AshramName;

    Spinner Ashram;

    List<String> spinnerArray =  new ArrayList<String>();

    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_money);

        Amount = (EditText)findViewById(R.id.Amount);
        donateMoney = (ImageView)findViewById(R.id.donateMoney);
        Ashram = (Spinner)findViewById(R.id.selectAshram);

        Ashram.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AshramName = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dbRef = FirebaseDatabase.getInstance().getReference("Admin").child("Ashram");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String Key = dataSnapshot.getKey();
                    String name = snapshot.child(Key).child("Name").getValue().toString();
                    spinnerArray.add(name);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getApplication(), android.R.layout.simple_spinner_item, spinnerArray);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Ashram.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        donateMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Donate = Amount.getText().toString();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (TextUtils.isEmpty(Donate)){
                    Amount.setError("Enter Donate Amount To Donate..!!!");
                    Amount.requestFocus();
                    return;
                }else if (user==null){
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Login to continue donate money..!!!", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Go To Login", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(DonateMoneyActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    });
                    snackbar.show();
                }else {
                    Log.d("DBefor", String.valueOf(DonateAmount));
                    DonateAmount = Math.round(Float.parseFloat(Donate) * 100);
                    Log.d("DAfter", String.valueOf(DonateAmount));
                    // initialize Razorpay account.
                    Functions.prefrence = Functions.getPref(DonateMoneyActivity.this);

                    String Name = Functions.NAME;
                    String Email = Functions.EMAIL;
                    String MoNo = Functions.MOBILE;


                    Checkout checkout = new Checkout();

                    // set your id as below
                    checkout.setKeyID("rzp_test_Q4sIeRagoiwSng");

                    // initialize json object
                    JSONObject object = new JSONObject();
                    try {
                        // OrderID
                        //object.put("order_id","asdasd");

                        // Name
                        object.put("name", "Why Waste Foods");

                        // Description
                        object.put("description", "Donate Money For Needy People");

                        // Theme color
                        object.put("theme.color", "");

                        // Currency
                        object.put("currency", "INR");

                        // Amount
                        object.put("amount", DonateAmount);

                        // Mobile Number
                        object.put("prefill.contact", String.valueOf(MoNo));

                        // Email
                        object.put("prefill.email", Email);

                        // open razorpay to checkout activity
                        checkout.open(DonateMoneyActivity.this, object);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onPaymentSuccess(String s) {
        try {
            String User = Functions.getUserID();
            dbRef = FirebaseDatabase.getInstance().getReference("Donation").child(User);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DonateAmount = DonateAmount/100;
                    if (snapshot.exists()){
                        int Node = (int) snapshot.getChildrenCount();
                        Node++;
                        HashMap<String,String> data = new HashMap<>();
                        data.put("date",Functions.getCurrentDate());
                        data.put("donation",String.valueOf(DonateAmount));
                        data.put("time",Functions.getCurrentTime());
                        data.put("name",Functions.NAME);
                        data.put("Ashram",AshramName);
                        dbRef.child(String.valueOf(Node)).setValue(data);
                        Toasty.success(getApplicationContext(),"Donation of " +DonateAmount+ "\u20B9 is successful..!!!",Toasty.LENGTH_LONG).show();
                    }else {
                        HashMap<String,String> data = new HashMap<>();
                        data.put("date",Functions.getCurrentDate());
                        data.put("donation", String.valueOf(DonateAmount));
                        data.put("time",Functions.getCurrentTime());
                        data.put("name",Functions.NAME);
                        data.put("Ashram",AshramName);
                        dbRef.child("1").setValue(data);
                        Toasty.success(getApplicationContext(),"Donation of " +DonateAmount+ "\u20B9 is successful..!!!",Toasty.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("PaymentError",error.toString());
                }
            });
            Intent intent = new Intent(getApplicationContext(), DonateFoodFragment.class);
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(intent);
        }catch (Exception e){ }
    }

    @Override
    public void onPaymentError(int i, String s) {
        DonateAmount = DonateAmount/100;
        Toasty.error(getApplicationContext(),"Your last donation of "+ DonateAmount +"\u20B9 is not successful..!!!",Toasty.LENGTH_LONG).show();
    }
}