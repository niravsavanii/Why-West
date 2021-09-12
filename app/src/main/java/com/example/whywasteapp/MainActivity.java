package com.example.whywasteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.whywasteapp.addarea.AddAreaFragment;
import com.example.whywasteapp.donate.DonateFragment;
import com.example.whywasteapp.faqs.FaqsFragment;
import com.example.whywasteapp.home.FoodAdapter;
import com.example.whywasteapp.home.HomeFragment;
import com.example.whywasteapp.login.LoginActivity;
import com.example.whywasteapp.profile.ProfileFragment;
import com.example.whywasteapp.settings.IOBackPress;
import com.example.whywasteapp.settings.SettingsFragment;
import com.example.whywasteapp.volunteer.VolunteerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

import static com.example.whywasteapp.Functions.firebaseAuth;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BottomNavigationView navigation = findViewById(R.id.bottom_navigation_main);
        onNavigationItemSelected(navigation.getMenu().getItem(0));
        navigation.setOnNavigationItemSelectedListener(this);
        findViewById(R.id.donate_food).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigation.setSelectedItemId(R.id.nav_hidden_option);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_addarea:
                fragment = new AddAreaFragment();
                break;
            case R.id.nav_hidden_option:
                fragment = new DonateFragment();
                break;
            case R.id.nav_volunteer:
                fragment = new VolunteerFragment();
                break;
            case R.id.nav_profile:
                fragment = new ProfileFragment();
                break;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout,fragment).commit();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_setting){
            loadFragment(new SettingsFragment());
            return true;
        }
        if(id == R.id.nav_faq){
            loadFragment(new FaqsFragment());
            return true;
        }

        if(id == R.id.nav_logout){

            SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = mySPrefs.edit();
            editor.clear();
            editor.apply();

//            SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
//            sharedPreferences.edit().clear().commit();
            Functions.User = FirebaseAuth.getInstance();
            Functions.User.signOut();
            finish();
            Toast.makeText(this, "Logged out successful..!!!", Toast.LENGTH_SHORT).show();
            Intent login = new Intent(this, LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(login);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_layout, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_layout);
        if (!(fragment instanceof IOBackPress) || !((IOBackPress) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }

}
