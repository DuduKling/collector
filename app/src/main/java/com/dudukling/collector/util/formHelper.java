package com.dudukling.collector.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

public class formHelper {
    public static final String REQUIRED_FIELD_ERROR_MSG = "Required Field!";

    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private static boolean activeGPS;
    private final ImageView imageViewSample;
    private static formActivity activity;

    private int sampleID;
    private static Button gpsButton;

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

    private static EditText fieldEditTextGPSLatitude;
    private static EditText fieldEditTextGPSLongitude;
    private static EditText fieldEditTextGPSAltitude;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;


    public formHelper(final formActivity activity, String formType, Sample sample) {
        this.activity = activity;

        fieldIDForm = activity.findViewById(R.id.TextViewIDForm);
        fieldDateForm = activity.findViewById(R.id.TextViewDateForm);
        FloatingActionButton cameraButton = activity.findViewById(R.id.buttonCamera);
        FloatingActionButton albumButton = activity.findViewById(R.id.buttonAlbum);
        gpsButton = activity.findViewById(R.id.buttonGPS);

        imageViewSample = activity.findViewById(R.id.imageViewSampleForm);

        setFields(activity);

        checkTypeOfForm(activity, formType, sample, cameraButton, albumButton);
    }

    private void checkTypeOfForm(formActivity activity, String formType, Sample sample, FloatingActionButton cameraButton, FloatingActionButton albumButton) {
        cameraButton.setVisibility(View.GONE);
        albumButton.setVisibility(View.GONE);

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

            sampleID = sample.getId();

            List<String> images = sample.getImagesList();
            if(images.size() > 1) {
                albumButton.setVisibility(View.VISIBLE);
            }

            gpsButton.setVisibility(View.GONE);
            fillForm(sample);

            startMaps();
            disableSpeechButtons();
        }

        if (formType.equals("new")) {
            sampleID = 0;

            fieldDateForm.setText("Date: " + todayDate());
            fieldIDForm.setText("ID: #" + (getLastID(activity) + 1));

            cameraButton.setVisibility(View.VISIBLE);

            setGPSValues(activity);
            toggleGPS(activity);
            onChangeLatLong();

            setSpeechButtons(activity);
        }

