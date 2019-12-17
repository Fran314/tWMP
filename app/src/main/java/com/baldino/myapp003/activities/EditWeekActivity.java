package com.baldino.myapp003.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.baldino.myapp003.EditableDayMeals;
import com.baldino.myapp003.R;
import com.baldino.myapp003.singletons.RecipeManagerSingleton;
import com.baldino.myapp003.singletons.WeekManagerSingleton;

import java.text.DateFormat;
import java.util.Calendar;

public class EditWeekActivity extends AppCompatActivity
{
    private int meals_index[];
    private EditableDayMeals[] days;
    private int year, month, day_of_month;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_week);

        Intent intent = getIntent();

        meals_index = intent.getIntArrayExtra("Meals_Index");
        year = intent.getIntExtra("Year", 1970);
        month = intent.getIntExtra("Month", 0);
        day_of_month = intent.getIntExtra("Day_Of_Month", 1);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day_of_month);

        c.set(Calendar.WEEK_OF_YEAR, c.get(Calendar.WEEK_OF_YEAR));

        days = new EditableDayMeals[7];

        days[0] = findViewById(R.id.editable_monday);
        days[1] = findViewById(R.id.editable_tuesday);
        days[2] = findViewById(R.id.editable_wednesday);
        days[3] = findViewById(R.id.editable_thursday);
        days[4] = findViewById(R.id.editable_friday);
        days[5] = findViewById(R.id.editable_saturday);
        days[6] = findViewById(R.id.editable_sunday);

        for(int i = 0; i < 7; i++)
        {
            days[i].lunch_spinner.setSelection(meals_index[i]);
            days[i].dinner_spinner.setSelection(meals_index[7+i]);
            days[i].dinner_side_spinner.setSelection(meals_index[14+i]);
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

    private void saveWeek()
    {
        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();
        RecipeManagerSingleton sRecipeManager = RecipeManagerSingleton.getInstance();

        for(int i = 0; i < 7; i++)
        {
            if(days[i].lunch_spinner.getSelectedItemPosition() != 0)
                sWeekManager.days[i].setLunch(sRecipeManager.getRecipe(days[i].lunch_spinner.getSelectedItemPosition()-1, 0).getName());
            else
                sWeekManager.days[i].setLunch("-");

            if(days[i].dinner_spinner.getSelectedItemPosition() != 0)
                sWeekManager.days[i].setDinner(sRecipeManager.getRecipe(days[i].dinner_spinner.getSelectedItemPosition()-1, 1).getName());
            else
                sWeekManager.days[i].setDinner("-");

            if(days[i].dinner_side_spinner.getSelectedItemPosition() != 0)
                sWeekManager.days[i].setSideDinner(sRecipeManager.getRecipe(days[i].dinner_side_spinner.getSelectedItemPosition()-1, 2).getName());
            else
                sWeekManager.days[i].setSideDinner("-");
        }

        sWeekManager.saveData();

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.generic_header_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.button_menu_save:
                saveWeek();
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
