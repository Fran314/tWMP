package com.baldino.myapp003.data_classes;

import com.baldino.myapp003.Util;

import java.util.ArrayList;
import java.util.List;

import static com.baldino.myapp003.Util.normalizeString;

public class Recipe
{
    private String name;
    private List<RecIngredient> ingredients;

    public Recipe()
    {
        ingredients = new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = normalizeString(name); }

    public List<RecIngredient> getCopyOfIngredients()
    {
        List<RecIngredient> to_return = new ArrayList<>();
        for(RecIngredient r : ingredients)
        {
            to_return.add(r.copyOf());
        }
        return to_return;
    }

    public void addIngredient(RecIngredient new_ingredient)
    {
        ingredients.add(new_ingredient);
    }

    public static boolean alphabFirst(Recipe arg0, Recipe arg1)
    {
        if(Util.compareStrings(arg0.getName(), arg1.getName())<0) //"a".compareTo("b") := -1
            return true;
        else return false;
    }
    public static boolean areEqual(Recipe arg0, Recipe arg1)
    {
        if(Util.compareStrings(arg0.getName(), arg1.getName()) == 0)
            return true;
        else return false;
    }
}
