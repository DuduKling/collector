package com.dudukling.collector;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.collector.dao.sampleDAO;
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

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        aViewHolder holder = (aViewHolder) viewHolder;
        Sample sample  = samples.get(position);

        holder.viewId.setText("#"+sample.getId());
        holder.viewSpecies.setText(sample.getSpecies());
        holder.viewDate.setText(sample.getDate());
    }

    @Override
    public int getItemCount() {
        return samples.size();
    }

    class aViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        final TextView viewId;
        final TextView viewDate;
        final TextView viewSpecies;

        public aViewHolder(View view) {
            super(view);
            viewId = view.findViewById(R.id.textViewId);
            viewSpecies = view.findViewById(R.id.textViewSpecies);
            viewDate = view.findViewById(R.id.textViewDate);

            view.setOnCreateContextMenuListener(this);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Sample sample  = samples.get(position);

                    Intent goToFormActivity = new Intent(context, formActivity.class);
                    goToFormActivity.putExtra("sample", sample)
                                    .putExtra("type", "readOnly");
                    context.startActivity(goToFormActivity);
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
            //menu.setHeaderTitle("Selecione a ação:");

            MenuItem menuEdit = menu.add("Editar");
            menuEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    Sample sample  = samples.get(position);

                    Intent goToFormActivity = new Intent(context, formActivity.class);
                    goToFormActivity.putExtra("sample", sample)
                                    .putExtra("type", "edit");
                    context.startActivity(goToFormActivity);

                    return false;
                }
            });

            MenuItem menuDelete = menu.add("Deletar");
            menuDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    Sample sample  = samples.get(position);

                    sampleDAO dao = new sampleDAO(context);
                    dao.delete(sample);
                    samples = dao.getSamples();
                    dao.close();

                    notifyDataSetChanged();

                    return false;
                }
            });
        }

    }
}
