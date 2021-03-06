package com.baldino.myapp003.main_fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baldino.myapp003.data_classes.MealFormat;
import com.baldino.myapp003.R;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.custom_views.EditableMealFormatView;
import com.baldino.myapp003.singletons.Database;

import java.util.ArrayList;
import java.util.List;

public class DayFormatFragment extends Fragment
{
    private Database D;
    private List<MealFormat> daily_meals;

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
                askSaveDailyMeals();
            }
        });

        D = Database.getInstance();

        daily_meals = D.getDailyMeals();

        emfv_list = new ArrayList<>();
        for (int i = 0; i < daily_meals.size(); i++) {
            addMealFormat(i);
            setDeleteButton(i);
        }

        ImageButton button_add = root.findViewById(R.id.button_add_meal_format);
        button_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                int pos = daily_meals.size();
                MealFormat new_meal = new MealFormat(getContext().getResources().getString(R.string.standard_new_meal));
                new_meal.addMeal(0, 0);
                daily_meals.add(new_meal);
                addMealFormat(pos);
                setDeleteButton(pos);
            }
        });

        return root;
    }

    public void addMealFormat(int pos)
    {
        EditableMealFormatView emfv = new EditableMealFormatView(getContext(), daily_meals.get(pos));

        View separator = new View(getContext());
        LinearLayout.LayoutParams separator_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Util.intToDp(1, getContext()));
        separator_params.setMargins(Util.intToDp(4, getContext()), Util.intToDp(4, getContext()), Util.intToDp(4, getContext()), Util.intToDp(4, getContext()));
        separator.setLayoutParams(separator_params);
        separator.setBackgroundColor(getResources().getColor(R.color.colorLightGray));

        emfv_list.add(emfv);

        days_container.addView(emfv, 2*pos);
        days_container.addView(separator, 2*pos + 1);
    }

    public void removeMealFormat(int pos)
    {
        daily_meals.remove(pos);

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

    public void askSaveDailyMeals()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.dialog_title_save_daily_meals));
        builder.setMessage(getResources().getString(R.string.dialog_text_save_daily_meals));
        builder.setPositiveButton(getResources().getString(R.string.dialog_button_save_daily_meals_yes), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                saveDailyMeals();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.dialog_button_save_daily_meals_no), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // Do nothing
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void saveDailyMeals()
    {
        daily_meals = new ArrayList<>();
        for(int i = 0; i < emfv_list.size(); i++)
        {
            daily_meals.add(emfv_list.get(i).getMealFormat());
        }

        D.setDailyMeals(daily_meals);

        Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_day_format_saved), Toast.LENGTH_LONG).show();
    }
}
