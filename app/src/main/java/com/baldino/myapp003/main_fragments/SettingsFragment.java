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
                    //TODO: translate text
                    Toast.makeText(getContext(), "Settings saved!", Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    private void askRefactorWeeks()
    {
        //TODO: translate text
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
                //TODO: translate text
                dialog.dismiss();
                beginRefactorWeeks();
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

    private void beginRefactorWeeks()
    {
        //TODO: translate text
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Checking for potential data corruption..."); // Setting Message
        progressDialog.setTitle("Begin Refactor"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);
        progressDialog.show(); // Display Progress Dialog

        boolean error = false;
        List<String> error_dates = new ArrayList<>();

        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();
        if(sWeekManager.saved_weeks.size() > 0)
        {
            WeekManagerSingleton.WeekData first_data, second_data;
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
        }
        else
        {
            //TODO: translate text
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setTitle("Potential Data Corruption");
            String message = "It looks like there are some pairs of consecutive weeks where the " +
                    "first week has a Day Format (Daily Meals) different to the second week's. " +
                    "This makes it unable for the app to refactor those two weeks and will end up " +
                    "in losing data relative to half of one of those two weeks. Do you want to " +
                    "change the first day of the week and refactor anyway?\n\n" +
                    "List of problematic pairs (any week not included in this list won't go corrupt "+
                    "and will be saved just fine):\n\n";
            for(String date : error_dates) message += date + "\n";
            builder.setMessage(message);
            builder.setPositiveButton("YES\n(Refactor)", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    refactorWeeks();
                }
            });

            builder.setNegativeButton("NO\n(Do Nothing)", new DialogInterface.OnClickListener()
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
        //TODO: translate text
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Do not close this app..."); // Setting Message
        progressDialog.setTitle("Refactoring Data"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);
        progressDialog.show(); // Display Progress Dialog

        int old_fdow = Util.FIRST_DAY_OF_WEEK;
        Util.FIRST_DAY_OF_WEEK = first_day_of_week.getSelectedItemPosition() + 1;

        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();
        sWeekManager.refactor(old_fdow, Util.FIRST_DAY_OF_WEEK);

        progressDialog.dismiss();

        Util.saveSettings();
        //TODO: translate text
        Toast.makeText(getContext(), "Settings saved!", Toast.LENGTH_LONG).show();
    }
}
