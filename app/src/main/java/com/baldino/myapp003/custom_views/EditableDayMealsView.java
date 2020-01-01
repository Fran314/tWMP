package com.baldino.myapp003.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.baldino.myapp003.R;

public class EditableDayMealsView extends LinearLayout {

    public TextView header;
    public TableLayout editable_meals_container;

    public EditableDayMealsView(Context context)
    {
        super(context);
        initializeViews(context);
    }

    public EditableDayMealsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initializeViews(context);
    }

    private void initializeViews(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_editable_day_meals, this);

        header = this.findViewById(R.id.text_day_name);
        editable_meals_container = this.findViewById(R.id.editable_meals_container);
    }
}
