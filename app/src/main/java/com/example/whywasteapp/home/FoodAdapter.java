package com.example.whywasteapp.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.example.whywasteapp.Functions;
import com.example.whywasteapp.R;
import com.example.whywasteapp.login.LoginActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class FoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<FoodModel> foodModels;
    Context mContext;

    private final int limit = 4;
    int like[]={R.drawable.ic_like,R.drawable.ic_liked};
    FoodReviewModel foodReviewCard;

    ProgressDialog progressDialog;

    String User = Functions.getUserID();

    DatabaseReference databaseReference,db;

    public FoodAdapter(Context context) {
        mContext = context;
    }

    public FoodAdapter(ArrayList<FoodModel> dataSet, Context mContext) {
        this.foodModels = dataSet;
        this.mContext = mContext;
    }

    public static class FoodHolder extends RecyclerView.ViewHolder {
        TextView UserKey,DonatorNode,FoodName,DonatorName,DonationTime;
        ImageView FoodImage,FoodReview,FoodLike;
        LinearLayout FoodCard;

        public FoodHolder(View itemView) {
            super(itemView);

            this.UserKey = itemView.findViewById(R.id.UserKey);
            this.DonatorNode = itemView.findViewById(R.id.DonationNode);
            this.FoodImage = itemView.findViewById(R.id.donateFoodImg);
            this.FoodReview = itemView.findViewById(R.id.foodReview);
            this.FoodLike = itemView.findViewById(R.id.FoodLike);
            this.FoodName = itemView.findViewById(R.id.donatedFoodName);
            this.DonatorName = itemView.findViewById(R.id.donatorName);
            this.DonationTime = itemView.findViewById(R.id.donationTime);
            this.FoodCard = itemView.findViewById(R.id.FoodCard);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.donatefoodrow_layout, null);
        return new FoodHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FoodHolder foodHolder = (FoodHolder) holder;
        foodHolder.UserKey.setText(foodModels.get(position).getUserKey());
        foodHolder.DonatorNode.setText(foodModels.get(position).getDonatorNode());
        foodHolder.FoodName.setText(foodModels.get(position).getFoodName());
        foodHolder.DonatorName.setText(foodModels.get(position).getDonatorName());
        foodHolder.DonationTime.setText(foodModels.get(position).getDonationTime());
        Glide.with(mContext).load(foodModels.get(position).getFoodImage()).into(foodHolder.FoodImage);

        databaseReference  = FirebaseDatabase.getInstance().getReference("Food_Donate").child(foodHolder.UserKey.getText().toString()).child(foodHolder.DonatorNode.getText().toString()).child("Like");
        String UserID = Functions.getUserID();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.child(UserID).exists()){
                        String Like = snapshot.child(UserID).getValue().toString();
                        if (Like.equals("Yes")){
                            foodHolder.FoodLike.setImageResource(like[1]);
                        }else {
                            foodHolder.FoodLike.setImageResource(like[0]);
                        }
                    }else {
                        foodHolder.FoodLike.setImageResource(like[0]);
                    }
                }catch (Exception e){}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        foodHolder.FoodReview.setOnClickListener(new View.OnClickListener() {

            RecyclerView recyclerViewReview;
            ArrayList<FoodReviewModel> foodReviewModels;
            FoodReviewAdapter foodReviewAdapter;
            TextView noData;
            EditText reviewText;
            ImageView sendReview;

            @Override
            public void onClick(View v) {
                DialogPlus dialog = DialogPlus.newDialog(mContext)
                        .setContentHolder(new ViewHolder(R.layout.foodreview))
                        .setExpanded(true,1000)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();

                View reviewView = dialog.getHolderView();
                foodReviewModels = new ArrayList<>();
                recyclerViewReview = reviewView.findViewById(R.id.recyclerViewReview);
                reviewText = reviewView.findViewById(R.id.reviewText);
                sendReview = reviewView.findViewById(R.id.sendReView);
                noData = reviewView.findViewById(R.id.NoData);
                recyclerViewReview.setLayoutManager(new LinearLayoutManager(mContext));
                String UserKey = foodModels.get(position).getUserKey();
                String DonateNode = foodModels.get(position).getDonatorNode();

                foodReviewModels = getMyReviewList(UserKey,DonateNode);

                android.os.Handler handler = new Handler();
                int delay = 1000; // 1000 milliseconds == 1 second

                progressDialog = new ProgressDialog(reviewView.getContext());
                progressDialog.setMessage("Please Wait..");
                progressDialog.show();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        // Do your work here
                        if (foodReviewModels.size()>0) {
                            foodReviewAdapter = new FoodReviewAdapter(foodReviewModels, reviewView.getContext());
                            recyclerViewReview.setAdapter(foodReviewAdapter);
                            foodReviewAdapter.notifyDataSetChanged();
                        }
                    }
                }, delay);

                dialog.show();

                sendReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String Review = reviewText.getText().toString();
                        if (TextUtils.isEmpty(Review)){
                            reviewText.setError("Enter Text For Review..!!!");
                            reviewText.requestFocus();
                            return;
                        }else {
                            try {
                                db = FirebaseDatabase.getInstance().getReference("Food_Donate").child(UserKey).child(DonateNode).child("Review").child(User);
                                db.child("name").setValue(Functions.NAME);
                                db.child("review").setValue(Review);
                                Toasty.success(mContext,"Review Added Successfully..!!!",Toasty.LENGTH_LONG).show();
                                dialog.dismiss();
                                InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            }catch (Exception e){
                                Snackbar snackbar = Snackbar.make(((FoodHolder) holder).FoodReview, "Login to continue use all features..!!!", Snackbar.LENGTH_LONG);
                                snackbar.setAction("Login", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(mContext, LoginActivity.class);
                                        mContext.startActivities(new Intent[]{intent});
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

        foodHolder.FoodLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference  = FirebaseDatabase.getInstance().getReference("Food_Donate").child(foodHolder.UserKey.getText().toString()).child(foodHolder.DonatorNode.getText().toString()).child("Like");
                String UserID = Functions.getUserID();
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.child(UserID).exists()){
                                String Like = snapshot.child(UserID).getValue().toString();
                                if (Like.equals("Yes")){
                                    foodHolder.FoodLike.setImageResource(like[0]);
                                    databaseReference.child(UserID).setValue("No");
                                }else {
                                    foodHolder.FoodLike.setImageResource(like[1]);
                                    databaseReference.child(UserID).setValue("Yes");
                                    Toasty.success(mContext,"Liked..!!!",Toasty.LENGTH_LONG).show();
                                }
                            }else {
                                foodHolder.FoodLike.setImageResource(like[1]);
                                databaseReference.child(UserID).setValue("Yes");
                                Toasty.success(mContext,"Liked..!!!",Toasty.LENGTH_LONG).show();
                            }
                        }catch (Exception e){
                            Snackbar snackbar = Snackbar.make(((FoodHolder) holder).FoodReview, "Login to continue use all features..!!!", Snackbar.LENGTH_LONG);
                            snackbar.setAction("Login", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(mContext, LoginActivity.class);
                                    mContext.startActivities(new Intent[]{intent});
                                }
                            });
                            snackbar.show();
                            return;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    private ArrayList<FoodReviewModel> getMyReviewList(String userKey,String donateNode) {
        ArrayList<FoodReviewModel> foodReviewModels = new ArrayList<>();
        foodReviewModels.clear();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Food_Donate").child(userKey).child(donateNode).child("Review");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        String ReviewKey = dataSnapshot.getKey();
                        DatabaseReference dbReview = dbRef.child(ReviewKey);
                        dbReview.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snap) {
                                String Name = snap.child("name").getValue().toString();
                                String Review = snap.child("review").getValue().toString();
                                Log.d("DataFood",Name + " " +Review);
                                foodReviewCard = new FoodReviewModel();
                                String Key = snap.getKey();
                                if (Key.equals(User)){
                                    foodReviewCard.setName("by you");
                                }else {
                                    foodReviewCard.setName("by "+Name);
                                }
                                foodReviewCard.setReview(Review);
                                foodReviewCard.setUserKey(userKey);
                                foodReviewCard.setDonateNode(donateNode);
                                foodReviewCard.setRId(ReviewKey);
                                foodReviewModels.add(foodReviewCard);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }else {
                    foodReviewCard = new FoodReviewModel();
                    foodReviewCard.setUserKey(null);
                    foodReviewModels.add(foodReviewCard);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return foodReviewModels;
    }

    @Override
    public int getItemCount() {
        //display Limited data in Recycler View
        /*if(foodModels.size() > limit){
            return limit;
        }
        else
        {
            return foodModels.size();
        }*/
        return foodModels.size()-1;
    }

    public static class FoodReviewModel{
        String Name,Review,RId,UserKey,DonateNode;

        public FoodReviewModel(){}

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getReview() {
            return Review;
        }

        public void setReview(String review) {
            Review = review;
        }

        public String getRId() {
            return RId;
        }

        public void setRId(String RId) {
            this.RId = RId;
        }

        public String getUserKey() {
            return UserKey;
        }

        public void setUserKey(String userKey) {
            UserKey = userKey;
        }

        public String getDonateNode() {
            return DonateNode;
        }

        public void setDonateNode(String donateNode) {
            DonateNode = donateNode;
        }
    }

    public static class FoodReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private ArrayList<FoodReviewModel>foodReviewModels;
        Context mContext;

        public FoodReviewAdapter(Context context) {
            mContext = context;
        }

        public FoodReviewAdapter(ArrayList<FoodReviewModel> dataSet, Context mContext) {
            this.foodReviewModels = dataSet;
            this.mContext = mContext;
        }

        class FoodReviewHolder extends RecyclerView.ViewHolder {
            CardView FoodReview;
            TextView Name,Review,RId,UserKey,DonateNode,NoReview;
            public FoodReviewHolder(@NonNull View itemView) {
                super(itemView);
                this.Name = itemView.findViewById(R.id.ReviewerName);
                this.Review = itemView.findViewById(R.id.Review);
                this.RId = itemView.findViewById(R.id.RId);
                this.UserKey = itemView.findViewById(R.id.UserKey);
                this.DonateNode = itemView.findViewById(R.id.DonateNode);
                this.NoReview = itemView.findViewById(R.id.NoReview);
                this.FoodReview = itemView.findViewById(R.id.FoodReviewCard);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.foodreviewrow_layout, null);
            return new FoodReviewAdapter.FoodReviewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            FoodReviewAdapter.FoodReviewHolder foodReviewHolder = (FoodReviewAdapter.FoodReviewHolder) holder;
            foodReviewHolder.RId.setText(foodReviewModels.get(position).getRId());
            foodReviewHolder.Name.setText(foodReviewModels.get(position).getName());
            foodReviewHolder.Review.setText(foodReviewModels.get(position).getReview());
            foodReviewHolder.UserKey.setText(foodReviewModels.get(position).getUserKey());
            foodReviewHolder.DonateNode.setText(foodReviewModels.get(position).getDonateNode());
            String donateNode = foodReviewModels.get(position).getDonateNode();

            if (TextUtils.isEmpty(donateNode)){
                foodReviewHolder.FoodReview.setVisibility(View.GONE);
                foodReviewHolder.NoReview.setVisibility(View.VISIBLE);
            }else if (foodReviewHolder.NoReview.getVisibility() == View.VISIBLE){
                foodReviewHolder.NoReview.setVisibility(View.GONE);
            }

            ((FoodReviewHolder) holder).FoodReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    String Usr = Functions.getUserID();
                    
                    if (foodReviewHolder.UserKey.equals(Usr)){
                        builder.setCancelable(false)
                                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String userKey = foodReviewModels.get(position).getUserKey();
                                        String donateNode = foodReviewModels.get(position).getDonateNode();
                                        String RId = foodReviewModels.get(position).getRId();

                                        foodReviewHolder.FoodReview.setVisibility(View.VISIBLE);
                                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Food_Donate").child(userKey).child(donateNode).child("Review").child(RId);
                                        dbRef.removeValue();

                                        foodReviewModels.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position,foodReviewModels.size());
                                        Toasty.success(mContext, "Review Deleted Successfully..!!!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                    }else {
                        builder.setCancelable(false)
                                .setPositiveButton("Mark as spam", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String userKey = foodReviewModels.get(position).getUserKey();
                                        String donateNode = foodReviewModels.get(position).getDonateNode();
                                        String RId = foodReviewModels.get(position).getRId();

//                                    foodReviewHolder.FoodReview.setVisibility(View.VISIBLE);
//                                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Food_Donate").child(userKey).child(donateNode).child("Review").child(RId);
//                                    dbRef.removeValue();

                                        foodReviewModels.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, foodReviewModels.size());
                                        Toasty.success(mContext, "Marked As SPAM Successfully..!!!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                    }
                    builder.setTitle("ACTION");
                    builder.setMessage("Choose Action For This Review");
                    builder.show();
                }
            });
        }


        @Override
        public int getItemCount() {
            return foodReviewModels.size();
        }
    }
}
