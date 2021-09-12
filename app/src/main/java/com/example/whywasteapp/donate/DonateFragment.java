package com.example.whywasteapp.donate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.whywasteapp.Functions;
import com.example.whywasteapp.MainActivity;
import com.example.whywasteapp.R;
import com.example.whywasteapp.home.FoodAdapter;
import com.example.whywasteapp.login.LoginActivity;
import com.example.whywasteapp.settings.SettingsFragment;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DonateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DonateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static View view;

    public static Uri CAPTURED_PHOTO1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageView donateFood,donateMoney;

    public DonateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DonateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DonateFragment newInstance(String param1, String param2) {
        DonateFragment fragment = new DonateFragment();
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Donate HERE");

        view = inflater.inflate(R.layout.fragment_donate, container, false);

        // Inflate the layout for this fragment
        donateFood = view.findViewById(R.id.donateFood);
        donateMoney = view.findViewById(R.id.donateMoney);

        donateFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 100);
                }else if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
                }else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 100);
                }
            }
        });

        donateMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                 //       new DonateMoneyFragment()).commit();

                Intent intent = new Intent(getActivity(), DonateMoneyActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
            }
        });

        return view;
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            } else {
                Toasty.error(getContext(),"CAMERA Permission Is Required..!!!",requestCode).show();
                return;
            }
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data!=null){
            try {
                Bitmap tmp = (Bitmap) data.getExtras().get("data");
                CAPTURED_PHOTO1 = Functions.BitmapToUri(tmp,getActivity());
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment myFragment = new DonateFoodFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, myFragment).addToBackStack(null).commit();
            }catch (Exception e){
                Snackbar snackbar = Snackbar.make(getView(), "Login to continue use all features..!!!", Snackbar.LENGTH_LONG);
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
        }

    }



}