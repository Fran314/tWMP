package com.baldino.myapp003.custom_views;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.baldino.myapp003.data_classes.MealFormat;
import com.baldino.myapp003.singletons.Database;

import java.util.ArrayList;
import java.util.List;

public class EditableMealFormatView extends LinearLayout
{
    private Database D;

    public MealFormat meal_format;

    private TableLayout table_container;
    public ImageButton delete_whole_button, add_button;
    public EditText name;
    public Spinner type, std;

    public List<Spinner> types;
    public List<Spinner> stds;
    public List<ImageButton> delete_buttons;

    public EditableMealFormatView(Context context, MealFormat init_format)
    {
        super(context);

        D = Database.getInstance();

        this.meal_format = init_format;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_editable_meal_format, this);

        types = new ArrayList<>();
        stds = new ArrayList<>();
        delete_buttons = new ArrayList<>();

        table_container = this.findViewById(R.id.table_container);
        name = this.findViewById(R.id.edit_main_name);
        type = this.findViewById(R.id.spinner_main_type);
        std = this.findViewById(R.id.spinner_main_std);
        delete_whole_button = this.findViewById(R.id.button_delete_whole_meal_format);

        name.setText(meal_format.getName());
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                meal_format.setName(editable.toString());
            }
        });

        types.add(type);
        stds.add(std);

        add_button = this.findViewById(R.id.button_add);

        add_button.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view) {
                int pos = meal_format.getDim();
                addRow(pos);
                meal_format.addMeal(0,0);
                readRow(pos);
                setListeners(pos);
            }
        });


        for(int j = 0; j < meal_format.getDim(); j++)
        {
            if(j > 0) addRow(j);
            readRow(j);
            setListeners(j);
        }
    }

    public void removeRow(int pos)
    {
        meal_format.removeMeal(pos);
        table_container.removeViewAt(2*pos + 1);
        table_container.removeViewAt(2*pos);
        types.remove(pos);
        stds.remove(pos);
        delete_buttons.remove(pos-1);
        for(int i = 0; i < types.size(); i++)
        {
            setListeners(i);
        }
    }

    public void setListeners(final int pos)
    {
        /*
            ... Listen, I have no idea why I have to put this setSelection(..., false) here but
            if I do, it works. If I don't put it here, for some reason the onItemSelected() function
            gets called (EVEN THOUGH IT'S DEFINED __AFTER__ I DEFINE THE ADAPTER?!?) and the
            correspondent standard recipe (std) spinner gets initialized to the value 0, no matter
            what value was saved and loaded... I tried in many ways, this works, so just, eh...

            Thanks to Dayanand Waghmare over StackOverflow for helping someone else with an issue
            similar to mine
            [https://stackoverflow.com/questions/13397933/android-spinner-avoid-onitemselected-calls-during-initialization]
         */

        types.get(pos).setSelection(types.get(pos).getSelectedItemPosition(),false);
        types.get(pos).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int selected_index, long id)
            {
                meal_format.setType(pos, selected_index);
                meal_format.setStd(pos, 0);
                stds.get(pos).setAdapter(D.getNamesAdapterOfCollection(selected_index, getContext()));
                stds.get(pos).setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        stds.get(pos).setSelection(stds.get(pos).getSelectedItemPosition(),false);
        stds.get(pos).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int selected_index, long id)
            {
                meal_format.setStd(pos, selected_index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if(pos>0)
        {
            delete_buttons.get(pos-1).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeRow(pos);
                }
            });
        }

    }

    public void readRow(int pos)
    {
        types.get(pos).setAdapter(D.getCollectionNamesAdapter(getContext()));
        types.get(pos).setSelection(meal_format.getType(pos));

        stds.get(pos).setAdapter(D.getNamesAdapterOfCollection(meal_format.getType(pos), getContext()));
        stds.get(pos).setSelection(meal_format.getStd(pos));
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
        delete_buttons.add(button_delete);

        Spinner spinner_type = new Spinner(getContext());
        TableRow.LayoutParams type_params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        type_params.gravity = Gravity.CENTER_VERTICAL;
        spinner_type.setLayoutParams(type_params);
        types.add(spinner_type);

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

        row_two.addView(filler_two);
        row_two.addView(filler_three);
        row_two.addView(spinner_std);

        table_container.addView(row_one, 2*pos);
        table_container.addView(row_two, 2*pos + 1);
    }

    public MealFormat getMealFormat() { return meal_format; }
}
