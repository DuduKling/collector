package com.dudukling.collector;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class aViewHolder extends RecyclerView.ViewHolder {
    final TextView idNum;
    final TextView date;
    final TextView species;

    public aViewHolder(View view) {
        super(view);
        idNum = view.findViewById(R.id.textViewId);
        species = view.findViewById(R.id.textViewSpecies);
        date = view.findViewById(R.id.textViewDate);
    }
}
