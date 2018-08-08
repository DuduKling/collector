package com.dudukling.collector;

import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;
import android.widget.TextView;

import com.dudukling.collector.model.Sample;

public class formHelper {
    private final String todayStringDB;
    private final int sampleID;

    private TextView fieldIDForm;
    private TextView fieldDateForm;

    private EditText fieldSpecies;
    private EditText fieldCollectorName;
    private EditText fieldSpeciesFamily;
    private EditText fieldAuthor;
    private EditText fieldSampleDescription;
    private EditText fieldAmbientDescription;
    private EditText fieldNotes;

    public formHelper(formActivity activity, String todayStringDB, int sampleID, Boolean readOnly) {
        this.todayStringDB = todayStringDB;
        this.sampleID = sampleID;

        fieldIDForm = activity.findViewById(R.id.TextViewIDForm);
        fieldDateForm = activity.findViewById(R.id.TextViewDateForm);

        //fieldCollectorName = activity.findViewById(R.id.editTextCollectorName);
        TextInputLayout textInputCollectorName = activity.findViewById(R.id.editTextCollectorName);
        fieldCollectorName = textInputCollectorName.getEditText();
        if(readOnly){disableEditText(fieldCollectorName);}

        //fieldSpecies = activity.findViewById(R.id.editTextSpecies);
        TextInputLayout textInputSpecies = activity.findViewById(R.id.editTextSpecies);
        fieldSpecies = textInputSpecies.getEditText();
        if(readOnly){disableEditText(fieldSpecies);}

        //fieldSpeciesFamily = activity.findViewById(R.id.editTextSpeciesFamily);
        TextInputLayout textInputSpeciesFamily = activity.findViewById(R.id.editTextSpeciesFamily);
        fieldSpeciesFamily = textInputSpeciesFamily.getEditText();
        if(readOnly){disableEditText(fieldSpeciesFamily);}

        //fieldAuthor = activity.findViewById(R.id.editTextAuthor);
        TextInputLayout textInputAuthor = activity.findViewById(R.id.editTextAuthor);
        fieldAuthor = textInputAuthor.getEditText();
        if(readOnly){disableEditText(fieldAuthor);}

        //fieldSampleDescription = activity.findViewById(R.id.editTextSampleDescription);
        TextInputLayout textInputSampleDescription = activity.findViewById(R.id.editTextSampleDescription);
        fieldSampleDescription = textInputSampleDescription.getEditText();
        if(readOnly){disableEditText(fieldSampleDescription);}

        //fieldAmbientDescription = activity.findViewById(R.id.editTextAmbientDescription);
        TextInputLayout textInputAmbientDescription = activity.findViewById(R.id.editTextAmbientDescription);
        fieldAmbientDescription = textInputAmbientDescription.getEditText();
        if(readOnly){disableEditText(fieldAmbientDescription);}

        //fieldNotes = activity.findViewById(R.id.editTextNotes);
        TextInputLayout textInputNotes = activity.findViewById(R.id.editTextNotes);
        fieldNotes = textInputNotes.getEditText();
        if(readOnly){disableEditText(fieldNotes);}
    }

    public Sample getSample() {
        Sample sample = new Sample();

        sample.setDate(todayStringDB);
        sample.setId(sampleID);

        sample.setSpecies(fieldSpecies.getText().toString());
        sample.setCollectorName(fieldCollectorName.getText().toString());
        sample.setSpeciesFamily(fieldSpeciesFamily.getText().toString());
        sample.setAuthor(fieldAuthor.getText().toString());
        sample.setSampleDescription(fieldSampleDescription.getText().toString());
        sample.setAmbientDescription(fieldAmbientDescription.getText().toString());
        sample.setNotes(fieldNotes.getText().toString());


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
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
//        editText.setBackgroundColor(Color.TRANSPARENT);
        editText.setTextColor(Color.parseColor("#616161"));
    }

}
