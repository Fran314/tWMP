package com.baldino.myapp003.ui.meals;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.baldino.myapp003.Util;
import com.baldino.myapp003.custom_views.DayMealsView;
import com.baldino.myapp003.activities.EditWeekActivity;
import com.baldino.myapp003.R;
import com.baldino.myapp003.activities.ShoppingListActivity;
import com.baldino.myapp003.singletons.RecipeManagerSingleton;
import com.baldino.myapp003.singletons.WeekManagerSingleton;

import java.text.DateFormat;
import java.util.Calendar;

public class MealsFragment extends Fragment implements DatePickerDialog.OnDateSetListener{

    private MealsViewModel mealsViewModel;

    private DayMealsView[] days = new DayMealsView[7];
    private ImageButton buttonShopList, buttonEdit, buttonCalendar;
    private TextView week_indicator;
    DatePickerDialog datePickerDialog;

    WeekManagerSingleton sWeekManager;
    RecipeManagerSingleton sRecipeManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mealsViewModel = ViewModelProviders.of(this).get(MealsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_meals, container, false);

        sWeekManager = WeekManagerSingleton.getInstance();
        sRecipeManager = RecipeManagerSingleton.getInstance();

        //TextView output_string = root.findViewById(R.id.output_string);
        //output_string.setText(getContext().getResources().getString(Util.getResId(getContext().getResources().getStringArray(R.array.id_array)[0], R.string.class)));
        //output_string.setText("WATTAAA");

        days[0] = root.findViewById(R.id.monday);
        days[1] = root.findViewById(R.id.tuesday);
        days[2] = root.findViewById(R.id.wednesday);
        days[3] = root.findViewById(R.id.thursday);
        days[4] = root.findViewById(R.id.friday);
        days[5] = root.findViewById(R.id.saturday);
        days[6] = root.findViewById(R.id.sunday);

