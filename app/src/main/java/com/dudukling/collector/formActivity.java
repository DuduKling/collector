package com.dudukling.collector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

public class formActivity extends AppCompatActivity {

    public static final String REGISTER_TITLE = "Register";
    public static final String REGISTRY_TITLE = "Registry #";
    public static final String EDIT_TITLE = "Edit #";
    private formHelper helperForm;
    private String formType;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        checkTypeOfForm();


        Button cameraButton = findViewById(R.id.buttonCamera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                photoPath = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
//                File photoFile = new File(photoPath);
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, 222);
            }
        });
    }

    private void checkTypeOfForm() {
        Intent intent = getIntent();
        Sample sample = (Sample) intent.getSerializableExtra("sample");
        formType = (String) intent.getSerializableExtra("type");

        if (formType.equals("new")) {setFormNew(sample); return;}
        if (formType.equals("edit")) {setFormEdit(sample); return;}
        if (formType.equals("readOnly")) {setFormReadOnly(sample); return;}
    }

    private void setFormNew(Sample sample) {
        setTitle(REGISTER_TITLE);
        helperForm = new formHelper(this, "new", sample);
    }

    private void setFormEdit(Sample sample) {
        setTitle(EDIT_TITLE +sample.getId());
        helperForm = new formHelper(this, "edit", sample);
    }

    private void setFormReadOnly(Sample sample) {
        setTitle(REGISTRY_TITLE +sample.getId());
        helperForm = new formHelper(this, "readOnly", sample);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!formType.equals("readOnly")){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_form, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menu_save_button:
                Sample sample = helperForm.getSample();

                sampleDAO dao = new sampleDAO(formActivity.this);
                //Toast.makeText(formActivity.this, "edit/insert: " + String.valueOf(sample.getId()), Toast.LENGTH_SHORT).show();
                if(sample.getId() != 0){
                    dao.edit(sample);
                } else {
                    dao.insert(sample);
                }
                dao.close();

                //Toast.makeText(formActivity.this, "Salvando: " + sample.getSpecies() + " !", Toast.LENGTH_LONG).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 111){
            formHelper.setGPSValues(formActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == 222) {
//                ImageView photo = findViewById(R.id.imageViewSampleForm);
//                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
//                Bitmap smallerBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
//                photo.setImageBitmap(smallerBitmap);
//                photo.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
    }
}
