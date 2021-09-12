package com.example.whywasteapp.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whywasteapp.Functions;
import com.example.whywasteapp.MainActivity;
import com.example.whywasteapp.R;
import com.example.whywasteapp.login.signup.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.PasswordAuthentication;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import es.dmoral.toasty.Toasty;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.whywasteapp.Functions.KeyboardDown;
import static com.example.whywasteapp.Functions.firebaseAuth;

public class LoginActivity extends AppCompatActivity {
    CircularProgressButton btnLogin;
    TextView signUp,forgotPass,Guest;
    TextInputEditText userEmail,userPassword;

    private SharedPreferences myPref;

    FirebaseAuth firebaseAuth;
    //FirebaseAuth User = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(this,MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btn_login);
        signUp = findViewById(R.id.signUp);

        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        Guest = findViewById(R.id.Guest);

        forgotPass = findViewById(R.id.forgotpwd);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            //Extract the data…
            String readyEmail = bundle.getString("Email");
            String readyPass = bundle.getString("Pass");
            userEmail.setText(readyEmail);
            userPassword.setText(readyPass);
            bundle.clear();
        }

        // Login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.startAnimation();
                userEmail.clearFocus();
                userPassword.clearFocus();
                String Email, Password;
                Email = userEmail.getText().toString();
                Password = userPassword.getText().toString();

                if (TextUtils.isEmpty(Email)) {
                    userEmail.setError("Email address required..!!!");
                    userEmail.requestFocus();
                    Functions.stopBtnAnimation(btnLogin);
                    return;
                }
                if (!(Functions.isValid(Email))){
                    userEmail.setError("Enter Valid Email Address..!!!");
                    userEmail.requestFocus();
                    Functions.stopBtnAnimation(btnLogin);
                    return;
                }
                if (TextUtils.isEmpty(Password)) {
                    userPassword.setError("Enter password..!!!");
                    userPassword.requestFocus();
                    Functions.stopBtnAnimation(btnLogin);
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                btnLogin.startAnimation();
                                if (task.isSuccessful()) {
                                    savePrefsData();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    Functions.stopBtnAnimation(btnLogin);
                                    Toasty.success(getApplicationContext(),"Login successful", LENGTH_SHORT).show();
                                    Functions.playNotificationSound(LoginActivity.this);
                                    Functions.setVibration(LoginActivity.this);
                                    SharedPreferences prefrence = Functions.getPref(LoginActivity.this);
                                    String tmpName = prefrence.getString("Name","");
                                    Functions.setNotification(LoginActivity.this,"Hii "+Functions.NAME,"Welcome Back To Why Waste Application");
                                    finish();
                                }else {
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        Functions.stopBtnAnimation(btnLogin);
                                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Invalid password..!!!", Snackbar.LENGTH_LONG);
                                        snackbar.setAction("Enter Again", new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                userPassword.requestFocus();
                                                Functions.KeyboardUp(LoginActivity.this);
                                            }
                                        });
                                        snackbar.show();
                                        return;
                                    }else {
                                        Functions.stopBtnAnimation(btnLogin);
                                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Email address not exist..!!!", Snackbar.LENGTH_LONG);
                                        snackbar.setAction("SignUp", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);

                                                Bundle bundle = new Bundle();
                                                bundle.putString("Email", Email);
                                                intent.putExtras(bundle);

                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                        snackbar.show();
                                        return;
                                    }
                                }
                            }
                        });
            }
        });

        userPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnLogin.performClick();
                    return true;
                }
                return false;
            }
        });

        // signup button click listener
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open main activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Forgot Password Click Listener
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email;
                Email = userEmail.getText().toString();
                if (TextUtils.isEmpty(Email)) {
                    userEmail.setError("Email address required..!!!");
                    userEmail.requestFocus();
                    return;
                }
                if (!(Functions.isValid(Email))){
                    userEmail.setError("Enter Valid Email Address..!!!");
                    userEmail.requestFocus();
                    return;
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(Email)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Password reset link successfully sent on your email..!!!", Snackbar.LENGTH_LONG);
                                    snackbar.setAction("Go", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i;
                                            PackageManager manager = getPackageManager();
                                            try {
                                                i = manager.getLaunchIntentForPackage("com.google.android.gm");
                                                if (i == null)
                                                    throw new PackageManager.NameNotFoundException();
                                                i.addCategory(Intent.CATEGORY_LAUNCHER);
                                                startActivity(i);
                                            } catch (PackageManager.NameNotFoundException e) {
                                                Toast.makeText(LoginActivity.this, "Gmail app not found..!!!", LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    snackbar.show();
                                    userEmail.clearFocus();
                                    return;
                                } else {
                                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Email address not registered..!!!", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                    return;
                                }
                            }
                        });
            }
        });
    }

    private void savePrefsData() {
        String UID = Functions.getUserID();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Registration").child(UID);

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("isIntroOpnend", true);

                String Name = snapshot.child("Name").getValue().toString();
                String MobNo = snapshot.child("MobileNo").getValue().toString();
                String Email = snapshot.child("Email").getValue().toString();
                String Gen = snapshot.child("Gender").getValue().toString();
                if (snapshot.child("Address").exists()){
                    String Address = snapshot.child("Address").getValue().toString();
                    editor.putString("Address",Address);
                }
                if (snapshot.child("City").exists()){
                    String City = snapshot.child("City").getValue().toString();
                    editor.putString("City",City);
                }
                if (snapshot.child("State").exists()){
                    String State = snapshot.child("State").getValue().toString();
                    editor.putString("State",State);
                }

                editor.putBoolean("isIntroOpnend", true);
                editor.putString("Name",Name);
                editor.putString("MobNo",MobNo);
                editor.putString("Email",Email);
                editor.putString("Gender",Gen);
                editor.commit();

                Functions.NAME = Name;
                Functions.MOBILE = MobNo;
                Functions.EMAIL = Email;
                Functions.GENDER = Gen;
                editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

