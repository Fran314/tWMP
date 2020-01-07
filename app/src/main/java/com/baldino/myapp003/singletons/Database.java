package com.baldino.myapp003.singletons;

import android.content.Context;

public class Database
{
    private static Database instance = null;
    private Context context;

    public IngredientManager std_ingredients, mnr_ingredients;
    public RecipeManager recipes;

    private Database(Context context)
    {
        this.context = context;

        std_ingredients = new IngredientManager();
        mnr_ingredients = new IngredientManager();

        recipes = new RecipeManager();
    }

    public synchronized static Database getInstance(Context context)
    {
        if(instance == null) {
            instance = new Database(context.getApplicationContext());
        }

        return instance;
    }
}