        if (formType.equals("edit")) {
            sampleID = sample.getId();
            fillForm(sample);

            activeGPS = false;
            gpsButton.setBackgroundResource(R.drawable.ic_play);
            toggleGPS(activity);
            onChangeLatLong();

            cameraButton.setVisibility(View.VISIBLE);
            List<String> images = sample.getImagesList();
            if(images.size() > 1) {
                albumButton.setVisibility(View.VISIBLE);
            }

            startMaps();

            setSpeechButtons(activity);
        }
    }

    public void disableSpeechButtons() {
        Button speechColectorButton = activity.findViewById(R.id.collector_speech_button);
        Button speechSpeciesButton = activity.findViewById(R.id.species_speech_button);
        Button speechSpeciesFamilyButton = activity.findViewById(R.id.speciesFamily_speech_button);
        Button speechAuthorButton = activity.findViewById(R.id.author_speech_button);
        Button speechSampleDescriptionButton = activity.findViewById(R.id.sampleDescription_speech_button);
        Button speechAmbientDescriptionButton = activity.findViewById(R.id.ambientDescription_speech_button);
        Button speechNotesButton = activity.findViewById(R.id.notes_speech_button);

        speechColectorButton.setEnabled(false);
        speechSpeciesButton.setEnabled(false);
        speechSpeciesFamilyButton.setEnabled(false);
        speechAuthorButton.setEnabled(false);
        speechSampleDescriptionButton.setEnabled(false);
        speechAmbientDescriptionButton.setEnabled(false);
        speechNotesButton.setEnabled(false);

        speechColectorButton.setBackgroundResource(R.drawable.bg_rect_gray_mic);
        speechSpeciesButton.setBackgroundResource(R.drawable.bg_rect_gray_mic);
        speechSpeciesFamilyButton.setBackgroundResource(R.drawable.bg_rect_gray_mic);
        speechAuthorButton.setBackgroundResource(R.drawable.bg_rect_gray_mic);
        speechSampleDescriptionButton.setBackgroundResource(R.drawable.bg_rect_gray_mic);
        speechAmbientDescriptionButton.setBackgroundResource(R.drawable.bg_rect_gray_mic);
        speechNotesButton.setBackgroundResource(R.drawable.bg_rect_gray_mic);

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
    }

    private void setSpeechButtons(formActivity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},555);
        }else{
            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity);
            mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

            Button speechColectorButton = activity.findViewById(R.id.collector_speech_button);
            ProgressBar progressBarColectorName = activity.findViewById(R.id.progressBarColectorName);
            hideSpeechProgressBar(progressBarColectorName);
            setSpeechRecognizerToButton(speechColectorButton, fieldCollectorName, progressBarColectorName);

            Button speechSpeciesButton = activity.findViewById(R.id.species_speech_button);
            ProgressBar progressBarSpecies = activity.findViewById(R.id.progressBarSpecies);
            hideSpeechProgressBar(progressBarSpecies);
            setSpeechRecognizerToButton(speechSpeciesButton, fieldSpecies, progressBarSpecies);

            Button speechSpeciesFamilyButton = activity.findViewById(R.id.speciesFamily_speech_button);
            ProgressBar progressBarSpeciesFamily = activity.findViewById(R.id.progressBarSpeciesFamily);
            hideSpeechProgressBar(progressBarSpeciesFamily);
            setSpeechRecognizerToButton(speechSpeciesFamilyButton, fieldSpeciesFamily, progressBarSpeciesFamily);

            Button speechAuthorButton = activity.findViewById(R.id.author_speech_button);
            ProgressBar progressBarAuthor = activity.findViewById(R.id.progressBarAuthor);
            hideSpeechProgressBar(progressBarAuthor);
            setSpeechRecognizerToButton(speechAuthorButton, fieldAuthor, progressBarAuthor);

            Button speechSampleDescriptionButton = activity.findViewById(R.id.sampleDescription_speech_button);
            ProgressBar progressBarSampleDescription = activity.findViewById(R.id.progressBarSampleDescription);
            hideSpeechProgressBar(progressBarSampleDescription);
            setSpeechRecognizerToButton(speechSampleDescriptionButton, fieldSampleDescription, progressBarSampleDescription);

            Button speechAmbientDescriptionButton = activity.findViewById(R.id.ambientDescription_speech_button);
            ProgressBar progressBarAmbientDescription = activity.findViewById(R.id.progressBarAmbientDescription);
            hideSpeechProgressBar(progressBarAmbientDescription);
            setSpeechRecognizerToButton(speechAmbientDescriptionButton, fieldAmbientDescription, progressBarAmbientDescription);

            Button speechNotesButton = activity.findViewById(R.id.notes_speech_button);
            ProgressBar progressBarNotes = activity.findViewById(R.id.progressBarNotes);
            hideSpeechProgressBar(progressBarNotes);
            setSpeechRecognizerToButton(speechNotesButton, fieldNotes, progressBarNotes);
        }
    }

    private void setSpeechRecognizerToButton(Button button, final EditText editText, final ProgressBar progressBar) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                setSpeechRecognizer(editText, progressBar);
                editText.requestFocus();

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        //showProgressBar(progressBar);
                        editText.setHint(null);
                        break;

                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        //editText.setText("");
                        editText.setHint("Listening...");
                        break;
                }
                return false;
            }
        });
    }

    private void showSpeechProgressBar(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideSpeechProgressBar(ProgressBar progressBar) {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void setSpeechRecognizer(final EditText finalEditText, final ProgressBar progressBar) {
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                showSpeechProgressBar(progressBar);
                //finalEditText.setHint("Translating...");
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null) {
                    finalEditText.setHint(null);
                    String oldText = finalEditText.getText().toString();
                    if (!oldText.isEmpty()) {
                        oldText = oldText + " ";
                    }
                    finalEditText.setText(oldText + matches.get(0));
                }

                hideSpeechProgressBar(progressBar);
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

    private void onChangeLatLong() {
        fieldEditTextGPSLatitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s){}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(fieldEditTextGPSLatitude.hasFocus()) {
                    disableGPSUpdates();
                    startMaps();
                }
            }
        });

        fieldEditTextGPSLongitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s){}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(fieldEditTextGPSLongitude.hasFocus()) {
                    disableGPSUpdates();
                    startMaps();
                }
            }
        });
    }

    private int getLastID(formActivity activity) {
        sampleDAO dao = new sampleDAO(activity);
        int lastID = dao.lastID();
        dao.close();

        return lastID;
    }

    private String todayDate() {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(todayDate);
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

        sample.setGPSLatitude(fieldEditTextGPSLatitude.getText().toString());
        sample.setGPSLongitude(fieldEditTextGPSLongitude.getText().toString());
        sample.setGPSAltitude(fieldEditTextGPSAltitude.getText().toString());


        sampleDAO dao = new sampleDAO(activity);
        List<String> imagesListDB = dao.getImagesDB(sample.getId());

        List<String> newestImageList = new ArrayList<>();
        newestImageList.addAll(imagesListDB);
        newestImageList.addAll(imagesList);

        sample.setImagesList(newestImageList);

        //Toast.makeText(activity,""+formActivity.imagesList,Toast.LENGTH_LONG).show();

        return sample;
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

        List<String> images = sample.getImagesList();
        if(images.size() > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(images.get(0));
            Bitmap smallerBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
            imageViewSample.setImageBitmap(smallerBitmap);
            imageViewSample.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        //editText.setBackgroundColor(Color.TRANSPARENT);
        editText.setTextColor(Color.parseColor("#616161"));
    }

    public static void setGPSValues(final formActivity activity) {
        fieldEditTextGPSLatitude.setFocusableInTouchMode(false);
        fieldEditTextGPSLatitude.setFocusable(false);
        fieldEditTextGPSLongitude.setFocusableInTouchMode(false);
        fieldEditTextGPSLongitude.setFocusable(false);

        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        final ProgressBar progressBar = activity.findViewById(R.id.progressBarGPS);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},111);
            gpsButton.setBackgroundResource(R.drawable.ic_play);
            return;
        }else{
            final double[] longitude = {0};
            final double[] latitude = {0};
            final double[] altitude = {0};

            activeGPS = true;
            gpsButton.setBackgroundResource(R.drawable.ic_pause);
            progressBar.setVisibility(View.VISIBLE);

            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    if(location != null) {
                        progressBar.setVisibility(View.INVISIBLE);

                        longitude[0] = location.getLongitude();
                        latitude[0] = location.getLatitude();
                        altitude[0] = location.getAltitude();

                        fieldEditTextGPSLatitude.setText("" + latitude[0], TextView.BufferType.EDITABLE);
                        fieldEditTextGPSLongitude.setText("" + longitude[0], TextView.BufferType.EDITABLE);
                        fieldEditTextGPSAltitude.setText("" + altitude[0], TextView.BufferType.EDITABLE);

                        startMaps();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {
                    if(provider.equals(LocationManager.GPS_PROVIDER)){
                        Toast.makeText(activity, "Please, enable GPS!", Toast.LENGTH_LONG).show();
                        Intent startGPSIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(startGPSIntent);
                    }
                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
            startMaps();
        }
    }

    public static void toggleGPS(final formActivity activity) {
        final ProgressBar progressBar = activity.findViewById(R.id.progressBarGPS);
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activeGPS){
                    disableGPSUpdates();
                    progressBar.setVisibility(View.INVISIBLE);

                    fieldEditTextGPSLatitude.setFocusableInTouchMode(true);
                    fieldEditTextGPSLatitude.setFocusable(true);
                    fieldEditTextGPSLatitude.setTextColor(Color.parseColor("#000000"));
                    fieldEditTextGPSLatitude.setCursorVisible(true);

                    fieldEditTextGPSLongitude.setFocusableInTouchMode(true);
                    fieldEditTextGPSLongitude.setFocusable(true);
                    fieldEditTextGPSLongitude.setTextColor(Color.parseColor("#000000"));
                    fieldEditTextGPSLongitude.setCursorVisible(true);
                }else{
                    setGPSValues(activity);
                    gpsButton.setBackgroundResource(R.drawable.ic_pause);
                    fieldEditTextGPSLatitude.setFocusableInTouchMode(false);
                    fieldEditTextGPSLatitude.setFocusable(false);
                    fieldEditTextGPSLatitude.setTextColor(Color.parseColor("#616161"));
                    fieldEditTextGPSLatitude.setCursorVisible(false);

                    fieldEditTextGPSLongitude.setFocusableInTouchMode(false);
                    fieldEditTextGPSLongitude.setFocusable(false);
                    fieldEditTextGPSLongitude.setTextColor(Color.parseColor("#616161"));
                    fieldEditTextGPSLongitude.setCursorVisible(false);
                }
            }
        });
    }

    private static void disableGPSUpdates() {
        activeGPS = false;
        locationManager.removeUpdates(locationListener);
        gpsButton.setBackgroundResource(R.drawable.ic_play);
    }

    public void disableGPS() {
        if(activeGPS){
            activeGPS = false;
            locationManager.removeUpdates(locationListener);
        }
    }

    public static void deleteImagesFromPhoneMemory(Sample sample) {
        List<String> imagesListToDelete = sample.getImagesList();
        //Toast.makeText(formActivity.this, "Deletando "+imagesListToDelete.size()+"!", Toast.LENGTH_LONG).show();
        for(int i = 0; i < imagesListToDelete.size(); i++){
            File file = new File(imagesListToDelete.get(i));
            file.delete();
        }
    }

    public static void startMaps() {
        fixScrollForMaps();

        FragmentManager fragManager = activity.getSupportFragmentManager();
        FragmentTransaction tx = fragManager.beginTransaction();
        tx.replace(R.id.mapFrame, new mapFragment());
        tx.commit();
    }

    private static void fixScrollForMaps() {
        final ScrollView mainScrollView = activity.findViewById(R.id.main_scrollview);
        ImageView transparentImageView = activity.findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
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
}
