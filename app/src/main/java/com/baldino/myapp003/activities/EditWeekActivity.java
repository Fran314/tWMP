package com.baldino.myapp003.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.baldino.myapp003.Day;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.custom_views.EditableDayMealsView;
import com.baldino.myapp003.R;
import com.baldino.myapp003.singletons.RecipeManagerSingleton;
import com.baldino.myapp003.singletons.ShoppingListSingleton;
import com.baldino.myapp003.singletons.WeekManagerSingleton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditWeekActivity extends AppCompatActivity
{
    private EditableDayMealsView[] days;
    private int year, month, day_of_month;

    private List<List<List<Spinner>>> spinners;

    WeekManagerSingleton sWeekManager;
    RecipeManagerSingleton sRecipeManager;
    ShoppingListSingleton sShoppingList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_week);

        sWeekManager = WeekManagerSingleton.getInstance();
        sRecipeManager = RecipeManagerSingleton.getInstance();
        sShoppingList = ShoppingListSingleton.getInstance();

        year = sWeekManager.year;
        month = sWeekManager.month;
        day_of_month = sWeekManager.day_of_month;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day_of_month);

        c.set(Calendar.WEEK_OF_YEAR, c.get(Calendar.WEEK_OF_YEAR));

        days = new EditableDayMealsView[7];

        days[0] = findViewById(R.id.editable_monday);
        days[1] = findViewById(R.id.editable_tuesday);
        days[2] = findViewById(R.id.editable_wednesday);
        days[3] = findViewById(R.id.editable_thursday);
        days[4] = findViewById(R.id.editable_friday);
        days[5] = findViewById(R.id.editable_saturday);
        days[6] = findViewById(R.id.editable_sunday);

        spinners = new ArrayList<>();

        for(int i = 0; i < 7; i++)
        {
            List<List<Spinner>> daily_spinners = new ArrayList<>();
            for(int j = 0; j < sWeekManager.daily_meals.size(); j++)
            {
                List<Spinner> meal_spinners = new ArrayList<>();
                //  Add first
                TableRow first_row = new TableRow(this);
                first_row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                TextView meal_name = new TextView(this);
                meal_name.setText(sWeekManager.daily_meals.get(j).getName());
                TableRow.LayoutParams name_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                name_params.gravity = Gravity.CENTER_VERTICAL;
                meal_name.setLayoutParams(name_params);

                TextView first_type_name = new TextView(this);
                first_type_name.setText("[" + sRecipeManager.getType(sWeekManager.daily_meals.get(j).getType(0)).getName() + "]");
                TableRow.LayoutParams first_type_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                first_type_params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                first_type_params.leftMargin = Util.intToDp(4);
                first_type_params.rightMargin = Util.intToDp(4);
                first_type_name.setLayoutParams(first_type_params);

                Spinner first_course_spinner = new Spinner(this);
                first_course_spinner.setAdapter(sRecipeManager.getType(sWeekManager.daily_meals.get(j).getType(0)).getNamesAdapter());
                TableRow.LayoutParams first_spinner_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                first_spinner_params.gravity = Gravity.CENTER_VERTICAL;
                first_course_spinner.setLayoutParams(first_spinner_params);

                first_row.addView(meal_name);
                first_row.addView(first_type_name);
                first_row.addView(first_course_spinner);
                days[i].editable_meals_container.addView(first_row);

                meal_spinners.add(first_course_spinner);

                for(int k = 1; k < sWeekManager.daily_meals.get(j).getDim(); k++)
                {
                    //  Add other rows
                    TableRow row = new TableRow(this);
                    first_row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    View filler = new View(this);
                    filler.setLayoutParams(new TableRow.LayoutParams(0, 0));

                    TextView type_name = new TextView(this);
                    type_name.setText("[" + sRecipeManager.getType(sWeekManager.daily_meals.get(j).getType(k)).getName() + "]");
                    TableRow.LayoutParams type_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    type_params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                    type_params.leftMargin = Util.intToDp(4);
                    type_params.rightMargin = Util.intToDp(4);
                    type_name.setLayoutParams(type_params);

                    Spinner course_spinner = new Spinner(this);
                    course_spinner.setAdapter(sRecipeManager.getType(sWeekManager.daily_meals.get(j).getType(k)).getNamesAdapter());
                    TableRow.LayoutParams spinner_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    spinner_params.gravity = Gravity.CENTER_VERTICAL;
                    course_spinner.setLayoutParams(spinner_params);

                    row.addView(filler);
                    row.addView(type_name);
                    row.addView(course_spinner);
                    days[i].editable_meals_container.addView(row);

                    meal_spinners.add(course_spinner);
                }

                daily_spinners.add(meal_spinners);
            }
            spinners.add(daily_spinners);
        }

        //TODO set the spinners to some value if it's the case, else set to standard value
        for(int i = 0; i < 7; i++)
        {
            if(sWeekManager.days[i].isNew || !sWeekManager.has_same_format)
            {
                //  Init at std values
                for(int j = 0; j < sWeekManager.daily_meals.size(); j++)
                {
                    for(int k = 0; k <sWeekManager.daily_meals.get(j).getDim(); k++)
                    {
                        spinners.get(i).get(j).get(k).setSelection(sWeekManager.daily_meals.get(j).getStd(k));
                    }
                }
            }
            else
            {
                //  Init at loaded values
                for(int j = 0; j < sWeekManager.daily_meals.size(); j++)
                {
                    for(int k = 0; k <sWeekManager.daily_meals.get(j).getDim(); k++)
                    {
                        if(Util.compareStrings(sWeekManager.days[i].getCourseOfmeal(k, j), "-") == 0)
                        {
                            spinners.get(i).get(j).get(k).setSelection(0);
                        }
                        else
                        {
                            int pos = sRecipeManager.getType(sWeekManager.daily_meals.get(j).getType(k)).binaryFindIndex(sWeekManager.days[i].getCourseOfmeal(k, j)) + 1;
                            spinners.get(i).get(j).get(k).setSelection(pos);
                        }
                    }
                }
            }
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
        for(int i = 0; i < 7; i++)
        {
            sWeekManager.days[i] = new Day(false);
            for(int j = 0; j < sWeekManager.daily_meals.size(); j++)
            {
                List<String> courses_of_this_meal = new ArrayList<>();
                for(int k = 0; k < sWeekManager.daily_meals.get(j).getDim(); k++)
                {
                    String course_name;
                    if(spinners.get(i).get(j).get(k).getSelectedItemPosition() == 0) course_name = "-";
                    //  I know... I know... waaaaaaaay too long of a line. Should work tho
                    else course_name = sRecipeManager.getType(sWeekManager.daily_meals.get(j).getType(k)).getRecipe(spinners.get(i).get(j).get(k).getSelectedItemPosition() - 1).getName();
                    courses_of_this_meal.add(course_name);
                }

                sWeekManager.days[i].addMeal(courses_of_this_meal);
            }
        }
        sWeekManager.has_same_format = true;

        sWeekManager.saveData();
        sShoppingList.updateShoppingList();

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
