package com.example.tasknew;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class ClaseDialogoFecha extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;

    public static ClaseDialogoFecha newInstance(DatePickerDialog.OnDateSetListener listener) {
        ClaseDialogoFecha dialogo = new ClaseDialogoFecha();
        dialogo.setListener(listener);
        return dialogo;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }

}