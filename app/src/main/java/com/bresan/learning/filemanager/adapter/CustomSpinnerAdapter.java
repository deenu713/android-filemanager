package com.bresan.learning.filemanager.adapter;

/**
 * Created by rodrigobresan on 4/27/16.
 */
import java.util.ArrayList;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bresan.learning.filemanager.R;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private ArrayList<String> mItems;
    private LayoutInflater inflater;

    public CustomSpinnerAdapter(Context context, ArrayList<String> objects) {
        super(context, R.layout.spinner_row, objects);

        mItems = objects;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    public View getCustomView(int position, ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_row, parent, false);
        TextView textPath = (TextView) row.findViewById(R.id.text_path);
        textPath.setText(mItems.get(position));

        return row;
    }
}