package com.baldino.myapp003.main_fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baldino.myapp003.MealFormat;
import com.baldino.myapp003.R;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.custom_views.EditableMealFormatView;
import com.baldino.myapp003.singletons.WeekManagerSingleton;

import java.util.ArrayList;
import java.util.List;

public class DayFormatFragment extends Fragment
{
    private WeekManagerSingleton sWeekManager;

    private LinearLayout days_container;
    private List<EditableMealFormatView> emfv_list;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_day_format, container, false);

        days_container = root.findViewById(R.id.container);
        ImageButton save_button = root.findViewById(R.id.button_save_daily_meals);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDailyMeals();
            }
        });

        sWeekManager = WeekManagerSingleton.getInstance();

        emfv_list = new ArrayList<>();
        for (int i = 0; i < sWeekManager.daily_meals.size(); i++) {
            addMealFormat(i);
            setDeleteButton(i);
        }

        ImageButton button_add = root.findViewById(R.id.button_add_meal_format);
        button_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                int pos = sWeekManager.daily_meals.size();
                MealFormat new_meal = new MealFormat(getContext().getResources().getString(R.string.standard_new_meal));
                new_meal.addMeal(0, 0);
                sWeekManager.daily_meals.add(new_meal);
                addMealFormat(pos);
                setDeleteButton(pos);
            }
        });

        return root;
    }

    public void addMealFormat(int pos)
    {
        EditableMealFormatView emfv = new EditableMealFormatView(getContext(), pos);

        View separator = new View(getContext());
        LinearLayout.LayoutParams separator_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Util.intToDp(1));
        separator_params.setMargins(Util.intToDp(4), Util.intToDp(4), Util.intToDp(4), Util.intToDp(4));
        separator.setLayoutParams(separator_params);
        separator.setBackgroundColor(getResources().getColor(R.color.colorLightGray));

        emfv_list.add(emfv);

        days_container.addView(emfv, 2*pos);
        days_container.addView(separator, 2*pos + 1);
    }

    public void removeMealFormat(int pos)
    {
        sWeekManager.daily_meals.remove(pos);

        days_container.removeViewAt(2*pos + 1);
        days_container.removeViewAt(2*pos);

        emfv_list.remove(pos);

        for(int i = 0; i < emfv_list.size(); i++)
        {
            setDeleteButton(i);
        }
    }

    public void setDeleteButton(final int pos)
    {
        emfv_list.get(pos).delete_whole_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeMealFormat(pos);
            }
        });
    }

    public void saveDailyMeals()
    {
        List<MealFormat> new_daily_meals = new ArrayList<>();
        for(int i = 0; i < emfv_list.size(); i++)
        {
            MealFormat new_meal_format = new MealFormat(emfv_list.get(i).name.getText().toString());

            for(int j = 0; j < emfv_list.get(i).types.size(); j++)
            {
                new_meal_format.addMeal(emfv_list.get(i).types.get(j).getSelectedItemPosition(), emfv_list.get(i).stds.get(j).getSelectedItemPosition());
            }

            new_daily_meals.add(new_meal_format);
        }

        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();

        sWeekManager.daily_meals = new_daily_meals;
        sWeekManager.saveDailyMeals();
        //  This load data is to handle the change in day format, because the currently loaded
        //  data won't have the same format as the new format, but has_same_format might still
        //  be set on true
        //TODO: there is probably a much more elegant way than calling a loadData, also because
        // this might be very inefficient
        sWeekManager.loadData();

        Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_day_format_saved), Toast.LENGTH_LONG).show();
    }
}
