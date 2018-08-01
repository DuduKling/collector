package com.dudukling.collector;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dudukling.collector.model.Sample;

import java.util.List;

class recyclerAdapter extends RecyclerView.Adapter {
    private List<Sample> samples;
    private Context context;

    public recyclerAdapter(List<Sample> samples, Context context) {
        this.samples = samples;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.collection_item, parent, false);
        aViewHolder holder = new aViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        aViewHolder holder = (aViewHolder) viewHolder;
        Sample sample  = samples.get(position);

        holder.idNum.setText(sample.getIdNum());
        holder.species.setText(sample.getSpecies());
        holder.date.setText(sample.getDate());
    }

    @Override
    public int getItemCount() {
        return samples.size();
    }
}