        buttonShopList = root.findViewById(R.id.button_shopping_list);
        buttonShopList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), ShoppingListActivity.class);

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        buttonEdit = root.findViewById(R.id.button_edit_week);
        buttonEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditWeekActivity.class);

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        week_indicator = root.findViewById(R.id.text_week_indicator);

        loadMeals(sWeekManager.year, sWeekManager.month, sWeekManager.day_of_month);
        datePickerDialog = new DatePickerDialog(getContext(), this, sWeekManager.year, sWeekManager.month, sWeekManager.day_of_month);
        buttonCalendar = root.findViewById(R.id.button_calendar);
        buttonCalendar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        week_indicator.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day_of_month)
    {
        loadMeals(year, month, day_of_month);
    }

    private void loadMeals(int year, int month, int day_of_month)
    {
        sWeekManager.setCalendar(year, month, day_of_month);
        sWeekManager.loadData();

        updateUI();
    }

    private void updateUI()
    {
        //TODO

        //  Check if week daily_meals can correspond with the current daily_meals
        //  (In particular, check if sWeekManager.courses_per_meal's size and values make
        //  sense in this daily_meals)

        //      If it doesn't, just load and don't try to make sense out of it
        //      If it does, load and try to see if the loaded recipes actually exist in the
        //      correspondent recipe_type and write them in black or red
        boolean check = true;
        if(sWeekManager.daily_meals.size() != sWeekManager.meal_names.size()) check = false;
        for(int i = 0; i < sWeekManager.daily_meals.size() && check; i++)
        {
            if(sWeekManager.daily_meals.get(i).getDim() != sWeekManager.courses_per_meal.get(i)) check = false;
        }
        for(int i = 0; i < sWeekManager.daily_meals.size() && check; i++)
        {
            if(Util.compareStrings(sWeekManager.daily_meals.get(i).getName(), sWeekManager.meal_names.get(i)) != 0) check = false;
        }

        if(check)
        {
            for(int i = 0; i < 7; i++)
            {
                days[i].emptyUI();
                for(int j = 0; j < sWeekManager.daily_meals.size(); j++)
                {
                    if(sRecipeManager.getType(sWeekManager.daily_meals.get(j).getType(0)).binaryFindIndex(sWeekManager.days[i].getCourseOfmeal(0, j)) != -1)
                        days[i].addFirstRow(sWeekManager.daily_meals.get(j).getName(), sWeekManager.days[i].getCourseOfmeal(0, j), getContext().getResources().getColor(R.color.colorBlack));
                    else
                        days[i].addFirstRow(sWeekManager.daily_meals.get(j).getName(), sWeekManager.days[i].getCourseOfmeal(0, j), getContext().getResources().getColor(R.color.colorErrorRed));

                    for(int k = 1; k < sWeekManager.daily_meals.get(j).getDim(); k++)
                    {
                        if(sRecipeManager.getType(sWeekManager.daily_meals.get(j).getType(k)).binaryFindIndex(sWeekManager.days[i].getCourseOfmeal(k, j)) != -1)
                            days[i].addRow(sWeekManager.days[i].getCourseOfmeal(k, j), getContext().getResources().getColor(R.color.colorBlack));
                        else
                            days[i].addRow(sWeekManager.days[i].getCourseOfmeal(k, j), getContext().getResources().getColor(R.color.colorErrorRed));
                    }
                }
            }
        }
        else
        {
            for(int i = 0; i < 7; i++)
            {
                days[i].emptyUI();
                for(int j = 0; j < sWeekManager.meal_names.size(); j++)
                {
                    days[i].addFirstRow(sWeekManager.meal_names.get(j), sWeekManager.days[i].getCourseOfmeal(0, j), getContext().getResources().getColor(R.color.colorBlack));

                    for(int k = 1; k < sWeekManager.courses_per_meal.get(j); k++)
                    {
                        days[i].addRow(sWeekManager.days[i].getCourseOfmeal(k, j), getContext().getResources().getColor(R.color.colorBlack));
                    }
                }
            }
        }


        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, sWeekManager.year);
        c.set(Calendar.MONTH, sWeekManager.month);
        c.set(Calendar.DAY_OF_MONTH, sWeekManager.day_of_month);

        String week_text = "";
        int offset = c.get(Calendar.DAY_OF_WEEK) - 2;
        if(offset < 0) offset += 7;
        c.add(Calendar.DATE, -offset);
        week_text += DateFormat.getDateInstance().format(c.getTime());
        week_text += " - ";
        c.add(Calendar.DATE, 6);
        week_text += DateFormat.getDateInstance().format(c.getTime());
        week_indicator.setText(week_text);

        //c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.add(Calendar.DATE, -6);
        days[0].header.setText(getResources().getString(R.string.meals_monday) + ", " + DateFormat.getDateInstance().format(c.getTime()));

        //c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        c.add(Calendar.DATE, 1);
        days[1].header.setText(getResources().getString(R.string.meals_tuesday) + ", " + DateFormat.getDateInstance().format(c.getTime()));

        //c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        c.add(Calendar.DATE, 1);
        days[2].header.setText(getResources().getString(R.string.meals_wednesday) + ", " + DateFormat.getDateInstance().format(c.getTime()));

        //c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        c.add(Calendar.DATE, 1);
        days[3].header.setText(getResources().getString(R.string.meals_thursday) + ", " + DateFormat.getDateInstance().format(c.getTime()));

        //c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        c.add(Calendar.DATE, 1);
        days[4].header.setText(getResources().getString(R.string.meals_friday) + ", " + DateFormat.getDateInstance().format(c.getTime()));

        //c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        c.add(Calendar.DATE, 1);
        days[5].header.setText(getResources().getString(R.string.meals_saturday) + ", " + DateFormat.getDateInstance().format(c.getTime()));

        //c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        c.add(Calendar.DATE, 1);
        days[6].header.setText(getResources().getString(R.string.meals_sunday) + ", " + DateFormat.getDateInstance().format(c.getTime()));
    }
}