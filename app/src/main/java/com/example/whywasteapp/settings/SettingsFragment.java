package com.example.whywasteapp.settings;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whywasteapp.Functions;
import com.example.whywasteapp.R;
import com.example.whywasteapp.faqs.FaqsFragment;
import com.example.whywasteapp.login.LoginActivity;
import com.example.whywasteapp.profile.ProfileFragment;
import com.google.android.material.internal.NavigationMenu;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Context myContext;

    private Switch darkModeSwitch;

    TextView editDetails,userName,editProfile,changePass,language,logout;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        myContext =  container.getContext();

        //Inflate the layout for this fragment
        editDetails = view.findViewById(R.id.editDetails);
        darkModeSwitch = view.findViewById(R.id.darkModeSwitch);
        changePass = view.findViewById(R.id.changePass);
        editProfile = view.findViewById(R.id.editProfile);
        language = view.findViewById(R.id.language);
        logout = view.findViewById(R.id.logout);

        if(new DarkModePrefManager(getActivity()).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        setDarkModeSwitch();

        userName = (TextView)view.findViewById(R.id.userName);
        Functions.prefrence = Functions.getPref(getActivity());
        userName.setText(Functions.NAME);

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new ChangePassFragment()).commit();
            }
        });

        editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditPersonalDetails();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditPersonalDetails();
            }
        });

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        new LanguageFragment()).commit();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = mySPrefs.edit();
                editor.clear();
                editor.apply();

//              SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
//              sharedPreferences.edit().clear().commit();
                Functions.User = FirebaseAuth.getInstance();
                Functions.User.signOut();

                getActivity().finish();
                Toast.makeText(getActivity(), "Logged out successful..!!!", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(getActivity(), LoginActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(login);
            }
        });

        return view;
    }

    private void setDarkModeSwitch(){
        darkModeSwitch.setChecked(new DarkModePrefManager(getActivity()).isNightMode());
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    DarkModePrefManager darkModePrefManager = new DarkModePrefManager(getActivity());
                    darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Toast.makeText(getActivity(), "Switch successfully to dark mode..!!!", Toast.LENGTH_SHORT).show();
                }else {
                    DarkModePrefManager darkModePrefManager = new DarkModePrefManager(getActivity());
                    darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Toast.makeText(getActivity(), "Switch successfully to day mode..!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void goToEditPersonalDetails() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                new ProfileFragment()).commit();
    }

}