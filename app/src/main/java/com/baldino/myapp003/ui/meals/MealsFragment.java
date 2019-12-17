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

import com.baldino.myapp003.DayMeals;
import com.baldino.myapp003.activities.EditWeekActivity;
import com.baldino.myapp003.R;
import com.baldino.myapp003.activities.ShoppingListActivity;
import com.baldino.myapp003.singletons.RecipeManagerSingleton;
import com.baldino.myapp003.singletons.WeekManagerSingleton;

import java.text.DateFormat;
import java.util.Calendar;

public class MealsFragment extends Fragment implements DatePickerDialog.OnDateSetListener{

    private MealsViewModel mealsViewModel;

    private DayMeals[] days = new DayMeals[7];
    private ImageButton buttonShopList, buttonEdit, buttonCalendar;
    private TextView week_indicator;
    DatePickerDialog datePickerDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mealsViewModel = ViewModelProviders.of(this).get(MealsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_meals, container, false);


        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();

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
                RecipeManagerSingleton sRecipeManager = RecipeManagerSingleton.getInstance();
                WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();
                Intent intent = new Intent(getActivity(), EditWeekActivity.class);
                int meals_index[] = new int[21];
                for(int i = 0; i < 7; i++)
                {
                    meals_index[i] = sRecipeManager.binaryFindIndex(days[i].lunch.getText().toString(), 0) + 1;
                    meals_index[7+i] = sRecipeManager.binaryFindIndex(days[i].dinner.getText().toString(), 1) + 1;
                    meals_index[14+i] = sRecipeManager.binaryFindIndex(days[i].side_dish.getText().toString(), 2) + 1;
                }
                intent.putExtra("Meals_Index", meals_index);
                intent.putExtra("Year", sWeekManager.year);
                intent.putExtra("Month", sWeekManager.month);
                intent.putExtra("Day_Of_Month", sWeekManager.day_of_month);
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

        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();

        for(int i = 0; i < 7; i++)
        {
            days[i].lunch.setText(sWeekManager.days[i].getLunch());
            days[i].dinner.setText(sWeekManager.days[i].getDinner());
            days[i].side_dish.setText(sWeekManager.days[i].getSideDinner());
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day_of_month)
    {
        loadMeals(year, month, day_of_month);
    }

    private void loadMeals(int year, int month, int day_of_month)
    {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day_of_month);

        String week_text = "";
        c.set(Calendar.WEEK_OF_YEAR, c.get(Calendar.WEEK_OF_YEAR));
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        week_text += DateFormat.getDateInstance().format(c.getTime());
        week_text += " - ";
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        week_text += DateFormat.getDateInstance().format(c.getTime());
        week_indicator.setText(week_text);


        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();
        sWeekManager.setCalendar(year, month, day_of_month);
        sWeekManager.loadData();

        for(int i = 0; i < 7; i++)
        {
            days[i].lunch.setText(sWeekManager.days[i].getLunch());
            days[i].dinner.setText(sWeekManager.days[i].getDinner());
            days[i].side_dish.setText(sWeekManager.days[i].getSideDinner());
        }

        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        days[0].header.setText(getResources().getString(R.string.meals_monday) + ", " + DateFormat.getDateInstance().format(c.getTime()));

        c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        days[1].header.setText(getResources().getString(R.string.meals_tuesday) + ", " + DateFormat.getDateInstance().format(c.getTime()));

        c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        days[2].header.setText(getResources().getString(R.string.meals_wednesday) + ", " + DateFormat.getDateInstance().format(c.getTime()));

        c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        days[3].header.setText(getResources().getString(R.string.meals_thursday) + ", " + DateFormat.getDateInstance().format(c.getTime()));

        c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        days[4].header.setText(getResources().getString(R.string.meals_friday) + ", " + DateFormat.getDateInstance().format(c.getTime()));

        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        days[5].header.setText(getResources().getString(R.string.meals_saturday) + ", " + DateFormat.getDateInstance().format(c.getTime()));

        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        days[6].header.setText(getResources().getString(R.string.meals_sunday) + ", " + DateFormat.getDateInstance().format(c.getTime()));
    }
}