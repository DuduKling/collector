package com.dudukling.collector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class collectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        Button buttonNewSample = findViewById(R.id.buttonNewSample);
        buttonNewSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(collectionActivity.this, "Clicado!", Toast.LENGTH_SHORT).show();

                Intent goToFormActivity = new Intent(collectionActivity.this, formActivity.class);
                startActivity(goToFormActivity);
            }
        });
    }
}
