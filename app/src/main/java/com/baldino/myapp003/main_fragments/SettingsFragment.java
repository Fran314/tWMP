package com.baldino.myapp003.main_fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.baldino.myapp003.R;
import com.baldino.myapp003.Util;

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
                //TODO: change this text to something that makes more sense and is based on
                // the device language
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Override Settings");
                builder.setMessage("Are you sure you want to override current settings?\n" +
                        "If you changed the first day of the week, this will corrupt the data " +
                        "about what you ate or what you planned to eat in the already saved weeks.");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Util.CURRENCY = currency.getText().toString();
                        Util.FIRST_DAY_OF_WEEK = first_day_of_week.getSelectedItemPosition() + 1;
                        Util.saveSettings();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener()
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
        });

        return root;
    }

}
