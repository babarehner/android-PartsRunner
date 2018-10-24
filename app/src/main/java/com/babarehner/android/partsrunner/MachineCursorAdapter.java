package com.babarehner.android.partsrunner;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.babarehner.android.partsrunner.data.PartsRunnerContract;

public class MachineCursorAdapter extends CursorAdapter {

    public MachineCursorAdapter(Context context, Cursor c) {
        super(context, c /* flags */); }


    @Override
    public View newView(Context context, Cursor c, ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.machine_list_item, parent,
                false);
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {
        TextView yearTextView = (TextView) v.findViewById(R.id.list_item_year);
        TextView manufacturerTextView = (TextView) v.findViewById(R.id.list_item_manufacturer);
        TextView modelTextView = (TextView) v.findViewById(R.id.list_item_model);

        int yearColIndex = c.getColumnIndex(PartsRunnerContract.MachineEntry.C_MODEL_YEAR);
        int manufacturerColIndex = c.getColumnIndex(PartsRunnerContract.MachineEntry.C_MANUFACTURER);
        int modelColIndex = c.getColumnIndex(PartsRunnerContract.MachineEntry.C_MODEL);

        String modelYear = c.getString(yearColIndex);
        String manufacturer = c.getString(manufacturerColIndex);
        String model = c.getString(manufacturerColIndex);

        yearTextView.setText(modelYear);
        manufacturerTextView.setText(manufacturer);
        modelTextView.setText(model);
    }
}