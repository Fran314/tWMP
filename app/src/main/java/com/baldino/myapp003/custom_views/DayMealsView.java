package com.baldino.myapp003.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.baldino.myapp003.R;
import com.baldino.myapp003.Util;

public class DayMealsView extends LinearLayout {

    public TextView header;
    public TableLayout meals_container;

    public DayMealsView(Context context)
    {
        super(context);
        initializeViews(context);
    }

    public DayMealsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initializeViews(context);
    }

    private void initializeViews(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_day_meals, this);

        header = this.findViewById(R.id.text_day_name);
        meals_container = this.findViewById(R.id.meals_container);
    }

    public void addFirstRow(String name, String meal, int color)
    {
        TableRow first_row = new TableRow(getContext());
        first_row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TextView meal_name = new TextView(getContext());
        meal_name.setText(name);
        meal_name.setTextSize(Util.intToDp(6, getContext()));
        meal_name.setTypeface(Typeface.DEFAULT_BOLD);
        TableRow.LayoutParams name_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        name_params.gravity = Gravity.CENTER_VERTICAL;
        meal_name.setLayoutParams(name_params);

        TextView first_course = new TextView(getContext());
        first_course.setText(meal);
        first_course.setTextColor(color);
        TableRow.LayoutParams first_course_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        first_course_params.gravity = Gravity.CENTER_VERTICAL;
        first_course_params.leftMargin = Util.intToDp(4, getContext());
        first_course_params.rightMargin = Util.intToDp(4, getContext());
        first_course.setLayoutParams(first_course_params);

        first_row.addView(meal_name);
        first_row.addView(first_course);
        meals_container.addView(first_row);
    }

    public void addRow(String meal, int color)
    {
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        View filler = new View(getContext());
        filler.setLayoutParams(new TableRow.LayoutParams(0, 0));

        TextView course = new TextView(getContext());
        course.setText(meal);
        course.setTextColor(color);
        TableRow.LayoutParams course_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        course_params.gravity = Gravity.CENTER_VERTICAL;
        course_params.leftMargin = Util.intToDp(4, getContext());
        course_params.rightMargin = Util.intToDp(4, getContext());
        course.setLayoutParams(course_params);

        row.addView(filler);
        row.addView(course);
        meals_container.addView(row);
    }

    public void emptyUI()
    {
        meals_container.removeAllViews();
    }
}
