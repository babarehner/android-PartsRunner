package com.babarehner.android.partsrunner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        /* return new DatePickerDialog(getActivity(), this, year, month, day);
          Set up DatePicker to only show year
          DatePickerDialog dpd = new DatePickerDialog(getActivity(),AlertDialog.THEME_HOLO_DARK,this,year, month, day){
          DatePickerDialog dpd = new DatePickerDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT,this,year, month, day){
          DatePickerDialog dpd = new DatePickerDialog(getActivity(),AlertDialog.THEME_TRADITIONAL,this,year, month, day){
         */
        DatePickerDialog dpd = new DatePickerDialog(getActivity(),AlertDialog.THEME_TRADITIONAL, this,year, month, day){
            @Override
            protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                int day = getContext().getResources().getIdentifier("android:id/day", null, null);
                if(day != 0){
                    View dayPicker = findViewById(day);
                    if(dayPicker != null){
                        //Set Day view visibility Off/Gone
                        dayPicker.setVisibility(View.GONE);
                    }
                }
                int month = getContext().getResources().getIdentifier("android:id/month", null, null);
                if(day != 0){
                    View monthPicker = findViewById(month);
                    if(monthPicker != null){
                        //Set Day view visibility Off/Gone
                        monthPicker.setVisibility(View.GONE);
                    }
                }
            }
        };

        return dpd;

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day)
    {

        EditText et = getActivity().findViewById(R.id.et_model_year);
        // Calendar cal = Calendar.getInstance(); cal.setTimeInMillis(0);
        // cal.set(year, month, day,  0, 0, 0);
        // Date chosenDate = cal.getTime();
        //DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        //String y = String.valueOf(year);
        //String formattedDate = df.format(chosenDate);  et.setText(formattedDate);
        et.setText(String.valueOf(year));
    }
}
