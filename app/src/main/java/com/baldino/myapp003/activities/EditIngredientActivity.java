package com.baldino.myapp003.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.baldino.myapp003.Ingredient;
import com.baldino.myapp003.R;
import com.baldino.myapp003.singletons.IngredientManagerSingleton;
import com.baldino.myapp003.databinding.ActivityEditIngredientBinding;

import static com.baldino.myapp003.Util.stringToFloat;

public class EditIngredientActivity extends AppCompatActivity {

    private int ingr_pos;
    private boolean ingr_new, is_standard;
    private EditText name, amount, unit, price;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ActivityEditIngredientBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_ingredient);

        IngredientManagerSingleton sIngredientManager = IngredientManagerSingleton.getInstance();

        ingr_pos = intent.getIntExtra("Ingredient_Position", -1);
        ingr_new = intent.getBooleanExtra("Ingredient_New", true);
        is_standard = intent.getBooleanExtra("Is_Standard", true);

        //TODO finish implementing is standard

        name = findViewById(R.id.edit_ingredient_name);
        amount = findViewById(R.id.edit_ingredient_amount);
        unit = findViewById(R.id.edit_ingredient_unit);
        price = findViewById(R.id.edit_ingredient_price);

        if(ingr_pos != -1)
        {
            Ingredient ingr;
            if(is_standard) ingr = sIngredientManager.standard_ingredients.get(ingr_pos);
            else ingr = sIngredientManager.minor_ingredients.get(ingr_pos);
            name.setText(ingr.getName());
            amount.setText(Float.toString(ingr.getAmount()));
            unit.setText(ingr.getUnit());
            price.setText(Float.toString(ingr.getPrice()));
        }

    }

    private void saveIngredient()
    {
        String s_name = name.getText().toString();
        String s_unit = unit.getText().toString();
        Float f_amount = stringToFloat(amount.getText().toString());
        Float f_price = stringToFloat(price.getText().toString());

        Ingredient new_ingredient = new Ingredient(s_name, f_amount, s_unit, f_price);

        IngredientManagerSingleton sIngredientManager = IngredientManagerSingleton.getInstance();

        if(is_standard)
        {
            if(!ingr_new)
            {
                sIngredientManager.removeStdIngr(ingr_pos);
            }

            sIngredientManager.addStdIngr(new_ingredient);
            sIngredientManager.saveStdIngr();
        }
        else
        {
            if(!ingr_new)
            {
                sIngredientManager.removeMnrIngr(ingr_pos);
            }

            sIngredientManager.addMnrIngr(new_ingredient);
            sIngredientManager.saveMnrIngr();
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.generic_header_menu, menu);
        if(is_standard)
        {
            if(ingr_new) getSupportActionBar().setTitle(getResources().getString(R.string.new_standard_ingredient_label));
            else getSupportActionBar().setTitle(getResources().getString(R.string.edit_standard_ingredient_label));
        }
        else
        {
            if(ingr_new) getSupportActionBar().setTitle(getResources().getString(R.string.new_minor_ingredient_label));
            else getSupportActionBar().setTitle(getResources().getString(R.string.edit_minor_ingredient_label));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.button_menu_save:
                saveIngredient();
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
