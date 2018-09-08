package com.dudukling.collector.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;

import com.dudukling.collector.R;
import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.formActivity;
import com.dudukling.collector.model.Sample;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.dudukling.collector.formActivity.CAMERA_PERMISSION_CODE;
import static com.dudukling.collector.formActivity.CAMERA_REQUEST_CODE;

public class cameraController {
    private final Sample sample;
    private File photoFile = null;
    private String photoPath;
    private final formActivity formActivity;
    public File storageDir;

    public cameraController(formActivity formActivity, Sample sample) {
        this.formActivity = formActivity;
        this.sample = sample;
    }

    public void setCameraActions() {
        PackageManager pm = formActivity.getPackageManager();
        boolean rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);

        if(rearCam) {
            FloatingActionButton cameraButton = formActivity.findViewById(R.id.buttonCamera);
            cameraButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    if (formActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(formActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
                    } else {
                        startCamera();
                    }
                }
            });
        }
    }

    public void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(formActivity.getPackageManager()) != null) {

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("TAG 8", "startCamera: ", ex);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(formActivity,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                formActivity.startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyy_HH-mm-ss").format(new Date());

        int id;
        if(sample.getId() == 0) {
            sampleDAO dao = new sampleDAO(formActivity);
            id = dao.lastID()+1;
            dao.close();
        }else{
            id = sample.getId();
        }

        String imageFileName = "id_" + id + "_#" + timeStamp;

        String path = formActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/id_" + id;
        storageDir = new File(path);
        if(!storageDir.exists()){
            boolean created = storageDir.mkdirs();
            Log.i("TAG 9", "createImageFile: "+created);
        }
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,   /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        photoPath = image.getAbsolutePath();
        return image;
    }

    public void deleteTempFromPhoneMemory(String photoFilePath) {
        File file = new File(photoFilePath);
        boolean deleted = file.delete();
        Log.d("TAG3", "delete() called: "+deleted);
    }

    public File getPhotoFile() {
        return photoFile;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoFile(File photoFile) {
        this.photoFile = photoFile;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
    
}
