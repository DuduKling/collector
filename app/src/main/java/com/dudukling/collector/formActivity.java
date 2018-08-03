package com.dudukling.collector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.model.Sample;

public class formActivity extends AppCompatActivity {

    private formHelper helperForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        helperForm = new formHelper(this);


//        Button buttonSaveForm = findViewById(R.id.buttonSaveForm);
//        buttonSaveForm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_form, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menu_save_button:
                // Toast.makeText(formActivity.this, "Salvando..", Toast.LENGTH_SHORT).show();
                Sample sample = helperForm.getSample();

                sampleDAO dao = new sampleDAO(formActivity.this);
                dao.insert(sample);
                dao.close();

                // Toast.makeText(formActivity.this, "Salvando: " + sample.getIdNum() + " - " + sample.getSpecies() + " - " + sample.getDate() + " !", Toast.LENGTH_LONG).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
