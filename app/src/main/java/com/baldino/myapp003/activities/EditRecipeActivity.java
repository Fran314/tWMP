package com.baldino.myapp003.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.baldino.myapp003.R;
import com.baldino.myapp003.data_classes.RecIngredient;
import com.baldino.myapp003.data_classes.Recipe;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.singletons.Database;

import java.util.ArrayList;
import java.util.List;

import static com.baldino.myapp003.Util.stringToFloat;

public class EditRecipeActivity extends AppCompatActivity
{
    private int collection, rec_pos;
    private boolean rec_new;

    private EditText name;
    private ImageButton button_add_rec_ingredient;
    private TableLayout ingredients_table;
    private List<RecIngredient> rec_ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_edit_recipe);

        collection = intent.getIntExtra("Collection", 0);
        rec_pos = intent.getIntExtra("Recipe_Position", -1);
        rec_new = intent.getBooleanExtra("Recipe_New", true);

        button_add_rec_ingredient = findViewById(R.id.button_add_rec_ingredient);
        button_add_rec_ingredient.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addIngredient();
            }
        });

        Database D = Database.getInstance();
        name = findViewById(R.id.edit_recipe_name);

        ingredients_table = findViewById(R.id.editable_recipe_ingredients_table);

        rec_ingredients = new ArrayList<>();
        if(rec_pos != -1)
        {
            name.setText(D.getRecipeOfCollection(rec_pos, collection).getName());
            rec_ingredients = D.getRecipeOfCollection(rec_pos, collection).getCopyOfIngredients();

            updateTable();
        }
        else
        {
            addIngredient();
        }
    }

    private void saveRecipe()
    {
        Recipe new_recipe = new Recipe();
        new_recipe.setName(name.getText().toString());
        for(int i = rec_ingredients.size()-1; i >= 0; i--)
        {
            if(Util.compareStrings(rec_ingredients.get(i).getName(), "") == 0) rec_ingredients.remove(i);
        }
        for(RecIngredient r : rec_ingredients) new_recipe.addIngredient(r);

        Database D = Database.getInstance();

        int index = D.findRecipeOfCollectionIndex(new_recipe.getName(), collection);
        if(!rec_new)
        {
            if(index == -1 || index == rec_pos)
            {
                D.updateRecipeOfCollection(rec_pos, collection, new_recipe);
                finish();
            }
            else Toast.makeText(this, getResources().getString(R.string.toast_cant_save_recipe), Toast.LENGTH_LONG).show();
        }
        else
        {
            if(index == -1)
            {
                D.addRecipeOfCollection(new_recipe, collection);
                finish();
            }
            else Toast.makeText(this, getResources().getString(R.string.toast_cant_save_recipe), Toast.LENGTH_LONG).show();
        }
    }

    private void updateTable()
    {
        ingredients_table.removeAllViews();

        for(int i = 0; i < rec_ingredients.size(); i++)
        {
            final int curr_pos = i;
            RecIngredient rec_ingr = rec_ingredients.get(i);
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            ImageButton button_delete = new ImageButton(this);
            button_delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_button_close_20dp));
            button_delete.setBackgroundColor(Color.TRANSPARENT);
            TableRow.LayoutParams button_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            button_params.gravity = Gravity.CENTER;
            button_delete.setLayoutParams(button_params);

            button_delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    removeIngredient(curr_pos);
                }
            });

            EditText name_in_row = new EditText(this);
            name_in_row.setText(rec_ingr.getName());
            name_in_row.setInputType(InputType.TYPE_CLASS_TEXT);
            name_in_row.setMaxLines(1);
            TableRow.LayoutParams name_params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
            name_params.gravity = Gravity.CENTER_VERTICAL;
            name_params.weight = 0.75f;
            name_in_row.setLayoutParams(name_params);
            name_in_row.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void afterTextChanged(Editable editable)
                {
                    rec_ingredients.get(curr_pos).setName(editable.toString());
                }
            });

            EditText amount_in_row = new EditText(this);
            amount_in_row.setText(Float.toString(rec_ingr.getAmount()));
            amount_in_row.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            TableRow.LayoutParams amount_params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
            amount_params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
            amount_params.weight = 0.25f;
            amount_in_row.setLayoutParams(amount_params);
            amount_in_row.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void afterTextChanged(Editable editable)
                {
                    rec_ingredients.get(curr_pos).setAmount(stringToFloat(editable.toString()));
                }
            });

            row.addView(button_delete);
            row.addView(name_in_row);
            row.addView(amount_in_row);

            ingredients_table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private void removeIngredient(int pos)
    {
        rec_ingredients.remove(pos);
        updateTable();
    }

    private void addIngredient()
    {
        rec_ingredients.add(new RecIngredient("", 0f));
        updateTable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.generic_header_menu, menu);
        if(!rec_new) getSupportActionBar().setTitle(getResources().getString(R.string.header_edit_recipe));
        else getSupportActionBar().setTitle(getResources().getString(R.string.header_new_recipe));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.button_menu_save:
                saveRecipe();
                return true;
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
