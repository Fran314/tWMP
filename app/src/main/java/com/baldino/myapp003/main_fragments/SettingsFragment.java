package com.baldino.myapp003.main_fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.baldino.myapp003.R;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.data_classes.WeekData;
import com.baldino.myapp003.singletons.WeekManagerSingleton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment
{
    public EditText currency;
    public Spinner first_day_of_week;

    public ImageButton save_settings_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        currency = root.findViewById(R.id.edittext_currency);
        first_day_of_week = root.findViewById(R.id.spinner_first_day_of_week);
        save_settings_button = root.findViewById(R.id.button_save_settings);

        currency.setText(Util.CURRENCY);
        first_day_of_week.setSelection(Util.FIRST_DAY_OF_WEEK - 1);

        save_settings_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Util.CURRENCY = currency.getText().toString();
                if(Util.FIRST_DAY_OF_WEEK != first_day_of_week.getSelectedItemPosition() + 1)
                {
                    askRefactorWeeks();
                }
                else
                {
                    Util.saveSettings();
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_settings_saved), Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    private void askRefactorWeeks()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle(getContext().getResources().getString(R.string.dialog_title_ask_refactor));
        builder.setMessage(getContext().getResources().getString(R.string.dialog_text_ask_refactor));
        builder.setPositiveButton(getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                beginRefactorWeeks();
            }
        });

        builder.setNegativeButton(getContext().getResources().getString(R.string.no), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void beginRefactorWeeks()
    {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getContext().getResources().getString(R.string.dialog_title_begin_refactor));
        progressDialog.setMessage(getContext().getResources().getString(R.string.dialog_text_begin_refactor));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        boolean error = false;
        List<String> error_dates = new ArrayList<>();

        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();
        if(sWeekManager.saved_weeks.size() > 0)
        {
            WeekData first_data, second_data;
            first_data = sWeekManager.loadData(sWeekManager.saved_weeks.get(0));
            for(int i = 1; i < sWeekManager.saved_weeks.size(); i++)
            {
                second_data = sWeekManager.loadData(sWeekManager.saved_weeks.get(i));

                if(!second_data.sameFormatAs(first_data))
                {
                    error = true;
                    error_dates.add(Util.dateToString(Util.fileNameToDate(sWeekManager.saved_weeks.get(i-1)), false) +
                            " - " +
                            Util.dateToString(Util.fileNameToDate(sWeekManager.saved_weeks.get(i)), false));
                }

                first_data = second_data;
            }
        }

        progressDialog.dismiss();

        if(!error)
        {
            refactorWeeks();
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_settings_saved_lossless), Toast.LENGTH_LONG).show();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setTitle(getContext().getResources().getString(R.string.dialog_title_data_corruption));
            String message = getContext().getResources().getString(R.string.dialog_text_data_corruption);
            for(String date : error_dates) message += date + "\n";
            builder.setMessage(message);
            builder.setPositiveButton(getContext().getResources().getString(R.string.dialog_button_data_corruption_yes), new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    refactorWeeks();
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_settings_saved), Toast.LENGTH_LONG).show();
                }
            });

            builder.setNegativeButton(getContext().getResources().getString(R.string.dialog_button_data_corruption_no), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void refactorWeeks()
    {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getContext().getResources().getString(R.string.dialog_title_refactoring));
        progressDialog.setMessage(getContext().getResources().getString(R.string.dialog_text_refactoring));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        int old_fdow = Util.FIRST_DAY_OF_WEEK;
        Util.FIRST_DAY_OF_WEEK = first_day_of_week.getSelectedItemPosition() + 1;

        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();
        sWeekManager.refactor(old_fdow, Util.FIRST_DAY_OF_WEEK);

        progressDialog.dismiss();

        Util.saveSettings();
    }
}
