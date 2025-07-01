package com.example.avisaaqui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinnerItemAdapter extends ArrayAdapter<SpinnerItem> {
    private Context context;
    private List<SpinnerItem> items;

    public SpinnerItemAdapter(Context context, List<SpinnerItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        SpinnerItem item = items.get(position);
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(item.getDescription());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        SpinnerItem item = items.get(position);
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(item.getDescription());

        return convertView;
    }
}