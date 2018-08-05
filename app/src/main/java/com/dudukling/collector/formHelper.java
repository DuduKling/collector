package com.dudukling.collector;

import android.widget.EditText;

import com.dudukling.collector.model.Sample;

public class formHelper {
    private final EditText fieldSpecies;
    private final String todayStringDB;

    public formHelper(formActivity activity, String todayStringDB) {
        this.todayStringDB = todayStringDB;
        fieldSpecies = activity.findViewById(R.id.editTextSpecies);
    }

    public Sample getSample() {
        Sample sample = new Sample();

        sample.setSpecies(fieldSpecies.getText().toString());
        sample.setDate(todayStringDB);

        return sample;
    }
}
