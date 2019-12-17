package com.baldino.myapp003;

import java.util.ArrayList;
import java.util.List;

import static com.baldino.myapp003.Util.normalizeString;

public class Recipe
{
    private String name;
    public List<RecIngredient> ingredients;

    public Recipe()
    {
        ingredients = new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = normalizeString(name); }

    public static boolean alphabFirst(Recipe arg0, Recipe arg1)
    {
        if(arg0.getName().compareTo(arg1.getName())<0) //"a".compareTo("b") := -1
            return true;
        else return false;
    }

    public static boolean areEqual(Recipe arg0, Recipe arg1)
    {
        if(arg0.getName().equals(arg1.getName()))
            return true;
        else return false;
    }
}
