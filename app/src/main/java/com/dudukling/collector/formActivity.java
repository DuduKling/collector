package com.dudukling.collector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.model.Sample;
import com.dudukling.collector.util.formHelper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class formActivity extends AppCompatActivity {

    public static final String REGISTER_TITLE = "Register";
    public static final String REGISTRY_TITLE = "Registry #";
    public static final String EDIT_TITLE = "Edit #";
    public static final int CAMERA_REQUEST_CODE = 222;
    public static final int CAMERA_PERMISSION_CODE = 333;
    public static final int ALBUM_RESULT_CODE = 444;

    private Sample sample;
    private formHelper helperForm;
    private String formType;
    public List<String> imagesList = new ArrayList<>();
    String photoPath;
    File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }

        checkTypeOfForm();
    }

    private void checkTypeOfForm() {
        Intent intent = getIntent();
        sample = (Sample) intent.getSerializableExtra("sample");
        formType = (String) intent.getSerializableExtra("type");

        if (formType.equals("new")) {setFormNew(); return;}
        if (formType.equals("edit")) {setFormEdit(); return;}
        if (formType.equals("readOnly")) {setFormReadOnly();}
    }

    private void setFormNew() {
        setTitle(REGISTER_TITLE);
        helperForm = new formHelper(this, "new", sample);
        setCameraActions();
    }

    private void setFormEdit() {
        setTitle(EDIT_TITLE +sample.getId());
        helperForm = new formHelper(this, "edit", sample);
        setCameraActions();
        setAlbumAction();
    }

    private void setFormReadOnly() {
        setTitle(REGISTRY_TITLE +sample.getId());
        helperForm = new formHelper(this, "readOnly", sample);

        setAlbumAction();
    }

    private void setAlbumAction() {
        FloatingActionButton albumButton = findViewById(R.id.buttonAlbum);
        albumButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent goToAlbum = new Intent(formActivity.this, albumActivity.class);

                if(formType.equals("edit")){
                    goToAlbum.putExtra("type", "edit");
                }
                goToAlbum.putExtra("sample", sample);
                startActivityForResult(goToAlbum, ALBUM_RESULT_CODE);
            }
        });
    }

    private void setCameraActions() {
        PackageManager pm = getPackageManager();
        boolean rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);

        if(rearCam) {
            FloatingActionButton cameraButton = findViewById(R.id.buttonCamera);
            cameraButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(formActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
                    } else {
                        startCamera();
                    }
                }
            });
        }
    }

    private void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(formActivity.this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyy_HH-mm-ss").format(new Date());
        String imageFileName = "Collector_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,   /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        photoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(formType.equals("readOnly")){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_form_readonly, menu);
        }else{
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_form, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        sampleDAO dao = new sampleDAO(formActivity.this);

        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;

            case R.id.menu_save_button:
                if(helperForm.validateForm()){
                    Sample sampleSave = helperForm.getSample(imagesList);

                    if(sample == null){
                        //Toast.makeText(formActivity.this, "insert", Toast.LENGTH_SHORT).show();
                        dao.insert(sampleSave);
                    }else{
                        //Toast.makeText(formActivity.this, "edit: " + String.valueOf(sample.getId()), Toast.LENGTH_SHORT).show();
                        dao.edit(sampleSave);
                    }
                    dao.close();

                    //Toast.makeText(formActivity.this, "Salvando: " + sample.getSpecies() + " !", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

            case R.id.menu_edit_button:
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("sample", sample)
                        .putExtra("type", "edit");
                finish();
                overridePendingTransition(0, 0);
                //Toast.makeText(formActivity.this, "editar", Toast.LENGTH_LONG).show();

                startActivity(intent);
                overridePendingTransition(0, 0);

                break;

            case R.id.menu_delete_button:
                formHelper.deleteImagesFromPhoneMemory(sample);
                dao.delete(sample);
                //Toast.makeText(formActivity.this, "Deleta ai!", Toast.LENGTH_LONG).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 111){
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_DENIED) {
                formHelper.setGPSValues(formActivity.this);

                // A permissão do GPS é chamada antes, e a do microfone acaba não sendo chamado. Por isso, é chamado aqui:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},555);
                }
            }else{
                Toast.makeText(this, "No GPS", Toast.LENGTH_SHORT).show();

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},555);
                }
            }
        }

        if(requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_DENIED) {
                startCamera();
            }
        }

        if(requestCode == 555){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                helperForm.disableSpeechButtons();
                Toast.makeText(this, "No Speech-to-Text", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        Sample sampleSave = helperForm.getSample(imagesList);
        savedInstanceState.putSerializable("sample", sampleSave);
        savedInstanceState.putSerializable("photoPath", photoPath);
        savedInstanceState.putSerializable("photoFile", photoFile);
        savedInstanceState.putSerializable("imagesList", (Serializable) imagesList);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Sample sampleSaved = (Sample) savedInstanceState.getSerializable("sample");
        photoPath = (String) savedInstanceState.getSerializable("photoPath");
        photoFile = (File) savedInstanceState.getSerializable("photoFile");
        imagesList = (List<String>) savedInstanceState.getSerializable("imagesList");
        helperForm.fillForm(sampleSaved);

        //helperForm.startMaps();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                ImageView imageViewForm = findViewById(R.id.imageViewSampleForm);
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.toString());
                Bitmap smallerBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);

                imageViewForm.setImageBitmap(smallerBitmap);
                imageViewForm.setScaleType(ImageView.ScaleType.CENTER_CROP);


                imagesList.add(photoFile.toString());

                //Toast.makeText(formActivity.this, ""+photoFile.toString(), Toast.LENGTH_SHORT).show();
            }
        }else{
            if (requestCode == CAMERA_REQUEST_CODE) {
                deleteTempFromPhoneMemory(photoFile.toString());
            }
        }
        if(resultCode == Activity.RESULT_CANCELED){
            if(requestCode == ALBUM_RESULT_CODE){
                //Toast.makeText(formActivity.this, "Voltaaaa", Toast.LENGTH_LONG).show();

                sampleDAO dao = new sampleDAO(formActivity.this);
                int savedSampleID = sample.getId();
                List<Sample> samples = dao.getSamples();
                sample = samples.get(savedSampleID - 1);
                helperForm.fillForm(sample);
            }
        }

        //Toast.makeText(formActivity.this, ""+imagesList, Toast.LENGTH_LONG).show();
    }

    private void deleteTempFromPhoneMemory(String photoFilePath) {
        File file = new File(photoFilePath);
        boolean deleted = file.delete();
        Log.d("TAG3", "delete() called: "+deleted);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helperForm.disableGPS();
    }
}
