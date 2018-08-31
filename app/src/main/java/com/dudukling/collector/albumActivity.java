package com.dudukling.collector;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.dudukling.collector.model.Sample;
import com.dudukling.collector.util.recyclerAlbumAdapter;

import java.util.Objects;


public class albumActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Sample sample;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        recyclerView = findViewById(R.id.album_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }


        Intent intent = getIntent();
        sample = (Sample) intent.getSerializableExtra("sample");
        type = (String) intent.getSerializableExtra("type");

        if(type==null){
            setTitle("Images #" + sample.getId());
        }else{
            setTitle("Edit #" + sample.getId());
        }

        loadRecycler();

        //int sampleID = sample.getId();
        //sampleDAO dao = new sampleDAO(this);
        //String qtdImgsDB = dao.countImages(sampleID);
        //Toast.makeText(this, "QTD IMAGENS: " + sample.getImagesList().size() + "|" + qtdImgsDB, Toast.LENGTH_LONG).show();
    }

    private void loadRecycler() {
        recyclerView.setAdapter(new recyclerAlbumAdapter(sample, type, this));

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
