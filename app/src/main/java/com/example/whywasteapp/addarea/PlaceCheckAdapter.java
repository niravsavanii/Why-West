package com.example.whywasteapp.addarea;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whywasteapp.R;

import java.util.ArrayList;

public class PlaceCheckAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<PlaceModel> placeCheckModels;
    Context mContext;

    public PlaceCheckAdapter(Context context) {
        mContext = context;
    }

    public PlaceCheckAdapter(ArrayList<PlaceModel> dataSet, Context mContext) {
        this.placeCheckModels = dataSet;
        this.mContext = mContext;
    }

    public static class PlaceCheckHolder extends RecyclerView.ViewHolder {

        TextView Street,Area,City,State,Date,Status,Key,Pos;

        public PlaceCheckHolder(View itemView) {
            super(itemView);
            this.Status = itemView.findViewById(R.id.Status);
            this.Key = itemView.findViewById(R.id.Key);
            this.Pos = itemView.findViewById(R.id.Pos);
            this.Street = itemView.findViewById(R.id.Street);
            this.Area = itemView.findViewById(R.id.Area);
            this.City = itemView.findViewById(R.id.City);
            this.State = itemView.findViewById(R.id.State);
            this.Date = itemView.findViewById(R.id.Date);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.placerow, null);
        return new PlaceCheckHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PlaceCheckHolder placeCheckHolder = (PlaceCheckHolder) holder;
        placeCheckHolder.Key.setText(placeCheckModels.get(position).getKey());
        placeCheckHolder.Pos.setText(placeCheckModels.get(position).getPos());
        placeCheckHolder.Street.setText(placeCheckModels.get(position).getStreet());
        placeCheckHolder.Area.setText(placeCheckModels.get(position).getArea());
        placeCheckHolder.City.setText(placeCheckModels.get(position).getCity());
        placeCheckHolder.State.setText(placeCheckModels.get(position).getState());
        placeCheckHolder.Date.setText(placeCheckModels.get(position).getDate());
    }

    @Override
    public int getItemCount() { return placeCheckModels.size(); }

    @Override
    public int getItemViewType(int position) { return position; }
}
