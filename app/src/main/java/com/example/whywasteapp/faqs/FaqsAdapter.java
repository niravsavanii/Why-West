package com.example.whywasteapp.faqs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whywasteapp.R;

import java.util.ArrayList;

public class FaqsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<FaqsModel> faqModels;
    Context mContext;

    public FaqsAdapter(Context context) {
        mContext = context;
    }

    public FaqsAdapter(ArrayList<FaqsModel> faqModels, Context mContext) {
        this.faqModels = faqModels;
        this.mContext = mContext;
    }

    public static class FaqsHolder extends RecyclerView.ViewHolder {
        TextView Question,Answer,Tag,Key;

        public FaqsHolder(View itemView) {
            super(itemView);
            this.Key = itemView.findViewById(R.id.Key);
            this.Question = itemView.findViewById(R.id.Question);
            this.Answer = itemView.findViewById(R.id.Answer);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.faqsrow, null);
        return new FaqsAdapter.FaqsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FaqsHolder faqHolder = (FaqsHolder) holder;
        faqHolder.Key.setText(faqModels.get(position).getKey());
        faqHolder.Question.setText(faqModels.get(position).getQuestion());
        faqHolder.Answer.setText(faqModels.get(position).getAnswer());
    }

    @Override
    public int getItemCount() {
        return faqModels.size();
    }
}
