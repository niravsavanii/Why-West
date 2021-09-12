package com.example.whywasteapp.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.whywasteapp.R;
import com.example.whywasteapp.settings.IOBackPress;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CovidInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CovidInfoFragment extends Fragment implements IOBackPress {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CovidInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CovidInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CovidInfoFragment newInstance(String param1, String param2) {
        CovidInfoFragment fragment = new CovidInfoFragment();
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Covid Info");

        View view = inflater.inflate(R.layout.fragment_covid_info, container, false);
        // Inflate the layout for this fragment

        Button Web = view.findViewById(R.id.statistics);
        Button Call = view.findViewById(R.id.button);
        Button Sms = view.findViewById(R.id.button2);

        String num = "Covid-19 Help Line";

        Call.setOnClickListener(openDialer -> {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:7433000104"));
            startActivity(callIntent);
        });

        Sms.setOnClickListener(sendSms -> {
            Uri uri = Uri.parse("smsto:" + num);
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra("sms_body", "I am feel sick with covid-19 symptoms. Please immediate help me.");
            startActivity(intent);
        });

        Web.setOnClickListener(webView -> {
            Uri uri = Uri.parse("https://www.covid19india.org/"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public boolean onBackPressed() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                new HomeFragment()).commit();
        return false;
    }
}