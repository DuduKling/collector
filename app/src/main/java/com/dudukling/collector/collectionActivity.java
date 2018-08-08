package com.dudukling.collector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;

import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.model.Sample;

import java.util.List;


public class collectionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        recyclerView = findViewById(R.id.lista_collection);

        startNewRegisterButton();
        registerForContextMenu(recyclerView);
    }

    private void startNewRegisterButton() {
        Button buttonNewSample = findViewById(R.id.buttonNewSample);
        buttonNewSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //Toast.makeText(collectionActivity.this, "Clicado!", Toast.LENGTH_SHORT).show();

            Intent goToFormActivity = new Intent(collectionActivity.this, formActivity.class);
//            goToFormActivity.putExtra("sample", sample);
            startActivity(goToFormActivity);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecycler();
    }

    public void loadRecycler() {
        sampleDAO dao = new sampleDAO(this);
        List<Sample> samples = dao.getSamples();
        dao.close();

        recyclerView.setAdapter(new recyclerAdapter(samples, this));

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }
}
