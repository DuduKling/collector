package com.dudukling.collector;

import android.widget.EditText;
import android.widget.TextView;

import com.dudukling.collector.model.Sample;

public class formHelper {
    private final String todayStringDB;

    private TextView fieldIDForm;
    private TextView fieldDateForm;

    private EditText fieldSpecies;
    private EditText fieldCollectorName;
    private EditText fieldSpeciesFamily;
    private EditText fieldAuthor;
    private EditText fieldSampleDescription;
    private EditText fieldAmbientDescription;
    private EditText fieldNotes;

    public formHelper(formActivity activity, String todayStringDB) {
        this.todayStringDB = todayStringDB;

        fieldIDForm = activity.findViewById(R.id.TextViewIDForm);
        fieldDateForm = activity.findViewById(R.id.TextViewDateForm);

        fieldCollectorName = activity.findViewById(R.id.editTextCollectorName);
        fieldSpecies = activity.findViewById(R.id.editTextSpecies);
        fieldSpeciesFamily = activity.findViewById(R.id.editTextSpeciesFamily);
        fieldAuthor = activity.findViewById(R.id.editTextAuthor);
        fieldSampleDescription = activity.findViewById(R.id.editTextSampleDescription);
        fieldAmbientDescription = activity.findViewById(R.id.editTextAmbientDescription);
        fieldNotes = activity.findViewById(R.id.editTextNotes);
    }

    public Sample getSample() {
        Sample sample = new Sample();

        sample.setDate(todayStringDB);

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
}
