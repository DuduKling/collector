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

import com.dudukling.collector.imageActivity;
import com.dudukling.collector.R;
import com.dudukling.collector.albumActivity;
import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.formActivity;
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

        Bitmap smallerBitmap = resize(bitmap, 200, 200);

        //Bitmap smallerBitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, true);
        holder.imageViewAlbum.setImageBitmap(smallerBitmap);
        holder.imageViewAlbum.setScaleType(ImageView.ScaleType.FIT_CENTER);

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

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent goToImageActivity = new Intent(context, imageActivity.class);
                    goToImageActivity.putExtra("image_url", imagesList.get(position))
                            .putExtra("position", Integer.toString(position+1));
                    context.startActivity(goToImageActivity);
                }
            });

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

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }
}
