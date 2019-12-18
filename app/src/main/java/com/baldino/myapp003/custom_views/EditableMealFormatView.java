package com.baldino.myapp003.custom_views;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.baldino.myapp003.R;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.singletons.RecipeManagerSingleton;
import com.baldino.myapp003.singletons.WeekManagerSingleton;

import java.util.ArrayList;
import java.util.List;

public class EditableMealFormatView extends LinearLayout
{
    WeekManagerSingleton sWeekManager;
    RecipeManagerSingleton sRecipeManager;

    private TableLayout table_container;
    public ImageButton delete_whole_button, add_button;
    public EditText name;
    public Spinner type, std;

    int meal;

    public List<Spinner> types;
    public List<Spinner> stds;
    public List<ImageButton> delete_buttons;

    public EditableMealFormatView(Context context, int i)
    {
        super(context);

        sWeekManager = WeekManagerSingleton.getInstance();
        sRecipeManager = RecipeManagerSingleton.getInstance();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_editable_meal_format, this);

        meal = i;

        types = new ArrayList<>();
        stds = new ArrayList<>();
        delete_buttons = new ArrayList<>();

        table_container = this.findViewById(R.id.table_container);
        name = this.findViewById(R.id.edit_main_name);
        type = this.findViewById(R.id.spinner_main_type);
        std = this.findViewById(R.id.spinner_main_std);

        name.setText(sWeekManager.daily_meals.get(meal).getName());

        types.add(type);
        stds.add(std);

        add_button = this.findViewById(R.id.button_add);

        add_button.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view) {
                addRow(sWeekManager.daily_meals.get(meal).getDim());
                sWeekManager.daily_meals.get(meal).addMeal(0, 0);
                readRow(sWeekManager.daily_meals.get(meal).getDim()-1);
                sWeekManager.saveDailyMeals();
            }
        });

        for(int j = 0; j < sWeekManager.daily_meals.get(meal).getDim(); j++)
        {
            if(j>0) addRow(j);
            readRow(j);
        }
    }

    public void removeRow(int pos)
    {
        sWeekManager.daily_meals.get(meal).removeMeal(pos);
        table_container.removeViewAt(2*pos + 1);
        table_container.removeViewAt(2*pos);
        types.remove(pos);
        stds.remove(pos);
        delete_buttons.remove(pos-1);
        for(int i = 0; i < types.size(); i++)
        {
            final int curr_pos = i;
            types.get(i).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int selected_index, long id)
                {
                    sWeekManager.daily_meals.get(meal).setType(curr_pos, selected_index);
                    stds.get(curr_pos).setAdapter(sRecipeManager.recipe_types.get(sWeekManager.daily_meals.get(meal).getType(curr_pos)).getNamesAdapter());
                    sWeekManager.saveDailyMeals();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            stds.get(i).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int selected_index, long id)
                {
                    sWeekManager.daily_meals.get(meal).setStd(curr_pos, selected_index);
                    sWeekManager.saveDailyMeals();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            if(i>0)
            {
                delete_buttons.get(i-1).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeRow(curr_pos);
                        sWeekManager.saveDailyMeals();
                    }
                });
            }
        }
    }

    public void readRow(int pos)
    {
        types.get(pos).setAdapter(sRecipeManager.type_names_adapter);
        types.get(pos).setSelection(sWeekManager.daily_meals.get(meal).getType(pos));

        stds.get(pos).setAdapter(sRecipeManager.recipe_types.get(sWeekManager.daily_meals.get(meal).getType(pos)).getNamesAdapter());
        stds.get(pos).setSelection(sWeekManager.daily_meals.get(meal).getStd(pos));
    }

    public void addRow(final int pos)
    {
        TableRow row_one = new TableRow(getContext());
        row_one.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        View filler_one = new View(getContext());
        filler_one.setLayoutParams(new TableRow.LayoutParams(0, 0));

        ImageButton button_delete = new ImageButton(getContext());
        button_delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_button_close_20dp));
        button_delete.setBackgroundColor(Color.TRANSPARENT);
        TableRow.LayoutParams button_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        button_params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        button_delete.setLayoutParams(button_params);
        button_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                removeRow(pos);
                sWeekManager.saveDailyMeals();
            }
        });
        delete_buttons.add(button_delete);

        Spinner spinner_type = new Spinner(getContext());
        TableRow.LayoutParams type_params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        type_params.gravity = Gravity.CENTER_VERTICAL;
        spinner_type.setLayoutParams(type_params);
        types.add(spinner_type);

        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int selected_index, long id)
            {
                sWeekManager.daily_meals.get(meal).setType(pos, selected_index);
                stds.get(pos).setAdapter(sRecipeManager.recipe_types.get(sWeekManager.daily_meals.get(meal).getType(pos)).getNamesAdapter());
                sWeekManager.saveDailyMeals();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        row_one.addView(filler_one);
        row_one.addView(button_delete);
        row_one.addView(spinner_type);


        TableRow row_two = new TableRow(getContext());
        row_two.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        View filler_two = new View(getContext());
        filler_two.setLayoutParams(new TableRow.LayoutParams(0, 0));

        View filler_three = new View(getContext());
        filler_three.setLayoutParams(new TableRow.LayoutParams(0, 0));

        Spinner spinner_std = new Spinner(getContext());
        TableRow.LayoutParams std_params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        std_params.gravity = Gravity.CENTER_VERTICAL;
        std_params.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        spinner_std.setLayoutParams(std_params);
        stds.add(spinner_std);

        spinner_std.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int selected_index, long id)
            {
                if(selected_index != sWeekManager.daily_meals.get(meal).getStd(pos))
                {
                    sWeekManager.daily_meals.get(meal).setStd(pos, selected_index);
                    sWeekManager.saveDailyMeals();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        row_two.addView(filler_two);
        row_two.addView(filler_three);
        row_two.addView(spinner_std);

        table_container.addView(row_one, 2*pos);
        table_container.addView(row_two, 2*pos + 1);
    }
}
