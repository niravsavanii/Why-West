package com.example.whywasteapp.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whywasteapp.Functions;
import com.example.whywasteapp.R;
import com.example.whywasteapp.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import es.dmoral.toasty.Toasty;
import kotlin.Function;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePassFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePassFragment extends Fragment implements IOBackPress{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextInputEditText currentPass,newPass,confirmPass;
    CircularProgressButton changePass;

    DatabaseReference dbRef;

    public ChangePassFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangePassFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangePassFragment newInstance(String param1, String param2) {
        ChangePassFragment fragment = new ChangePassFragment();
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
        View view = inflater.inflate(R.layout.fragment_change_pass, container, false);

        // Inflate the layout for this fragment
        currentPass = (TextInputEditText)view.findViewById(R.id.currentPass);
        newPass = (TextInputEditText)view.findViewById(R.id.newPass);
        confirmPass = (TextInputEditText)view.findViewById(R.id.confirmPass);
        changePass = (CircularProgressButton)view.findViewById(R.id.changePass);

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String CPass,NPass,CFPass;
                CPass = currentPass.getText().toString();
                NPass = newPass.getText().toString();
                CFPass = confirmPass.getText().toString();

                if (TextUtils.isEmpty(CPass)){
                    currentPass.setError("Enter Current Password..!!!");
                    currentPass.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(NPass)){
                    newPass.setError("Enter New Password..!!!");
                    newPass.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(CFPass)){
                    confirmPass.setError("Confirm Your Password..!!!");
                    confirmPass.requestFocus();
                    return;
                }

                Functions.KeyboardDown(getActivity());
                changePass.startAnimation();
                String Key = Functions.getUserID();

                dbRef = FirebaseDatabase.getInstance().getReference("Registration").child(Key);
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String UserPass = snapshot.child("Password").getValue().toString();
                        if (UserPass.equals(CPass)){
                            if (NPass.equals(CFPass)) {
                                if (!UserPass.equals(CFPass)) {
                                    FirebaseUser User = Functions.getCurrentUser();
                                    String UserEmail = Functions.getUserEmailID();

                                    AuthCredential credential = EmailAuthProvider.getCredential(UserEmail, CPass);

                                    User.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                User.updatePassword(CFPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            dbRef.child("Password").setValue(NPass);
                                                            Toasty.success(getActivity(), "Password changed successfully..!!!", Toasty.LENGTH_LONG).show();
                                                            Functions.stopBtnAnimation(changePass);
                                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                                                                    new ProfileFragment()).commit();
                                                            return;
                                                        } else {
                                                            Toasty.error(getActivity(), "Password not changed successfully..!!!", Toasty.LENGTH_LONG).show();
                                                            Functions.stopBtnAnimation(changePass);
                                                            return;
                                                        }
                                                    }
                                                });
                                            }else {
                                                Toasty.info(getActivity(),"Server issue..!!!",Toasty.LENGTH_LONG).show();
                                                Functions.stopBtnAnimation(changePass);
                                                return;
                                            }
                                        }
                                    });
                                }else {
                                    Toasty.info(getActivity(),"New password must be different from current password..!!!",Toasty.LENGTH_LONG).show();
                                    Functions.stopBtnAnimation(changePass);
                                    return;
                                }
                            }else {
                                Toasty.warning(getActivity(),"New password and confirm password is not same..!!!",Toasty.LENGTH_LONG).show();
                                Functions.stopBtnAnimation(changePass);
                                return;
                            }
                        }else {
                            Toasty.error(getActivity(),"Incorrect current password..!!!",Toasty.LENGTH_LONG).show();
                            currentPass.requestFocus();
                            Functions.stopBtnAnimation(changePass);
                            return;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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