package com.dudukling.collector.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dudukling.collector.R;
import com.dudukling.collector.collectionActivity;
import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.formActivity;
import com.dudukling.collector.model.Sample;

import java.util.List;

public class recyclerAdapter extends RecyclerView.Adapter {
    private View holderView;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        aViewHolder holder = (aViewHolder) viewHolder;
        Sample sample  = samples.get(position);

        holder.viewId.setText("#"+sample.getId());
        //Toast.makeText(context, "ID: "+sample.getId(), Toast.LENGTH_LONG).show();
        holder.viewSpecies.setText(sample.getSpecies());
        holder.viewDate.setText(sample.getDate());

        List<String> images = sample.getImagesList();
        if(images.size() > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(images.get(0));
            Bitmap smallerBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
            holder.imageViewSample.setImageBitmap(smallerBitmap);
            holder.imageViewSample.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        //Toast.makeText(context, "PATH: "+images.size(), Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return samples.size();
    }

    class aViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        final TextView viewId;
        final TextView viewDate;
        final TextView viewSpecies;
        private final ImageView imageViewSample;

        aViewHolder(View view) {
            super(view);
            holderView = view;
            viewId = view.findViewById(R.id.textViewId);
            viewSpecies = view.findViewById(R.id.textViewSpecies);
            viewDate = view.findViewById(R.id.textViewDate);
            imageViewSample = view.findViewById(R.id.imageViewSample);

            view.setOnCreateContextMenuListener(this);

            // Open ReadOnly form
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
                    formHelper.deleteImagesFromPhoneMemory(sample);
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
