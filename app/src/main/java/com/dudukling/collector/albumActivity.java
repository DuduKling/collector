package com.dudukling.collector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.model.Sample;
import com.dudukling.collector.util.recyclerAlbumAdapter;


public class albumActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Sample sample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        recyclerView = findViewById(R.id.album_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        sample = (Sample) intent.getSerializableExtra("sample");
        setTitle("Images #" + sample.getId());


        //int sampleID = sample.getId();
        //sampleDAO dao = new sampleDAO(this);
        //String qtdImgsDB = dao.countImages(sampleID);
        //Toast.makeText(this, "QTD IMAGENS: " + sample.getImagesList().size() + "|" + qtdImgsDB, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecycler();
    }

    private void loadRecycler() {
        recyclerView.setAdapter(new recyclerAlbumAdapter(sample, this));

        RecyclerView.LayoutManager layout = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layout);

//        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
