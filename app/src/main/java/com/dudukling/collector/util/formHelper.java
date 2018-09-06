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

    private TextInputLayout textInputCollectorName;
    private TextInputLayout textInputSpecies;
    private TextInputLayout textInputSpeciesFamily;
    private TextInputLayout textInputAuthor;
    private TextInputLayout textInputSampleDescription;
    private TextInputLayout textInputAmbientDescription;
    private TextInputLayout textInputNotes;
    private TextInputLayout editTextGPSLatitude;
    private TextInputLayout editTextGPSLongitude;
    private TextInputLayout editTextGPSAltitude;

    private EditText fieldSpecies;
    private EditText fieldCollectorName;
    private EditText fieldSpeciesFamily;
    private EditText fieldAuthor;
    private EditText fieldSampleDescription;
    private EditText fieldAmbientDescription;
    private EditText fieldNotes;
    private EditText fieldEditTextGPSLatitude;
    private EditText fieldEditTextGPSLongitude;
    private EditText fieldEditTextGPSAltitude;
    private EditText fieldEditTextGeoCountry;
    private EditText fieldEditTextGeoState;
    private EditText fieldEditTextGeoCity;
    private EditText fieldEditTextGeoNeighborhood;
    private EditText fieldEditTextGeoOtherInfo;

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

        Button gpsButton = activity.findViewById(R.id.buttonGPS);;
        gpsControl = new gpsController(activity, gpsButton, fieldEditTextGPSLatitude, fieldEditTextGPSLongitude, fieldEditTextGPSAltitude);
        speechControl = new speechController(activity);
        mapsControl = new mapsController(activity);

        cameraButton.setVisibility(View.GONE);
        albumButton.setVisibility(View.GONE);

        checkTypeOfForm(activity, formType, sample, cameraButton, albumButton);
    }

    @SuppressLint("SetTextI18n")
    private void checkTypeOfForm(formActivity activity, String formType, Sample sample, FloatingActionButton cameraButton, FloatingActionButton albumButton) {
        if (formType.equals("readOnly")) {
            disableEditText(fieldCollectorName);
            disableEditText(fieldSpecies);
            disableEditText(fieldSpeciesFamily);
            disableEditText(fieldAuthor);
            disableEditText(fieldSampleDescription);
            disableEditText(fieldAmbientDescription);
            disableEditText(fieldNotes);
            disableEditText(fieldEditTextGPSLatitude);
            disableEditText(fieldEditTextGPSLongitude);
            disableEditText(fieldEditTextGPSAltitude);
            disableEditText(fieldEditTextGeoCountry);
            disableEditText(fieldEditTextGeoState);
            disableEditText(fieldEditTextGeoCity);
            disableEditText(fieldEditTextGeoNeighborhood);
            disableEditText(fieldEditTextGeoOtherInfo);

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

            speechControl.setSpeechButtons(fieldCollectorName, fieldSpecies, fieldSpeciesFamily, fieldAuthor, fieldSampleDescription, fieldAmbientDescription, fieldNotes);
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

            speechControl.setSpeechButtons(fieldCollectorName, fieldSpecies, fieldSpeciesFamily, fieldAuthor, fieldSampleDescription, fieldAmbientDescription, fieldNotes);
        }
    }

    public Sample getSample(List<String> imagesList) {
        Sample sample = new Sample();

        sample.setDate(todayDate());
        sample.setId(sampleID);

        sample.setSpecies(fieldSpecies.getText().toString());
        sample.setCollectorName(fieldCollectorName.getText().toString());
        sample.setSpeciesFamily(fieldSpeciesFamily.getText().toString());
        sample.setAuthor(fieldAuthor.getText().toString());
        sample.setSampleDescription(fieldSampleDescription.getText().toString());
        sample.setAmbientDescription(fieldAmbientDescription.getText().toString());
        sample.setNotes(fieldNotes.getText().toString());

        String latitude = fieldEditTextGPSLatitude.getText().toString();
        String longitude = fieldEditTextGPSLongitude.getText().toString();

        sample.setGPSLatitude(latitude);
        sample.setGPSLongitude(longitude);
        sample.setGPSAltitude(fieldEditTextGPSAltitude.getText().toString());

        sample.setGeoCountry(fieldEditTextGeoCountry.getText().toString());
        sample.setGeoState(fieldEditTextGeoState.getText().toString());
        sample.setGeoCity(fieldEditTextGeoCity.getText().toString());
        sample.setGeoNeighborhood(fieldEditTextGeoNeighborhood.getText().toString());
        sample.setGeoOtherInfo(fieldEditTextGeoOtherInfo.getText().toString());

        if(checkBoxFlower.isChecked()){sample.setHasFlower("x");}else{sample.setHasFlower("");}
        if(checkBoxFruit.isChecked()){sample.setHasFruit("x");}else{sample.setHasFruit("");}

        sampleDAO dao = new sampleDAO(activity);
        List<String> imagesListDB = dao.getImagesDB(sample.getId());

        List<String> newestImageList = new ArrayList<>();
        newestImageList.addAll(imagesListDB);
        newestImageList.addAll(imagesList);

        sample.setImagesList(newestImageList);

        //Toast.makeText(activity,""+formActivity.imagesList,Toast.LENGTH_LONG).show();

        return sample;
    }

    private void setFields(formActivity activity) {
        textInputCollectorName = activity.findViewById(R.id.editTextCollectorName);
        fieldCollectorName = textInputCollectorName.getEditText();
        setValidateEmpty(textInputCollectorName);

        textInputSpecies = activity.findViewById(R.id.editTextSpecies);
        fieldSpecies = textInputSpecies.getEditText();
        setValidateEmpty(textInputSpecies);

        textInputSpeciesFamily = activity.findViewById(R.id.editTextSpeciesFamily);
        fieldSpeciesFamily = textInputSpeciesFamily.getEditText();
        setValidateEmpty(textInputSpeciesFamily);

        textInputAuthor = activity.findViewById(R.id.editTextAuthor);
        fieldAuthor = textInputAuthor.getEditText();
        setValidateEmpty(textInputAuthor);

        textInputSampleDescription = activity.findViewById(R.id.editTextSampleDescription);
        fieldSampleDescription = textInputSampleDescription.getEditText();
        setValidateEmpty(textInputSampleDescription);

        textInputAmbientDescription = activity.findViewById(R.id.editTextAmbientDescription);
        fieldAmbientDescription = textInputAmbientDescription.getEditText();
        setValidateEmpty(textInputAmbientDescription);

        textInputNotes = activity.findViewById(R.id.editTextNotes);
        fieldNotes = textInputNotes.getEditText();
        setValidateEmpty(textInputNotes);

        editTextGPSLatitude = activity.findViewById(R.id.editTextGPSLatitude);
        fieldEditTextGPSLatitude = editTextGPSLatitude.getEditText();
        setValidateEmpty(editTextGPSLatitude);

        editTextGPSLongitude = activity.findViewById(R.id.editTextGPSLongitude);
        fieldEditTextGPSLongitude = editTextGPSLongitude.getEditText();
        setValidateEmpty(editTextGPSLongitude);

        editTextGPSAltitude = activity.findViewById(R.id.editTextGPSAltitude);
        fieldEditTextGPSAltitude = editTextGPSAltitude.getEditText();
        setValidateEmpty(editTextGPSAltitude);

        TextInputLayout editTextGeoCountry = activity.findViewById(R.id.editTextGeoCountry);
        fieldEditTextGeoCountry = editTextGeoCountry.getEditText();

        TextInputLayout editTextGeoState = activity.findViewById(R.id.editTextGeoState);
        fieldEditTextGeoState = editTextGeoState.getEditText();

        TextInputLayout editGeoTextCity = activity.findViewById(R.id.editTextGeoCity);
        fieldEditTextGeoCity = editGeoTextCity.getEditText();

        TextInputLayout editTextGeoNeighborhood = activity.findViewById(R.id.editTextGeoNeighborhood);
        fieldEditTextGeoNeighborhood = editTextGeoNeighborhood.getEditText();

        TextInputLayout editTextGeoOtherInfo = activity.findViewById(R.id.editTextGeoOtherInfo);
        fieldEditTextGeoOtherInfo = editTextGeoOtherInfo.getEditText();

    }

    public void fillForm(Sample sample) {
        fieldIDForm.setText("ID: #" + sample.getId());
        fieldDateForm.setText("Date: " + sample.getDate());

        fieldSpecies.setText(sample.getSpecies());
        fieldCollectorName.setText(sample.getCollectorName());
        fieldSpeciesFamily.setText(sample.getSpeciesFamily());
        fieldAuthor.setText(sample.getAuthor());
        fieldSampleDescription.setText(sample.getSampleDescription());
        fieldAmbientDescription.setText(sample.getAmbientDescription());
        fieldNotes.setText(sample.getNotes());

        fieldEditTextGPSLatitude.setText(sample.getGPSLatitude());
        fieldEditTextGPSLongitude.setText(sample.getGPSLongitude());
        fieldEditTextGPSAltitude.setText(sample.getGPSAltitude());

        fieldEditTextGeoCountry.setText(sample.getGeoCountry());
        fieldEditTextGeoState.setText(sample.getGeoState());
        fieldEditTextGeoCity.setText(sample.getGeoCity());
        fieldEditTextGeoNeighborhood.setText(sample.getGeoNeighborhood());
        fieldEditTextGeoOtherInfo.setText(sample.getGeoOtherInfo());

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
        if(fieldIsEmpty(textInputCollectorName)){return false;}
        if(fieldIsEmpty(textInputSpecies)){return false;}
        if(fieldIsEmpty(textInputSpeciesFamily)){return false;}
        if(fieldIsEmpty(textInputAuthor)){return false;}
        if(fieldIsEmpty(textInputSampleDescription)){return false;}
        if(fieldIsEmpty(textInputAmbientDescription)){return false;}
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
