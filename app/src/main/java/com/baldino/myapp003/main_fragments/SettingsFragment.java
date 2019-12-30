package com.baldino.myapp003.main_fragments;


import android.app.AlertDialog;
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
                    //TODO: change this text to something that makes more sense and is based on
                    // the device language
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false);
                    builder.setTitle("Override First Day Of Week");
                    builder.setMessage("Changing the first day of the week will make unreadable " +
                            "all the previously saved weeks.\n" +
                            "Do you want to refactor all your previously saved weeks according " +
                            "to the new week format?\n\n" +
                            "(Refactoring might take a while. Do NOT close this app as long as " +
                            "this alert dialog is open!)");
                    builder.setPositiveButton("YES\n(Do Refactor)", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Util.FIRST_DAY_OF_WEEK = first_day_of_week.getSelectedItemPosition() + 1;
                            Util.saveSettings();
                            //TODO change text so that it changes based on device language
                            Toast.makeText(getContext(), "Settings saved!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO\n(Don't Refactor)", new DialogInterface.OnClickListener()
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
                else
                {
                    Util.saveSettings();
                    //TODO change text so that it changes based on device language
                    Toast.makeText(getContext(), "Settings saved!", Toast.LENGTH_LONG).show();

                    //  TODO
                    //  THESE THREE LINES OF CODE ARE HERE ONLY FOR TESTING
                    //  They should be moved in builder.setPositiveButton later
                    int old_fdow = Util.FIRST_DAY_OF_WEEK;
                    WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();
                    sWeekManager.refactor(old_fdow);
                }
            }
        });

        return root;
    }

}
