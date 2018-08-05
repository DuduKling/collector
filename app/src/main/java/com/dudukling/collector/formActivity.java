package com.dudukling.collector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.model.Sample;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class formActivity extends AppCompatActivity {

    private formHelper helperForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Intent intent = getIntent();
//        int lastID = (int) intent.getSerializableExtra("lastID");


        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String todayStringNormal = formatter.format(todayDate);

        TextView tv1 = findViewById(R.id.TextViewDateForm);
        tv1.setText("Date: " + todayStringNormal);


        sampleDAO dao = new sampleDAO(this);
        int lastID = dao.nextID();
        dao.close();
        TextView tv2 = findViewById(R.id.TextViewIDForm);
        tv2.setText("ID: #" + (lastID + 1));


        helperForm = new formHelper(this, todayStringNormal);
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
                Sample sample = helperForm.getSample();

                sampleDAO dao = new sampleDAO(formActivity.this);
                dao.insert(sample);
                dao.close();

//                Toast.makeText(formActivity.this, "Salvando: " + sample.getSpecies() + " !", Toast.LENGTH_LONG).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}