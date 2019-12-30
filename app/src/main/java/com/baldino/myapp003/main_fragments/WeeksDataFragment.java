package com.baldino.myapp003.main_fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.baldino.myapp003.R;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.singletons.WeekManagerSingleton;

import java.io.File;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

public class WeeksDataFragment extends Fragment
{
    private WeekManagerSingleton sWeekManager;

    private TableLayout data_container;
    private List<ImageButton> delete_buttons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_weeks_data, container, false);

        sWeekManager = WeekManagerSingleton.getInstance();

        data_container = root.findViewById(R.id.container_weeks_data);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI()
    {
        delete_buttons = new ArrayList<>();
        data_container.removeAllViews();

        for(int i = 0; i < sWeekManager.saved_weeks.size(); i++)
        {
            addRow(i);
            setButtons(i);
        }
    }

    private void addRow(int pos)
    {
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        ImageButton button_delete = new ImageButton(getContext());
        button_delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_button_close_20dp));
        button_delete.setBackgroundColor(Color.TRANSPARENT);
        button_delete.setPadding(Util.intToDp(8), Util.intToDp(8), Util.intToDp(8), Util.intToDp(8));
        TableRow.LayoutParams button_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        button_params.gravity = Gravity.CENTER_VERTICAL;
        button_delete.setLayoutParams(button_params);
        delete_buttons.add(button_delete);

        TextView week_name = new TextView(getContext());
        TableRow.LayoutParams text_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        text_params.gravity = Gravity.CENTER_VERTICAL;
        week_name.setLayoutParams(text_params);

        int year = Util.stringToInt(sWeekManager.saved_weeks.get(pos).substring(0, 4));
        int month = Util.stringToInt(sWeekManager.saved_weeks.get(pos).substring(5, 7));
        int day_of_month = Util.stringToInt(sWeekManager.saved_weeks.get(pos).substring(8, 10));


        LocalDate date = LocalDate.of(year, month, day_of_month);

        String name = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) + " - ";

        date = date.plusDays(6);

        name += date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));

        week_name.setText(name);
        week_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        week_name.setTextColor(getContext().getResources().getColor(R.color.colorBlack));

        row.addView(button_delete);
        row.addView(week_name);

        View separator = new View(getContext());
        TableRow.LayoutParams separator_params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, Util.intToDp(1));
        separator_params.setMargins(Util.intToDp(4), Util.intToDp(4), Util.intToDp(4), Util.intToDp(4));
        //separator_params.span = 1;
        separator.setLayoutParams(separator_params);
        separator.setBackgroundColor(getResources().getColor(R.color.colorLightGray));

        data_container.addView(row, 2*pos);
        data_container.addView(separator, 2*pos + 1);
    }

    private void setButtons(final int pos)
    {
        delete_buttons.get(pos).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                removeRow(pos);
            }
        });
    }

    private void removeRow(int pos)
    {
        String file_name = sWeekManager.saved_weeks.get(pos);

        File folder = new File(getContext().getFilesDir(), Util.WEEKS_DATA_FOLDER);
        folder.mkdirs();
        File old_file = new File(folder, file_name);
        if(old_file.exists()) old_file.delete();

        sWeekManager.saved_weeks.remove(pos);
        sWeekManager.saveWeeks();
        data_container.removeViewAt(2*pos + 1);
        data_container.removeViewAt(2*pos);
        delete_buttons.remove(pos);

        for(int i = 0; i < delete_buttons.size(); i++)
        {
            setButtons(i);
        }
    }
}
