package com.example.agoracard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<String> {

    private int vg;
    private ArrayList<String[]> items_list;
    private Context context;

    public ListAdapter(Context context, int vg, int id, ArrayList<String[]> items_list){
        super(context, vg, id, new String[items_list.size()]);
        this.context = context;
        this.items_list = items_list;
        this.vg = vg;
    }

    //Hold views of the ListView to improve its scrolling performance
    static class ViewHolder{
        public TextView rowTraining;
        public TextView rowDate;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View rowView = convertView;
        if(rowView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(vg, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.rowTraining = (TextView)rowView.findViewById(R.id.rowtraining);
            holder.rowDate = (TextView)rowView.findViewById(R.id.rowdate);
            rowView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder)rowView.getTag();
        holder.rowTraining.setText(items_list.get(position)[0]);
        holder.rowDate.setText(items_list.get(position)[1]);
        return rowView;
    }
}
