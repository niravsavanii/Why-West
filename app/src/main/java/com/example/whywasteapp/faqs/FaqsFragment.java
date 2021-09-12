package com.example.whywasteapp.faqs;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whywasteapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FaqsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FaqsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;

    ProgressDialog progressDialog;
    ArrayList<FaqsModel> faqModels;

    FaqsModel faqCard;

    FaqsAdapter faqAdapter;

    DatabaseReference dbRef;

    public FaqsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FaqsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FaqsFragment newInstance(String param1, String param2) {
        FaqsFragment fragment = new FaqsFragment();
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
        View view = inflater.inflate(R.layout.fragment_faqs, container, false);

        // Inflate the layout for this fragment
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerViewFaq);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        int delay = 1000;
        faqModels = getMyList();
        Handler handler = new Handler();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                // Do your work here
                if (faqModels.size()>0) {
                    faqAdapter = new FaqsAdapter(faqModels, getActivity());
                    recyclerView.setAdapter(faqAdapter);
                    faqAdapter.notifyDataSetChanged();
                }
            }
        }, delay);

        return view;
    }

    private ArrayList<FaqsModel> getMyList() {
        ArrayList<FaqsModel> faqModels = new ArrayList<>();

        dbRef = FirebaseDatabase.getInstance().getReference("Admin").child("FAQs");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String Node = dataSnapshot.getKey();
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Admin").child("FAQs").child(Node);
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            for (DataSnapshot data:snap.getChildren()){
                                if (data.getKey().equals("Answer")){
                                    faqCard = new FaqsModel();
                                    faqCard.setKey(Node);
                                    String Answer = data.getValue().toString();
                                    faqCard.setAnswer(Answer);
                                }
                                if (data.getKey().equals("Question")){
                                    String Question = data.getValue().toString();
                                    faqCard.setQuestion(Question);
                                    faqModels.add(faqCard);
                                }
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

        return faqModels;
    }
}