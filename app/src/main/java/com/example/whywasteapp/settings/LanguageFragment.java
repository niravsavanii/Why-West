package com.example.whywasteapp.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.whywasteapp.R;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LanguageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LanguageFragment extends Fragment implements IOBackPress{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Switch lanEnglish,lanHindi;

    public LanguageFragment(){
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LanguageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LanguageFragment newInstance(String param1, String param2) {
        LanguageFragment fragment = new LanguageFragment();
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
        View view = inflater.inflate(R.layout.fragment_language, container, false);

        // Inflate the layout for this fragment
        lanEnglish = view.findViewById(R.id.lanEnglish);
        lanHindi = view.findViewById(R.id.lanHindi);

        lanEnglish.setClickable(false);

        lanHindi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b==true){
                    lanEnglish.setChecked(false);
                    lanHindi.setChecked(true);
                    Toasty.normal(getContext(),"Device Not Supported Hindi Language..!!!",Toasty.LENGTH_SHORT).show();
                    lanHindi.setChecked(false);
                    lanEnglish.setChecked(true);
                    lanEnglish.setClickable(false);
                }
            }
        });

        return view;
    }

    @Override
    public boolean onBackPressed() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                new SettingsFragment()).commit();
        return true;
    }
}