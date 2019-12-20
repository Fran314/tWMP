package com.baldino.myapp003.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.baldino.myapp003.R;

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
}
