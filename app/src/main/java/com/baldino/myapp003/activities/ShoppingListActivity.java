package com.baldino.myapp003.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.baldino.myapp003.R;
import com.baldino.myapp003.RecIngredient;
import com.baldino.myapp003.Recipe;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.singletons.RecipeManagerSingleton;
import com.baldino.myapp003.singletons.WeekManagerSingleton;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity
{
    private LinearLayout container;
    private List<RecIngredient> shopping_list;

    WeekManagerSingleton sWeekManager;
    RecipeManagerSingleton sRecipeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        container = findViewById(R.id.shopping_list_container);

        sWeekManager = WeekManagerSingleton.getInstance();
        sRecipeManager = RecipeManagerSingleton.getInstance();

        shopping_list = new ArrayList<>();

        for(int i = 0; i < 7; i++)
        {
            if(sWeekManager.days[i].hasSameFormat)
            {
                for(int j = 0; j < sWeekManager.courses_per_meal.size(); j++)
                {
                    for(int k = 0; k < sWeekManager.courses_per_meal.get(j); k++)
                    {
                        Recipe rec = sRecipeManager.recipe_types.get(sWeekManager.daily_meals.get(j).getType(k)).binaryFind(sWeekManager.days[i].getCourseOfmeal(k,j));
                        if(rec != null)
                        {
                            for(int h = 0; h < rec.ingredients.size(); h++)
                            {
                                addItem(rec.ingredients.get(h).getName(), rec.ingredients.get(h).getAmount());
                            }
                        }
                    }
                }
            }
        }

        for(RecIngredient rec_ingr : shopping_list)
        {
            CheckBox new_item = new CheckBox(this);
            new_item.setText(rec_ingr.getName() + " x" + rec_ingr.getAmount());
            container.addView(new_item);
        }
    }

    private void addItem(String name, float amount)
    {
        int index = binaryFindIndex(name);
        if(index == -1)
        {
            int pos = shopping_list.size();
            for(int i = 0; i < shopping_list.size(); i++)
            {
                if(Util.compareStrings(name, shopping_list.get(i).getName()) < 0)
                {
                    pos = i;
                    break;
                }
            }
            shopping_list.add(pos, new RecIngredient(name, amount));
        }
        else
        {
            shopping_list.get(index).setAmount(shopping_list.get(index).getAmount() + amount);
        }
    }

    private int binaryFindIndex(String name) { return binaryFindIndex(name, 0, shopping_list.size()-1); }
    private int binaryFindIndex(String name, int left, int right)
    {
        if(left  > right) return -1;

        int mid = left + ((right - left)/2);
        if(Util.compareStrings(name, shopping_list.get(mid).getName()) == 0) return mid;
        else if(Util.compareStrings(name, shopping_list.get(mid).getName()) < 0) return binaryFindIndex(name, left, mid-1);
        else return binaryFindIndex(name, mid+1, right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id) {
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
