package com.dudukling.collector;

import android.view.View;
import android.widget.EditText;

import com.dudukling.collector.model.Sample;

public class formHelper {

    private final EditText fieldID;
    private final EditText fieldSpecies;
    private final EditText fieldDate;

    public formHelper(formActivity activity) {
        fieldID = activity.findViewById(R.id.editTextID);
        fieldSpecies = activity.findViewById(R.id.editTextSpecies);
        fieldDate = activity.findViewById(R.id.editTextDate);
    }

    public Sample getSample() {
        Sample sample = new Sample();

        sample.setIdNum(fieldID.getText().toString());
        sample.setSpecies(fieldSpecies.getText().toString());
        sample.setDate(fieldDate.getText().toString());

        return sample;
    }
}
