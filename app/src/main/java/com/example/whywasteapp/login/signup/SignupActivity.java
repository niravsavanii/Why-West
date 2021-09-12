package com.example.whywasteapp.login.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whywasteapp.Functions;
import com.example.whywasteapp.MainActivity;
import com.example.whywasteapp.R;
import com.example.whywasteapp.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

import static com.example.whywasteapp.Functions.firebaseAuth;

public class SignupActivity extends AppCompatActivity {

    TextView Login_back;
    CircularProgressButton btn_signup;
    RadioButton genMale,genFemale;

    TextInputEditText userName,userEmail,userPassword,userMobileNo;

    String Name,Email,Password,MobileNo,Gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Login_back = findViewById(R.id.GoTo_Login);
        btn_signup = (CircularProgressButton) findViewById(R.id.btn_signup);

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        userMobileNo = findViewById(R.id.userMobileNo);
        genMale = findViewById(R.id.genMale);
        genFemale = findViewById(R.id.genFemale);


        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            //Extract the data…
            String readyEmail = bundle.getString("Email");
            userEmail.setText(readyEmail);
            bundle.clear();
        }

        // Login text click listener
        Login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open main activity
                Intent LoginScreen = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(LoginScreen);
                finish();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = userName.getText().toString();
                Email = userEmail.getText().toString();
                Password = userPassword.getText().toString();
                MobileNo = userMobileNo.getText().toString();

                if (TextUtils.isEmpty(Name)){
                    userName.setError("Name Required..!!!");
                    userName.requestFocus();
                    return;
                }
                if (Functions.isValidUsername(Name)){
                    userName.setError("Enter Valid Name..!!!");
                    userName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(Email)){
                    userEmail.setError("Email Required..!!!");
                    userEmail.requestFocus();
                    return;
                }
                if (!(Functions.isValid(Email))){
                    userEmail.setError("Enter Valid Email Address..!!!");
                    userEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(Password)){
                    userPassword.setError("Password Required..!!!");
                    userPassword.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(MobileNo)){
                    userMobileNo.setError("Mobile Number Is Required..!!!");
                    userMobileNo.requestFocus();
                    return;
                }

                if (!(Functions.isValidIndianMobileNumber(MobileNo))){
                    userMobileNo.setError("Enter Valid Mobile Number..!!!");
                    userMobileNo.requestFocus();
                    return;
                }

                if (genMale.isChecked())
                {
                    Gender = "Male";
                }else {
                    Gender = "Female";
                }

                SignUp();
            }
        });

    }

    private void SignUp() {
        btn_signup.startAnimation();
        firebaseAuth.createUserWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            Map<String, Object> data = new HashMap<>();
                            data.put("Name",Name);
                            data.put("Email",Email);
                            data.put("Password",Password);
                            data.put("MobileNo",MobileNo);
                            data.put("Gender",Gender);

                            FirebaseDatabase.getInstance().getReference("Registration")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    savePrefsData();
                                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Registred successfully..!!!", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    Functions.playNotificationSound(SignupActivity.this);
                                    Functions.setVibration(SignupActivity.this);
                                    Functions.setNotification(SignupActivity.this,"Hii "+Name,"Welcome To Why Waste Application");
                                    Functions.stopBtnAnimation(btn_signup);
                                    finish();
                                }
                            });
                        }
                        else if (task.getException() instanceof FirebaseAuthUserCollisionException){
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Email address already registred..!!!", Snackbar.LENGTH_LONG);
                            snackbar.setAction("Login", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                                    Bundle bundle = new Bundle();
                                    bundle.putString("Email",Email);
                                    bundle.putString("Pass",Password);
                                    intent.putExtras(bundle);

                                    startActivity(intent);
                                    finish();
                                }
                            });
                            snackbar.show();
                            Functions.stopBtnAnimation(btn_signup);
                        } else {
                            Toast.makeText(SignupActivity.this, "Authentication failed, Try after some times..!!!", Toast.LENGTH_SHORT).show();
                            Functions.stopBtnAnimation(btn_signup);
                            return;
                        }
                    }
                });
    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend", true);
        editor.putString("Name",Name);
        editor.putString("MobNo",MobileNo);
        editor.putString("Email",Email);
        editor.putString("Gender",Gender);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Do You Want To Exit The Application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
    }
}