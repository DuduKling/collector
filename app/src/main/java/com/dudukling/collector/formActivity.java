package com.dudukling.collector;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.dudukling.collector.util.cameraController;
import com.dudukling.collector.util.formHelper;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.dudukling.collector.util.gpsController.GPS_REQUEST_CODE;
import static com.dudukling.collector.util.speechController.MIC_REQUEST_CODE;

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

    private cameraController cameraControl;
    private boolean saved = false;


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

        cameraControl = new cameraController(formActivity.this, helperForm.getSample(imagesList));
        cameraControl.setCameraActions();
    }

    private void setFormEdit() {
        setTitle(EDIT_TITLE +sample.getId());
        helperForm = new formHelper(this, "edit", sample);

        cameraControl = new cameraController(formActivity.this, helperForm.getSample(imagesList));
        cameraControl.setCameraActions();

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
                saved = true;
                if(helperForm.validateForm()){
                    Sample sampleSave = helperForm.getSample(imagesList);

                    if(sample == null){
                        //Toast.makeText(formActivity.this, "insert", Toast.LENGTH_SHORT).show();
                        dao.insert(sampleSave);
                    }else{
                        //Toast.makeText(formActivity.this, "edit: " + String.valueOf(sample.getId()), Toast.LENGTH_SHORT).show();
                        dao.edit(sampleSave);
                    }
                    //Toast.makeText(formActivity.this, "Salvando: " + sample.getSpecies() + " !", Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(formActivity.this, "Please, fill all the required fields!", Toast.LENGTH_LONG).show();
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
        dao.close();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == GPS_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_DENIED) {
                formHelper.gpsControl.setGPSValues(formActivity.this);

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
                cameraControl.startCamera();
            }
        }

        if(requestCode == MIC_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                helperForm.speechControl.disableSpeechButtons();
                Toast.makeText(this, "No Speech-to-Text", Toast.LENGTH_SHORT).show();
            }
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                helperForm.setSpeech();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        Sample sampleSave = helperForm.getSample(imagesList);
        savedInstanceState.putSerializable("sample", sampleSave);
        if(cameraControl!=null){
            savedInstanceState.putSerializable("photoPath", cameraControl.getPhotoPath());
            savedInstanceState.putSerializable("photoFile", cameraControl.getPhotoFile());
        }
        savedInstanceState.putSerializable("imagesList", (Serializable) imagesList);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Sample sampleSaved = (Sample) savedInstanceState.getSerializable("sample");

        if(cameraControl!=null) {
            String photoPath = (String) savedInstanceState.getSerializable("photoPath");
            cameraControl.setPhotoPath(photoPath);
            File photoFile = (File) savedInstanceState.getSerializable("photoFile");
            cameraControl.setPhotoFile(photoFile);
        }

        imagesList = (List<String>) savedInstanceState.getSerializable("imagesList");
        helperForm.fillForm(sampleSaved);

        //helperForm.startMaps();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                ImageView imageViewForm = findViewById(R.id.imageViewSampleForm);
                Bitmap bitmap = BitmapFactory.decodeFile(cameraControl.getPhotoFile().toString());
                Bitmap smallerBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);

                imageViewForm.setImageBitmap(smallerBitmap);
                imageViewForm.setScaleType(ImageView.ScaleType.CENTER_CROP);


                imagesList.add(cameraControl.getPhotoFile().toString());

                //Toast.makeText(formActivity.this, ""+photoFile.toString(), Toast.LENGTH_SHORT).show();
            }
        }else{
            if (requestCode == CAMERA_REQUEST_CODE) {
                cameraControl.deleteTempFromPhoneMemory(cameraControl.getPhotoFile().toString());
            }
        }
        if(resultCode == Activity.RESULT_CANCELED){
            if(requestCode == ALBUM_RESULT_CODE){
                //Toast.makeText(formActivity.this, "Voltaaaa", Toast.LENGTH_LONG).show();

//                sampleDAO dao = new sampleDAO(formActivity.this);
//                int savedSampleID = sample.getId();
//                List<Sample> samples = dao.getSamples();
//                sample = samples.get(savedSampleID - 1);
//                helperForm.fillForm(sample);
//                dao.close();
            }
        }

        //Toast.makeText(formActivity.this, ""+imagesList, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helperForm.gpsControl.disableGPS();
        if(isFinishing()){
            if(!saved && cameraControl!=null){
                helperForm.deleteImagesListFromPhoneMemory(imagesList, cameraControl);
            }
        }
    }
}
