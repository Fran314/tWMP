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

import com.baldino.myapp003.data_classes.Day;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.custom_views.EditableDayMealsView;
import com.baldino.myapp003.R;
import com.baldino.myapp003.data_classes.WeekData;
import com.baldino.myapp003.singletons.Database;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EditWeekActivity extends AppCompatActivity
{
    private EditableDayMealsView[] days;

    private List<List<List<Spinner>>> spinners;

    Database D;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_week);

        D = Database.getInstance();

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
            for(int j = 0; j < D.getMealsPerDay(); j++)
            {
                List<Spinner> meal_spinners = new ArrayList<>();
                //  Add first
                TableRow first_row = new TableRow(this);
                first_row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                TextView meal_name = new TextView(this);
                meal_name.setText(D.getMealName(j));
                TableRow.LayoutParams name_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                name_params.gravity = Gravity.CENTER_VERTICAL;
                meal_name.setLayoutParams(name_params);

                TextView first_type_name = new TextView(this);
                first_type_name.setText("[" + D.getNameOfCollection(D.getTypeOfMeal(0, j)) + "]");
                TableRow.LayoutParams first_type_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                first_type_params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                first_type_params.leftMargin = Util.intToDp(4, this);
                first_type_params.rightMargin = Util.intToDp(4, this);
                first_type_name.setLayoutParams(first_type_params);

                Spinner first_course_spinner = new Spinner(this);
                first_course_spinner.setAdapter(D.getNamesAdapterOfCollection(D.getTypeOfMeal(0, j), this));
                TableRow.LayoutParams first_spinner_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                first_spinner_params.gravity = Gravity.CENTER_VERTICAL;
                first_course_spinner.setLayoutParams(first_spinner_params);

                first_row.addView(meal_name);
                first_row.addView(first_type_name);
                first_row.addView(first_course_spinner);
                days[i].editable_meals_container.addView(first_row);

                meal_spinners.add(first_course_spinner);

                for(int k = 1; k < D.getCoursesDimOfMeal(j); k++)
                {
                    //  Add other rows
                    TableRow row = new TableRow(this);
                    first_row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    View filler = new View(this);
                    filler.setLayoutParams(new TableRow.LayoutParams(0, 0));

                    TextView type_name = new TextView(this);
                    type_name.setText("[" + D.getNameOfCollection(D.getTypeOfMeal(k, j)) + "]");
                    TableRow.LayoutParams type_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    type_params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                    type_params.leftMargin = Util.intToDp(4, this);
                    type_params.rightMargin = Util.intToDp(4, this);
                    type_name.setLayoutParams(type_params);

                    Spinner course_spinner = new Spinner(this);
                    course_spinner.setAdapter(D.getNamesAdapterOfCollection(D.getTypeOfMeal(k, j), this));
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

        if(D.isWeekNew() || !D.hasWeekSameFormat())
        {
            for (int i = 0; i < 7; i++)
            {
                //  Init to standard values
                for (int j = 0; j < D.getMealsPerDay(); j++)
                {
                    for (int k = 0; k < D.getCoursesDimOfMeal(j); k++)
                    {
                        spinners.get(i).get(j).get(k).setSelection(D.getStdOfMeal(k, j));
                    }
                }
            }
        }
        else
        {
            for(int i = 0; i < 7; i++)
            {
                //  Init to loaded values
                for(int j = 0; j < D.getMealsPerDay(); j++)
                {
                    for(int k = 0; k < D.getCoursesDimOfMeal(j); k++)
                    {
                        if(Util.compareStrings(D.getCourseOfMealOfDay(k, j, i), Util.NULL_RECIPE) == 0)
                        {
                            spinners.get(i).get(j).get(k).setSelection(0);
                        }
                        else
                        {
                            int pos = D.findRecipeOfCollectionIndex(D.getCourseOfMealOfDay(k, j, i), D.getTypeOfMeal(k, j)) + 1;
                            spinners.get(i).get(j).get(k).setSelection(pos);
                        }
                    }
                }
            }
        }

        LocalDate date = LocalDate.of(D.getYear(), D.getMonth(), D.getDayOfMonth());

        int d_offset = date.getDayOfWeek().getValue() - Util.FIRST_DAY_OF_WEEK;
        if(d_offset < 0) d_offset += 7;
        date = date.minusDays(d_offset);

        days[0].header.setText(Util.dateToString(date, true, this));

        date = date.plusDays(1);
        days[1].header.setText(Util.dateToString(date, true, this));

        date = date.plusDays(1);
        days[2].header.setText(Util.dateToString(date, true, this));

        date = date.plusDays(1);
        days[3].header.setText(Util.dateToString(date, true, this));

        date = date.plusDays(1);
        days[4].header.setText(Util.dateToString(date, true, this));

        date = date.plusDays(1);
        days[5].header.setText(Util.dateToString(date, true, this));

        date = date.plusDays(1);
        days[6].header.setText(Util.dateToString(date, true, this));
    }

    private void saveWeek()
    {
        WeekData new_week = new WeekData();
        for(int i = 0; i < 7; i++)
        {
            new_week.days[i] = new Day();
            for(int j = 0; j < D.getMealsPerDay(); j++)
            {
                List<String> courses_of_this_meal = new ArrayList<>();
                for(int k = 0; k < D.getCoursesDimOfMeal(j); k++)
                {
                    String course_name;
                    if(spinners.get(i).get(j).get(k).getSelectedItemPosition() == 0) course_name = Util.NULL_RECIPE;
                    //  I know... I know... waaaaaaaay too long of a line. Should work tho
                    else course_name = D.getNameOfRecipeOfCollection(spinners.get(i).get(j).get(k).getSelectedItemPosition() - 1, D.getTypeOfMeal(k, j));
                    courses_of_this_meal.add(course_name);
                }

                new_week.days[i].addMeal(courses_of_this_meal);
            }
        }

        new_week.is_new_week = false;

        new_week.meals_per_day = D.getMealsPerDay();

        for(int i = 0; i < D.getMealsPerDay(); i++)
        {
            new_week.meal_names.add(D.getMealName(i));
            new_week.courses_per_meal.add(D.getCoursesDimOfMeal(i));
        }
        /*
        for(int i = 0; i < 7; i++)
        {
            for(int j = 0; j < D.getMealsPerDay(); j++)
            {
                for(int k = 0; k < D.getCoursesDimOfMeal(j); k++)
                {
                    Log.w("AAA", i + " - " + j + " - " + k + ": " + new_week.days[i].getCourseOfmeal(k, j));
                }
            }
        }

         */
        //TODO
        // MAYBE YOU SHOULD PUT updateShoppingList INSIDE setWeekData AND REMOVE IT HERE
        D.setWeekData(new_week);
        D.updateShoppingList();

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
