package com.dudukling.collector.util;

import android.content.Context;
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

import com.dudukling.collector.R;
import com.dudukling.collector.albumActivity;
import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.model.Sample;

import java.util.ArrayList;
import java.util.List;


public class recyclerAlbumAdapter extends RecyclerView.Adapter {
    private final Sample sample;
    private final String type;
    private List<String> imagesList;
    private Context context;

    public recyclerAlbumAdapter(Sample sample, String type, albumActivity albumActivity) {
        this.context = albumActivity;
        imagesList = sample.getImagesList();
        this.sample = sample;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_item, parent, false);

        return new albumViewHolder(view);
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

    public class albumViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private final ImageView imageViewAlbum;

        albumViewHolder(View view) {
            super(view);
            imageViewAlbum = view.findViewById(R.id.imageViewAlbum);

            if(type != null && type.equals("edit")){
                view.setOnCreateContextMenuListener(this);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem menuDelete = menu.add("Deletar imagem");
            menuDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    int sampleID  = sample.getId();

                    List<String> imageToDelete = new ArrayList<>();
                    imageToDelete.add(sample.getImagesList().get(position));

                    sampleDAO dao = new sampleDAO(context);
                    int imageID = dao.getImageIdDB(sample.getImagesList().get(position));
                    dao.deleteImageFromDB(sampleID, imageID);

                    imagesList = dao.getImagesDB(sampleID);
                    dao.close();

                    notifyDataSetChanged();

                    Sample sampleToDelete = new Sample();
                    sampleToDelete.setImagesList(imageToDelete);
                    formHelper.deleteImagesFromPhoneMemory(sampleToDelete);

                    return false;
                }
            });
        }
    }
}
