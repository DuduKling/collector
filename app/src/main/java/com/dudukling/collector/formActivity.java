package com.dudukling.collector;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.model.Sample;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class formActivity extends AppCompatActivity {

    public static final String REGISTER_TITLE = "Register";
    public static final String REGISTRY_TITLE = "Registry #";
    public static final String EDIT_TITLE = "Edit #";
    public static final int CAMERA_REQUEST_CODE = 222;

    private formHelper helperForm;
    private String formType;
    String photoPath;
    private Sample sample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        checkTypeOfForm();

        PackageManager pm = getPackageManager();
        boolean rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
        if(rearCam){
            Button cameraButton = findViewById(R.id.buttonCamera);
            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
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

                    //Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //photoPath = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
                    //File photoFile = new File(photoPath);
                    //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    //startActivityForResult(cameraIntent, 222);
                }
            });
        }
    }

    private void checkTypeOfForm() {
        Intent intent = getIntent();
        sample = (Sample) intent.getSerializableExtra("sample");
        formType = (String) intent.getSerializableExtra("type");

        if (formType.equals("new")) {setFormNew(); return;}
        if (formType.equals("edit")) {setFormEdit(); return;}
        if (formType.equals("readOnly")) {setFormReadOnly(); return;}
    }

    private void setFormNew() {
        setTitle(REGISTER_TITLE);
        helperForm = new formHelper(this, "new", sample);
    }

    private void setFormEdit() {
        setTitle(EDIT_TITLE +sample.getId());
        helperForm = new formHelper(this, "edit", sample);
    }

    private void setFormReadOnly() {
        setTitle(REGISTRY_TITLE +sample.getId());
        helperForm = new formHelper(this, "readOnly", sample);
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
                Sample sampleSave = helperForm.getSample();

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
                break;

            case R.id.menu_edit_button:
                // TO DO: enable everything
                Toast.makeText(formActivity.this, "editar", Toast.LENGTH_LONG).show();
                break;

            case R.id.menu_delete_button:
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
            if (grantResults[0] != PackageManager.PERMISSION_DENIED) {
//                formHelper.setGPSValues(formActivity.this);
                formHelper.toggleGPS(formActivity.this);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        Sample sampleSave = helperForm.getSample();
        savedInstanceState.putSerializable("sample", sampleSave);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Sample sampleSaved = (Sample) savedInstanceState.getSerializable("sample");
        helperForm.fillForm(sampleSaved);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
//                ImageView imageViewForm = findViewById(R.id.imageViewSampleForm);
//                Bundle extras = data.getExtras();
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                imageViewForm.setImageBitmap(imageBitmap);

                galleryAddPic();
                Toast.makeText(formActivity.this, "aaaaaaaa", Toast.LENGTH_LONG).show();

//                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
//                Bitmap smallerBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
//                photo.setImageBitmap(smallerBitmap);
//                photo.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Collector_" + sample.getId() + "_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        photoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}
