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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

public class DonateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<DonateModel> donateModels;
    Context mContext;

    public DonateAdapter(Context context) {
        mContext = context;
    }

    public DonateAdapter(ArrayList<DonateModel> dataSet, Context mContext) {
        this.donateModels = dataSet;
        this.mContext = mContext;
    }

    public static class DonateHolder extends RecyclerView.ViewHolder {
        TextView FoodName,Date,FoodQty;
        ImageView FoodImg;

        public DonateHolder(View itemView) {
            super(itemView);
            this.FoodImg = itemView.findViewById(R.id.foodImg);
            this.FoodName = itemView.findViewById(R.id.foodName);
            this.FoodQty = itemView.findViewById(R.id.foodQty);
            this.Date = itemView.findViewById(R.id.donateDate);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.profile_userdonates, null);
        return new DonateHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DonateAdapter.DonateHolder donateHolder = (DonateAdapter.DonateHolder) holder;
        donateHolder.FoodName.setText(donateModels.get(position).getFoodName());
        donateHolder.FoodQty.setText("Food Quantity : "+donateModels.get(position).getFoodQty());
        donateHolder.Date.setText(donateModels.get(position).getDate());
        Glide.with(mContext).load(donateModels.get(position).getImageName()).into(donateHolder.FoodImg);
    }

    @Override
    public int getItemCount() {
        return donateModels.size()-1;
    }
}


/*public class DonateAdapter extends FirebaseRecyclerAdapter<DonateModel,DonateAdapter.DonateHolder> {

    public DonateAdapter(@NonNull FirebaseRecyclerOptions<DonateModel> options) {
        super(options);
    }

    public static class DonateHolder extends RecyclerView.ViewHolder {
        TextView FoodName,DonatorName,Date;
        ImageView FoodImg;

        public DonateHolder(View itemView) {
            super(itemView);
            FoodImg = itemView.findViewById(R.id.foodImg);
            FoodName = itemView.findViewById(R.id.foodName);
            Date = itemView.findViewById(R.id.donateDate);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull DonateHolder holder, int i, @NonNull DonateModel model) {
        Glide.with(holder.FoodImg.getContext()).load(model.imageName).into(holder.FoodImg);
        holder.FoodName.setText(model.getFoodName());
        holder.Date.setText(model.getDate());
    }

    @NonNull
    @Override
    public DonateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_userdonates,parent,false);
        return new DonateHolder(view);
    }

}*/
