package com.example.whywasteapp.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whywasteapp.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ReviewModel> reviewModels;
    Context mContext;

    public ReviewAdapter(ArrayList<ReviewModel> dataSet, Context mContext) {
        this.reviewModels = dataSet;
        this.mContext = mContext;
    }

    public static class ReviewHolder extends RecyclerView.ViewHolder{
        TextView Review;
        ImageView FoodImg;
        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            this.Review = itemView.findViewById(R.id.foodReview);
            this.FoodImg = itemView.findViewById(R.id.foodImg);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.profile_userreviews, null);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReviewHolder reviewHolder = (ReviewHolder) holder;
        reviewHolder.Review.setText(reviewModels.get(position).getReview());
        Glide.with(mContext).load(reviewHolder.FoodImg).into(reviewHolder.FoodImg);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return reviewModels.size()-1;
    }
}
