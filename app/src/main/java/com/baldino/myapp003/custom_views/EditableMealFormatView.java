package com.baldino.myapp003.custom_views;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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

public class EditableMealFormatView extends LinearLayout {

    private TableLayout table_container;
    public ImageButton delete_whole_button;
    public EditText name;
    public Spinner type, std;

    int meal;

    public List<Spinner> types;
    public List<Spinner> stds;
    public List<ImageButton> delete_buttons;

    public EditableMealFormatView(Context context, int i)
    {
        super(context);

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

        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();
        RecipeManagerSingleton sRecipeManager = RecipeManagerSingleton.getInstance();

        name.setText(sWeekManager.daily_meals.get(meal).getName());
        type.setAdapter(sRecipeManager.type_names_adapter);
        type.setSelection(sWeekManager.daily_meals.get(meal).getType(0));
        std.setAdapter(sRecipeManager.recipe_types.get(sWeekManager.daily_meals.get(meal).getType(0)).getNamesAdapter());
        std.setSelection(sWeekManager.daily_meals.get(meal).getStd(0));


        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        View filler_one = new View(getContext());
        filler_one.setLayoutParams(new TableRow.LayoutParams(0, 0));

        View filler_two = new View(getContext());
        filler_two.setLayoutParams(new TableRow.LayoutParams(0, 0));

        ImageButton button_add = new ImageButton(getContext());
        button_add.setImageDrawable(getResources().getDrawable(R.drawable.ic_button_add_24dp));
        button_add.setBackgroundColor(Color.TRANSPARENT);
        TableRow.LayoutParams button_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        button_params.gravity = Gravity.CENTER;
        button_add.setLayoutParams(button_params);

        row.addView(filler_one);
        row.addView(filler_two);
        row.addView(button_add);

        table_container.addView(row);

        for(int j = 1; j < sWeekManager.daily_meals.get(meal).getDim(); j++)
        {
            addRow(j);
        }
    }

    public void addRow(int pos)
    {
        RecipeManagerSingleton sRecipeManager = RecipeManagerSingleton.getInstance();
        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();

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
            }
        });

        Spinner spinner_type = new Spinner(getContext());
        TableRow.LayoutParams type_params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        type_params.gravity = Gravity.CENTER_VERTICAL;
        spinner_type.setLayoutParams(type_params);
        spinner_type.setAdapter(sRecipeManager.type_names_adapter);

        spinner_type.setSelection(sWeekManager.daily_meals.get(meal).getType(pos));

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
        spinner_std.setAdapter(sRecipeManager.recipe_types.get(sWeekManager.daily_meals.get(meal).getType(pos)).getNamesAdapter());
        spinner_std.setSelection(sWeekManager.daily_meals.get(meal).getStd(pos));


        row_two.addView(filler_two);
        row_two.addView(filler_three);
        row_two.addView(spinner_std);

        table_container.addView(row_one, 2*pos);
        table_container.addView(row_two, 2*pos + 1);
    }
}
