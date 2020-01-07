package com.baldino.myapp003.singletons;

import android.content.Context;

import com.baldino.myapp003.Util;

public class Database
{
    private static Database instance = null;
    private Context context;

    public IngredientManager std_ingredients, mnr_ingredients;
    public RecipeManager recipes;
    public WeekManager week_manager;

    private Database(Context context)
    {
        this.context = context;

        std_ingredients = new IngredientManager(Util.STANDARD_INGR_PATH);
        mnr_ingredients = new IngredientManager(Util.MINOR_INGR_PATH);

        recipes = new RecipeManager();

        week_manager = new WeekManager();
    }

    public synchronized static Database getInstance(Context context)
    {
        if(instance == null) {
            instance = new Database(context.getApplicationContext());
        }

        return instance;
    }

    public void loadAll()
    {
        std_ingredients.loadIngr(context);
        mnr_ingredients.loadIngr(context);

        recipes.loadTypeNames(context);

        week_manager.loadWeeks(context);
        week_manager.loadDailyMeals(context);
        week_manager.loadData(context);
    }
}
