package com.dudukling.collector.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.dudukling.collector.R;
import com.dudukling.collector.formActivity;

import java.util.ArrayList;
import java.util.Locale;

public class speechController {
    public static final int MIC_REQUEST_CODE = 555;
    private final formActivity activity;

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;


    public speechController(formActivity activity) {
        this.activity = activity;
    }

    public void setSpeechButtons(EditText fieldCollectorName, EditText fieldSpecies, EditText fieldSpeciesFamily, EditText fieldAuthor, EditText fieldSampleDescription, EditText fieldAmbientDescription, EditText fieldNotes) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, MIC_REQUEST_CODE);
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

    @SuppressLint("ClickableViewAccessibility")
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

            @SuppressLint("SetTextI18n")
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
}
