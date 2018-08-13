package com.dudukling.collector.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.collector.R;
import com.dudukling.collector.albumActivity;
import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.formActivity;
import com.dudukling.collector.model.Sample;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class recyclerAlbumAdapter extends RecyclerView.Adapter {
    private final int sampleID;
    private List<String> imagesList;
    private Context context;

    public recyclerAlbumAdapter(Sample sample, albumActivity albumActivity) {
        this.context = albumActivity;
        imagesList = sample.getImagesList();
        sampleID = sample.getId();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_item, parent, false);
        albumViewHolder holder = new recyclerAlbumAdapter.albumViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        albumViewHolder holder = (albumViewHolder) viewHolder;

        Bitmap bitmap = BitmapFactory.decodeFile(imagesList.get(position));
        Bitmap smallerBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
        holder.imageViewAlbum.setImageBitmap(smallerBitmap);
        holder.imageViewAlbum.setScaleType(ImageView.ScaleType.CENTER_CROP);

//        sampleDAO dao = new sampleDAO(context);
//        String qtdImgsDB = dao.countImages(sampleID);
//
//        Toast.makeText(context, "QTD IMAGENS: "+imagesList.size() + " | " + qtdImgsDB, Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class albumViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageViewAlbum;

        public albumViewHolder(View view) {
            super(view);
            imageViewAlbum = view.findViewById(R.id.imageViewAlbum);
        }
    }
}
