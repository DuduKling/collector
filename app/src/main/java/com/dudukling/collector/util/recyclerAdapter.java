package com.dudukling.collector.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.collector.R;
import com.dudukling.collector.collectionActivity;
import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.formActivity;
import com.dudukling.collector.model.Sample;

import java.util.ArrayList;
import java.util.List;

public class recyclerAdapter extends RecyclerView.Adapter implements Filterable {
    private List<Sample> samples;
    private List<Sample> samplesFiltered;
    private Context context;

    public recyclerAdapter(List<Sample> samples, Context context) {
        this.samples = samples;
        this.context = context;
        this.samplesFiltered = samples;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.collection_item, parent, false);

        return new aViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        aViewHolder holder = (aViewHolder) viewHolder;
        Sample sample  = samplesFiltered.get(position);

        List<String> images = sample.getImagesList();
        if(images.size() > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(images.get(0));
            Bitmap smallerBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
            holder.imageViewSample.setImageBitmap(smallerBitmap);
            holder.imageViewSample.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else{
            holder.imageViewSample.setImageResource(R.drawable.avatar_leaf);
            holder.imageViewSample.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        holder.viewFamily.setText(sample.getSpeciesFamily());
        holder.viewSpecies.setText(sample.getSpecies());
        holder.viewGenus.setText(sample.getGenus());

        holder.viewNumber.setText("#"+sample.getNumber());
        holder.viewDate.setText(sample.getDate());

        //Toast.makeText(context, "PATH: "+images.size(), Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return samplesFiltered.size();
    }

    class aViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        final TextView viewNumber;
        final TextView viewDate;
        final TextView viewFamily;
        final TextView viewSpecies;
        final TextView viewGenus;
        private final ImageView imageViewSample;

        aViewHolder(View view) {
            super(view);
            imageViewSample = view.findViewById(R.id.imageViewSample);

            viewFamily = view.findViewById(R.id.textViewFamily);
            viewSpecies = view.findViewById(R.id.textViewSpecies);
            viewGenus = view.findViewById(R.id.textViewGenus);

            viewNumber = view.findViewById(R.id.textViewNumber);
            viewDate = view.findViewById(R.id.textViewDate);

            view.setOnCreateContextMenuListener(this);

            // Open ReadOnly form
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Sample sample  = samplesFiltered.get(position);

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
                    Sample sample  = samplesFiltered.get(position);

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
                    Sample sample  = samplesFiltered.get(position);

                    sampleDAO dao = new sampleDAO(context);
                    formHelper.deleteImagesFromPhoneMemory(sample);
                    dao.delete(sample);
                    samplesFiltered = dao.getSamples();
                    dao.close();

                    notifyDataSetChanged();

                    return false;
                }
            });
        }

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                List<Sample> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered = samples;
                } else {
                    for (Sample sample : samples) {
                        if (sample.getSpeciesFamily().toLowerCase().contains(query.toLowerCase()) ||
                                sample.getSpecies().toLowerCase().contains(query.toLowerCase()) ||
                                sample.getGenus().toLowerCase().contains(query.toLowerCase()) ||
                                sample.getNumber().contains(query)
                                ) {
                            filtered.add(sample);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                samplesFiltered = (List<Sample>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
