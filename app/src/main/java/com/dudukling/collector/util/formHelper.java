package com.dudukling.collector.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dudukling.collector.R;
import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.formActivity;
import com.dudukling.collector.model.Sample;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class formHelper {
    private static final String REQUIRED_FIELD_ERROR_MSG = "Required Field!";

    private static formActivity activity;
    private final ImageView imageViewSample;
    private int sampleID;
    public static gpsController gpsControl;
    public speechController speechControl;
    private mapsController mapsControl;

    private TextView fieldIDForm;
    private TextView fieldDateForm;

    private TextInputLayout textInputNumber;
    private TextInputLayout textInputSpecies;
    private TextInputLayout textInputSpeciesFamily;
    private TextInputLayout textInputGenus;
    private TextInputLayout textInputCollector;
    private TextInputLayout textInputNotes;
    private TextInputLayout editTextGPSLatitude;
    private TextInputLayout editTextGPSLongitude;
    private TextInputLayout editTextGPSAltitude;

    private EditText fieldSpecies;
    private EditText fieldNumber;
    private EditText fieldSpeciesFamily;
    private EditText fieldGenus;
    private EditText fieldCollector;
    private EditText fieldNotes;
    private EditText fieldEditTextGPSLatitude;
    private EditText fieldEditTextGPSLongitude;
    private EditText fieldEditTextGPSAltitude;
    private EditText fieldEditTextGeoCountry;
    private EditText fieldEditTextGeoState;
    private EditText fieldEditTextGeoCity;
    private EditText fieldEditTextGeoNeighborhood;
    private EditText fieldEditTextLocnotes;

    private final CheckBox checkBoxFlower;
    private final CheckBox checkBoxFruit;


    public formHelper(final formActivity activity, String formType, Sample sample) {
        formHelper.activity = activity;

        fieldIDForm = activity.findViewById(R.id.TextViewIDForm);
        fieldDateForm = activity.findViewById(R.id.TextViewDateForm);
        FloatingActionButton cameraButton = activity.findViewById(R.id.buttonCamera);
        FloatingActionButton albumButton = activity.findViewById(R.id.buttonAlbum);

        checkBoxFlower = activity.findViewById(R.id.checkBoxFlower);
        checkBoxFruit = activity.findViewById(R.id.checkBoxFruit);

        imageViewSample = activity.findViewById(R.id.imageViewSampleForm);

        setFields(activity);

        Button gpsButton = activity.findViewById(R.id.buttonGPS);
        gpsControl = new gpsController(activity, gpsButton, editTextGPSLatitude, editTextGPSLongitude, fieldEditTextGPSAltitude);
        speechControl = new speechController(activity);
        mapsControl = new mapsController(activity);

        cameraButton.setVisibility(View.GONE);
        albumButton.setVisibility(View.GONE);

        checkTypeOfForm(activity, formType, sample, cameraButton, albumButton);
    }

    @SuppressLint("SetTextI18n")
    private void checkTypeOfForm(formActivity activity, String formType, Sample sample, FloatingActionButton cameraButton, FloatingActionButton albumButton) {
        if (formType.equals("readOnly")) {
            disableEditText(fieldNumber);
            disableEditText(fieldSpecies);
            disableEditText(fieldSpeciesFamily);
            disableEditText(fieldGenus);
            disableEditText(fieldCollector);
            disableEditText(fieldNotes);
            disableEditText(fieldEditTextGPSLatitude);
            disableEditText(fieldEditTextGPSLongitude);
            disableEditText(fieldEditTextGPSAltitude);
            disableEditText(fieldEditTextGeoCountry);
            disableEditText(fieldEditTextGeoState);
            disableEditText(fieldEditTextGeoCity);
            disableEditText(fieldEditTextGeoNeighborhood);
            disableEditText(fieldEditTextLocnotes);

            disableCheckBox(checkBoxFlower);
            disableCheckBox(checkBoxFruit);

            sampleID = sample.getId();

            List<String> images = sample.getImagesList();
            if(images.size() > 1) {
                albumButton.setVisibility(View.VISIBLE);
            }

            gpsControl.setGPSButtonGone();
            fillForm(sample);

            mapsControl.startMaps();
            speechControl.disableSpeechButtons();
        }

        if (formType.equals("new")) {
            sampleID = (getLastID(activity) + 1);

            fieldDateForm.setText("Date: " + todayDate());
            fieldIDForm.setText("ID: #" + sampleID);

            cameraButton.setVisibility(View.VISIBLE);

            gpsControl.setGPSValues(activity);
            gpsControl.toggleGPS(activity);
            gpsControl.onChangeLatLong();

            setSpeech();
        }

        if (formType.equals("edit")) {
            sampleID = sample.getId();
            fillForm(sample);

            gpsControl.setActiveGPS(false);
            gpsControl.setGPSButtonBgPlay();

            gpsControl.toggleGPS(activity);
            gpsControl.onChangeLatLong();

            cameraButton.setVisibility(View.VISIBLE);
            List<String> images = sample.getImagesList();
            if(images.size() > 1) {
                albumButton.setVisibility(View.VISIBLE);
            }

            mapsControl.startMaps();

            setSpeech();
        }
    }

    public void setSpeech() {
        speechControl.setSpeechButtons(fieldSpeciesFamily, fieldGenus, fieldSpecies, fieldCollector, fieldNotes, fieldEditTextLocnotes);
    }

    public Sample getSample(List<String> imagesList) {
        Sample sample = new Sample();

        sample.setDate(todayDate());
        sample.setId(sampleID);

        sample.setNumber(fieldNumber.getText().toString());
        sample.setSpeciesFamily(fieldSpeciesFamily.getText().toString());
        sample.setGenus(fieldGenus.getText().toString());
        sample.setSpecies(fieldSpecies.getText().toString());
        sample.setCollector(fieldCollector.getText().toString());
        sample.setNotes(fieldNotes.getText().toString());
        sample.setLocnotes(fieldEditTextLocnotes.getText().toString());

        String latitude = fieldEditTextGPSLatitude.getText().toString();
        String longitude = fieldEditTextGPSLongitude.getText().toString();

        sample.setGPSLatitude(latitude);
        sample.setGPSLongitude(longitude);
        sample.setGPSAltitude(fieldEditTextGPSAltitude.getText().toString());

        sample.setGeoCountry(fieldEditTextGeoCountry.getText().toString());
        sample.setGeoState(fieldEditTextGeoState.getText().toString());
        sample.setGeoCity(fieldEditTextGeoCity.getText().toString());
        sample.setGeoNeighborhood(fieldEditTextGeoNeighborhood.getText().toString());

        if(checkBoxFlower.isChecked()){sample.setHasFlower("x");}else{sample.setHasFlower("");}
        if(checkBoxFruit.isChecked()){sample.setHasFruit("x");}else{sample.setHasFruit("");}

        sampleDAO dao = new sampleDAO(activity);
        List<String> imagesListDB = dao.getImagesDB(sample.getId());
        dao.close();

        List<String> newestImageList = new ArrayList<>();
        newestImageList.addAll(imagesListDB);
        newestImageList.addAll(imagesList);

        sample.setImagesList(newestImageList);

        //Toast.makeText(activity,""+formActivity.imagesList,Toast.LENGTH_LONG).show();

        return sample;
    }

    private void setFields(formActivity activity) {
        textInputNumber = activity.findViewById(R.id.Number);
        fieldNumber = textInputNumber.getEditText();
        setValidateEmpty(textInputNumber);

        textInputSpeciesFamily = activity.findViewById(R.id.editTextSpeciesFamily);
        fieldSpeciesFamily = textInputSpeciesFamily.getEditText();
        setValidateEmpty(textInputSpeciesFamily);

        textInputGenus = activity.findViewById(R.id.editTextGenus);
        fieldGenus = textInputGenus.getEditText();
        setValidateEmpty(textInputGenus);

        textInputSpecies = activity.findViewById(R.id.editTextSpecies);
        fieldSpecies = textInputSpecies.getEditText();
        setValidateEmpty(textInputSpecies);

        textInputCollector = activity.findViewById(R.id.editTextCollector);
        fieldCollector = textInputCollector.getEditText();
        setValidateEmpty(textInputCollector);

        textInputNotes = activity.findViewById(R.id.editTextNotes);
        fieldNotes = textInputNotes.getEditText();
        setValidateEmpty(textInputNotes);

        editTextGPSLatitude = activity.findViewById(R.id.editTextGPSLatitude);
        fieldEditTextGPSLatitude = editTextGPSLatitude.getEditText();

        editTextGPSLongitude = activity.findViewById(R.id.editTextGPSLongitude);
        fieldEditTextGPSLongitude = editTextGPSLongitude.getEditText();

        editTextGPSAltitude = activity.findViewById(R.id.editTextGPSAltitude);
        fieldEditTextGPSAltitude = editTextGPSAltitude.getEditText();
        setValidateEmpty(editTextGPSAltitude);

        TextInputLayout editTextGeoCountry = activity.findViewById(R.id.editTextGeoCountry);
        fieldEditTextGeoCountry = editTextGeoCountry.getEditText();
        setValidateEmpty(editTextGeoCountry);

        TextInputLayout editTextGeoState = activity.findViewById(R.id.editTextGeoState);
        fieldEditTextGeoState = editTextGeoState.getEditText();
        setValidateEmpty(editTextGeoState);

        TextInputLayout editGeoTextCity = activity.findViewById(R.id.editTextGeoCity);
        fieldEditTextGeoCity = editGeoTextCity.getEditText();
        setValidateEmpty(editGeoTextCity);

        TextInputLayout editTextGeoNeighborhood = activity.findViewById(R.id.editTextGeoNeighborhood);
        fieldEditTextGeoNeighborhood = editTextGeoNeighborhood.getEditText();
        setValidateEmpty(editTextGeoNeighborhood);

        TextInputLayout editTextGeoLocnotes = activity.findViewById(R.id.editTextLocnotes);
        fieldEditTextLocnotes = editTextGeoLocnotes.getEditText();
        setValidateEmpty(editTextGeoLocnotes);

    }

    public void fillForm(Sample sample) {
        fieldIDForm.setText("ID: #" + sample.getId());
        fieldDateForm.setText("Date: " + sample.getDate());

        fieldSpecies.setText(sample.getSpecies());
        fieldNumber.setText(sample.getNumber());
        fieldSpeciesFamily.setText(sample.getSpeciesFamily());
        fieldGenus.setText(sample.getGenus());
        fieldCollector.setText(sample.getCollector());
        fieldEditTextLocnotes.setText(sample.getLocnotes());
        fieldNotes.setText(sample.getNotes());

        fieldEditTextGPSLatitude.setText(sample.getGPSLatitude());
        fieldEditTextGPSLongitude.setText(sample.getGPSLongitude());
        fieldEditTextGPSAltitude.setText(sample.getGPSAltitude());

        fieldEditTextGeoCountry.setText(sample.getGeoCountry());
        fieldEditTextGeoState.setText(sample.getGeoState());
        fieldEditTextGeoCity.setText(sample.getGeoCity());
        fieldEditTextGeoNeighborhood.setText(sample.getGeoNeighborhood());

        if(sample.getHasFlower()!=null){if(sample.getHasFlower().equals("x")){checkBoxFlower.setChecked(true);}}
        if(sample.getHasFruit()!=null){if(sample.getHasFruit().equals("x")){checkBoxFruit.setChecked(true);}}

        List<String> images = sample.getImagesList();
        if(images!=null && images.size() > 0 ) {
            Bitmap bitmap = BitmapFactory.decodeFile(images.get(0));

            Bitmap smallerBitmap = null;
            if (bitmap != null) {
                smallerBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
                imageViewSample.setImageBitmap(smallerBitmap);
                imageViewSample.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
    }

    public boolean validateForm() {
        if(fieldIsEmpty(textInputNumber)){return false;}
        if(fieldIsEmpty(textInputSpecies)){return false;}
        if(fieldIsEmpty(textInputSpeciesFamily)){return false;}
        if(fieldIsEmpty(textInputGenus)){return false;}
        if(fieldIsEmpty(textInputCollector)){return false;}
        if(fieldIsEmpty(textInputNotes)){return false;}
        if(fieldIsEmpty(editTextGPSLatitude)){return false;}
        if(fieldIsEmpty(editTextGPSLongitude)){return false;}
        if(fieldIsEmpty(editTextGPSAltitude)){return false;}

        return true;
    }

    private boolean fieldIsEmpty(TextInputLayout textInputCampo) {
        EditText campo = textInputCampo.getEditText();
        assert campo != null;
        String text = campo.getText().toString();
        if(text.isEmpty()) {
            textInputCampo.setError(REQUIRED_FIELD_ERROR_MSG);
        }else{
            textInputCampo.setError(null);
            textInputCampo.setErrorEnabled(false);
        }
        return text.isEmpty();
    }

    private void setValidateEmpty(final TextInputLayout textInputCampo){
        final EditText campo = textInputCampo.getEditText();
        assert campo != null;
        campo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                textInputCampo.setError(null);
                textInputCampo.setErrorEnabled(false);
                if(!hasFocus){
                    String text = campo.getText().toString();
                    if(text.isEmpty()){
                        textInputCampo.setError(REQUIRED_FIELD_ERROR_MSG);
                    }
                }else{
                    textInputCampo.setError(null);
                    textInputCampo.setErrorEnabled(false);
                }
            }
        });
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        //editText.setBackgroundColor(Color.TRANSPARENT);
        editText.setTextColor(Color.parseColor("#616161"));
    }

    private void disableCheckBox(CheckBox checkBox) {
        checkBox.setEnabled(false);
    }

    private int getLastID(formActivity activity) {
        sampleDAO dao = new sampleDAO(activity);
        int lastID = dao.lastID();
        dao.close();

        return lastID;
    }

    private String todayDate() {
        Date todayDate = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(todayDate);
    }

    public static void deleteImagesFromPhoneMemory(Sample sample) {
        List<String> imagesListToDelete = sample.getImagesList();
        for(int i = 0; i < imagesListToDelete.size(); i++){
            File file = new File(imagesListToDelete.get(i));
            boolean deleted = file.delete();
            Log.d("TAG4", "delete() called: "+deleted);
        }
    }

    public void deleteImagesListFromPhoneMemory(List<String> imagesList, cameraController cameraControl) {
        for(int i=0; i<imagesList.size(); i++){
            File file = new File(imagesList.get(i));
            boolean deleted = file.delete();
            Log.d("TAG8", "delete() called: "+deleted);
        }
        if(cameraControl.storageDir!=null){
            cameraControl.storageDir.delete();
        }
    }
}
