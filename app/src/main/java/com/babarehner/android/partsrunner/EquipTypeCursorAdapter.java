package com.babarehner.android.partsrunner;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.babarehner.android.partsrunner.data.PartsRunnerContract;

public class EquipTypeCursorAdapter extends CursorAdapter {

    public EquipTypeCursorAdapter(Context context, Cursor c) { super(context, c);}

    /**
     * creates a new blank list item with no data
     * @param context   app context
     * @param c         cursor
     * @param parent    parent to which view is attached to
     * @return          the newly created list item view
     */
    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.equipment_type_list_item, parent, false);
    }


    /**
     * Binds data to the empty list item
     * @param v View
     * @param context Context
     * @param c Cursor
     */
    @Override
    public void bindView(View v, Context context, Cursor c) {

        TextView equip_typeTextView = (TextView) v.findViewById(R.id.list_item_equip_type);
        int equip_typeColIndex = c.getColumnIndex(PartsRunnerContract.EquipmentType.C_EQUIPMENT_TYPE);
        String equip_type = c.getString(equip_typeColIndex);
        equip_typeTextView.setText(equip_type);

    }
}
