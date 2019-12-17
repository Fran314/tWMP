package com.baldino.myapp003;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DayMeals extends LinearLayout {

    public TextView header, lunch, dinner, side_dish;
    String mDayName;

    public DayMeals(Context context)
    {
        super(context);
        mDayName = getResources().getString(R.string.standard_day_name);
        initializeViews(context);
    }

    public DayMeals(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initializeViews(context);

        applyCustomAttrs(context, attrs);
    }

    public DayMeals(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initializeViews(context);

        applyCustomAttrs(context, attrs);
    }

    private void initializeViews(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_day_meals, this);
    }

    private void applyCustomAttrs(Context context, AttributeSet attrs)
    {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DayMeals, 0, 0);
        try {
            mDayName = a.getString(R.styleable.DayMeals_day);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        // Sets the images for the previous and next buttons. Uses
        // built-in images so you don't need to add images, but in
        // a real application your images should be in the
        // application package so they are always available.

        header = (TextView) this.findViewById(R.id.text_day_name);
        lunch = (TextView) this.findViewById(R.id.text_lunch_meal);
        dinner = (TextView) this.findViewById(R.id.text_dinner_meal);
        side_dish = (TextView) this.findViewById(R.id.text_dinner_side);

        header.setText(mDayName);
    }
}
