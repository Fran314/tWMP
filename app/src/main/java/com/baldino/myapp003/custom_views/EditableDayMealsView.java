package com.baldino.myapp003.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.baldino.myapp003.R;
import com.baldino.myapp003.singletons.RecipeManagerSingleton;

public class EditableDayMealsView extends LinearLayout {

    public Spinner lunch_spinner, dinner_spinner, dinner_side_spinner;
    public TextView header;

    public EditableDayMealsView(Context context)
    {
        super(context);
        initializeViews(context);
    }

    public EditableDayMealsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initializeViews(context);

        //applyCustomAttrs(context, attrs);
    }

    public EditableDayMealsView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initializeViews(context);

        //applyCustomAttrs(context, attrs);
    }

    private void initializeViews(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_editable_day_meals, this);
    }

    /*
    private void applyCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DayMeals, 0, 0);
        try {
            mDayName = a.getString(R.styleable.DayMeals_day);
        } finally {
            a.recycle();
        }
    }
     */

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        header = this.findViewById(R.id.text_day_name);

        lunch_spinner = this.findViewById(R.id.spinner_lunch);
        dinner_spinner = this.findViewById(R.id.spinner_dinner);
        dinner_side_spinner = this.findViewById(R.id.spinner_side_dinner);

        RecipeManagerSingleton sRecipeManager = RecipeManagerSingleton.getInstance();
        lunch_spinner.setAdapter(sRecipeManager.fc_names_adapter);
        dinner_spinner.setAdapter(sRecipeManager.sc_names_adapter);
        dinner_side_spinner.setAdapter(sRecipeManager.sd_names_adapter);
    }
}
