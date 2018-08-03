package com.dudukling.collector;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.model.Sample;

import java.util.List;

class aViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    final TextView idNum;
    final TextView date;
    final TextView species;
    private List<Sample> samples;
    private Context context;

    public aViewHolder(View view, List<Sample> samples, Context context) {
        super(view);
        this.samples = samples;
        this.context = context;
        idNum = view.findViewById(R.id.textViewId);
        species = view.findViewById(R.id.textViewSpecies);
        date = view.findViewById(R.id.textViewDate);

        view.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        //menu.setHeaderTitle("Selecione a ação:");
        MenuItem menuDelete = menu.add("Deletar");
        menuDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//                Sample sample  = samples.get(info.position);
//
//                sampleDAO dao = new sampleDAO(context);
//                dao.delete(sample);
//                dao.close();

//                context.loadRecycler();

                Toast.makeText(context, "Deleta ai: "+menuInfo, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

}
