package com.baldino.myapp003.singletons;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.baldino.myapp003.IngredientListAdapter;
import com.baldino.myapp003.R;
import com.baldino.myapp003.RecipeCollection;
import com.baldino.myapp003.RecipeListAdapter;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.data_classes.Ingredient;
import com.baldino.myapp003.data_classes.MealFormat;
import com.baldino.myapp003.data_classes.RecIngredient;
import com.baldino.myapp003.data_classes.Recipe;
import com.baldino.myapp003.data_classes.WeekData;
import com.baldino.myapp003.main_fragments.IngredientsFragment;
import com.baldino.myapp003.main_fragments.RecipesFragment;

import java.io.File;
import java.time.DayOfWeek;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class Database
{
    private static Database instance = null;
    private Context context = null;

    private SettingsManager settings;
    private IngredientManager std_ingredients, mnr_ingredients;
    private RecipeManager recipes;
    private WeekManager week_manager;
    private ShoppingListManager shopping_list;

    private Database()
    {
        settings = new SettingsManager();

        std_ingredients = new IngredientManager(true);
        mnr_ingredients = new IngredientManager(false);

        recipes = new RecipeManager();

        week_manager = new WeekManager();

        shopping_list = new ShoppingListManager();
    }
    public synchronized static Database getInstance()
    {
        if(instance == null) {
            instance = new Database();
        }

        return instance;
    }
    public void setContext(Context context) { this.context = context.getApplicationContext(); }

    public void loadAll()
    {
        if(context != null)
        {
            settings.loadSettings(context);

            std_ingredients.loadIngr(context);
            mnr_ingredients.loadIngr(context);

            recipes.loadCollectionList(context);
            recipes.loadCollections(context);

            week_manager.loadWeeks(context);
            week_manager.loadDailyMeals(context);
            week_manager.loadData(context, settings.getFirstDayOfWeek());

            updateShoppingList();
            shopping_list.loadValues(context);
        }
    }

    //--- FIRST INIT METHODS ---//
    public boolean isFirstStart()
    {
        File first_start = new File(context.getFilesDir(), Util.SETTINGS_PATH);
        if(first_start.exists()) return false;

        Locale locale = Locale.getDefault();
        Currency currency = Currency.getInstance(locale);
        DayOfWeek first_day_of_week = WeekFields.of(locale).getFirstDayOfWeek();

        settings.setCurrency(currency.getSymbol());
        settings.setFirstDayOfWeek(first_day_of_week.getValue());
        settings.saveSettings(context);

        return true;
    }
    public void createInitFiles()
    {
        InitiatorClass.initBasicTemplate(context, settings.getFirstDayOfWeek());
    }
    //---   ---//

    //--- SETTINGS METHODS ---//
    public String getCurrency() { return settings.getCurrency(); }
    public int getFirstDayOfWeek() { return settings.getFirstDayOfWeek(); }
    public void setCurrency(String new_currency)
    {
        int result = settings.setCurrency(new_currency);
        if(result != -1)
        {
            settings.saveSettings(context);
        }
    }
    public void setFirstDayOfWeek(int new_fdow)
    {
        int result = settings.setFirstDayOfWeek(new_fdow);
        if(result != -1)
        {
            settings.saveSettings(context);
        }
    }
    //---   ---//

    //--- STD & MNR INGREDIENTS METHODS ---//
    public void setStdIngrFragment(IngredientsFragment fragment) { std_ingredients.setFragment(fragment); }
    public void setMnrIngrFragment(IngredientsFragment fragment) { mnr_ingredients.setFragment(fragment); }
    public IngredientListAdapter getStdIngrAdapter() { return std_ingredients.getAdapter(); }
    public IngredientListAdapter getMnrIngrAdapter() { return mnr_ingredients.getAdapter(); }
    public Ingredient getStdIngr(int pos) { return std_ingredients.getIngr(pos); }
    public Ingredient getMnrIngr(int pos) { return mnr_ingredients.getIngr(pos); }
    public Ingredient findStdIngr(String name) { return std_ingredients.binaryFindIngr(name); }
    public Ingredient findMnrIngr(String name) { return mnr_ingredients.binaryFindIngr(name); }
    public int findStdIngrIndex(String name) { return std_ingredients.binaryFindIndex(name); }
    public int findMnrIngrIndex(String name) { return mnr_ingredients.binaryFindIndex(name); }
    public int getStdIngrSize() { return std_ingredients.getSize(); }
    public int getMnrIngrSize() { return mnr_ingredients.getSize(); }
    public void removeStdIngr(int pos)
    {
        int result = std_ingredients.removeIngr(pos);
        if(result != -1)
        {
            updateShoppingList();
            shopping_list.saveValues(context);
            std_ingredients.saveIngr(context);
        }
    }
    public void removeMnrIngr(int pos)
    {
        int result = mnr_ingredients.removeIngr(pos);
        if(result != -1)
        {
            updateShoppingList();
            shopping_list.saveValues(context);
            mnr_ingredients.saveIngr(context);
        }
    }
    public void updateStdIngr(int pos, Ingredient new_ingredient)
    {
        int remove = std_ingredients.removeIngr(pos);
        int add = std_ingredients.addIngr(new_ingredient);
        if(remove != -1 && add != -1)
        {
            updateShoppingList();
            shopping_list.saveValues(context);
            std_ingredients.saveIngr(context);
        }
    }
    public void updateMnrIngr(int pos, Ingredient new_ingredient)
    {
        int remove = mnr_ingredients.removeIngr(pos);
        int add = mnr_ingredients.addIngr(new_ingredient);
        if(remove != -1 && add != -1)
        {
            updateShoppingList();
            shopping_list.saveValues(context);
            mnr_ingredients.saveIngr(context);
        }
    }
    public void addStdIngr(Ingredient new_ingredient)
    {
        int result = std_ingredients.addIngr(new_ingredient);
        if(result != -1)
        {
            updateShoppingList();
            shopping_list.saveValues(context);
            std_ingredients.saveIngr(context);
        }
    }
    public void addMnrIngr(Ingredient new_ingredient)
    {
        int result = mnr_ingredients.addIngr(new_ingredient);
        if(result != -1)
        {
            updateShoppingList();
            shopping_list.saveValues(context);
            mnr_ingredients.saveIngr(context);
        }
    }
    //---   ---//

    //--- RECIPES METHODS ---//
    public Recipe getRecipeOfCollection(int recipe, int collection_index){ return recipes.getRecipeOfCollection(recipe, collection_index); }
    public int findRecipeOfCollectionIndex(String name, int collection) { return recipes.findRecipeOfCollectionIndex(name, collection); }
    public int getSizeOfCollection(int collection) { return recipes.getSizeOfCollection(collection); }
    public void removeRecipeOfCollection(int recipe, int collection)
    {
        int result = recipes.removeRecipeOfCollection(recipe, collection);
        if(result != -1)
        {
            week_manager.removedRecipe(collection, recipe);
            week_manager.saveDailyMeals(context);
            updateShoppingList();
            shopping_list.saveValues(context);
            recipes.saveCollection(collection, context);
        }
    }
    public void updateRecipeOfCollection(int recipe, int collection, Recipe new_recipe)
    {
        int remove = recipes.removeRecipeOfCollection(recipe, collection);
        int add = recipes.addRecipeOfCollection(new_recipe, collection);
        if(remove != -1 && add != -1)
        {
            week_manager.updatedRecipe(collection, recipe, add);
            week_manager.saveDailyMeals(context);
            updateShoppingList();
            shopping_list.saveValues(context);
            recipes.saveCollection(collection, context);
        }
    }
    public void addRecipeOfCollection(Recipe new_recipe, int collection)
    {
        int result = recipes.addRecipeOfCollection(new_recipe, collection);
        if(result != -1)
        {
            week_manager.addedRecipe(collection, result);
            week_manager.saveDailyMeals(context);
            updateShoppingList();
            shopping_list.saveValues(context);
            recipes.saveCollection(collection, context);
        }
    }
    public String getNameOfCollection(int collection) { return recipes.getNameOfCollection(collection); }
    public ArrayAdapter<String> getNamesAdapterOfCollection(int collection, Context curr_context) { return recipes.getNamesAdapterOfCollection(collection, curr_context); }
    public String getNameOfRecipeOfCollection(int recipe, int collection) { return recipes.getNameOfRecipeOfCollection(recipe, collection); }
    public ArrayAdapter<String> getCollectionNamesAdapter(Context curr_context) { return recipes.getCollectionNamesAdapter(curr_context); }
    public RecipeListAdapter getRecipeListAdapterOfCollection(int collection) { return recipes.getListAdapterOfCollection(collection); }
    public int getCollectionsSize() { return recipes.getCollectionsSize(); }
    public void updateCollectionName(String name, int collection)
    {
        String last_name = getNameOfCollection(collection);

        recipes.updateCollectionName(name, collection);
        recipes.saveCollection(collection, context);
        recipes.saveCollectionsList(context);

        File folder = new File(context.getFilesDir(), Util.COLLECTIONS_FOLDER);
        folder.mkdirs();

        File to_delete = new File(folder, Util.nameToFileName(last_name) + ".txt");
        to_delete.delete();
    }
    public void addCollection(RecipeCollection new_collection)
    {
        int result = recipes.addCollection(new_collection);
        recipes.saveCollection(result, context);
        recipes.saveCollectionsList(context);

        week_manager.addedCollection(result);
        week_manager.saveDailyMeals(context);
    }
    public void removeCollection(int pos)
    {
        File folder = new File(context.getFilesDir(), Util.COLLECTIONS_FOLDER);
        folder.mkdirs();
        File to_delete = new File(folder, Util.nameToFileName(recipes.getNameOfCollection(pos)) + ".txt");
        if(to_delete.exists()) to_delete.delete();

        int result = recipes.removeCollection(pos);
        if(result != -1)
        {
            week_manager.removedCollection(pos);
            week_manager.saveDailyMeals(context);
            recipes.saveCollectionsList(context);
        }
    }
    public void setRecipeListAdapterFragment(RecipesFragment fragment, int collection) { recipes.setListAdapterFragment(fragment, collection); }
    public int getExpandedValueOfCollection(int collection) { return recipes.getExpandedValueOfCollection(collection); }
    //---   ---//

    //--- WEEK MANAGER METHODS ---//
    public int getMealsPerDay() { return week_manager.getMealsPerDay(); }
    public String getMealName(int meal) { return week_manager.getMealName(meal); }
    public int getTypeOfMeal(int type, int meal) { return week_manager.getTypeOfMeal(type, meal); }
    public int getStdOfMeal(int std, int meal) { return week_manager.getStdOfMeal(std, meal); }
    public int getCoursesDimOfMeal(int meal) { return week_manager.getCoursesDimOfMeal(meal); }
    public boolean isWeekNew() { return week_manager.isWeekNew(); }
    public boolean hasWeekSameFormat() { return week_manager.hasWeekSameFormat(); }
    public String getCourseOfMealOfDay(int course, int meal, int day) { return week_manager.getCourseOfMealOfDay(course, meal, day); }
    public int getYear() { return week_manager.getYear(); }
    public int getMonth() { return week_manager.getMonth(); }
    public int getDayOfMonth() { return week_manager.getDayOfMonth(); }
    public void setWeekData(WeekData new_week)
    {
        int result = week_manager.setWeekData(new_week);
        if(result != -1)
        {
            week_manager.updateHasSameFormat();
            updateShoppingList();
            shopping_list.saveValues(context);
            week_manager.saveData(context, settings.getFirstDayOfWeek());
        }
    }
    public List<MealFormat> getDailyMeals() { return week_manager.getDailyMeals(); }
    public void setDailyMeals(List<MealFormat> daily_meals)
    {
        int result = week_manager.setDailyMeals(daily_meals);
        if(result != -1)
        {
            week_manager.updateHasSameFormat();
            updateShoppingList();
            shopping_list.saveValues(context);
            week_manager.saveDailyMeals(context);
        }
    }
    public WeekData getLoadedWeek() { return week_manager.getLoadedWeek(); }
    public void setCalendar(int year, int month, int day_of_month)
    {
        week_manager.setCalendar(year, month, day_of_month);

        week_manager.loadData(context, settings.getFirstDayOfWeek());
        updateShoppingList();
        shopping_list.saveValues(context);
    }
    public void loadWeekData()
    {
        week_manager.loadData(context, settings.getFirstDayOfWeek());
        updateShoppingList();
        shopping_list.saveValues(context);
    }
    public List<String> getProblematicPairs() { return week_manager.getProblematicPairs(context); }
    public void refactorWeeks(int old_first_day_of_week, int new_first_day_of_week) { week_manager.refactor(old_first_day_of_week, new_first_day_of_week, context); }
    public int getSavedWeeksAmount() { return week_manager.getSavedWeeksAmount(); }
    public List<String> getSavedWeeks() { return week_manager.getSavedWeeks(); }
    public void removeSavedWeek(int pos)
    {
        int result = week_manager.removeSavedWeek(pos, settings.getFirstDayOfWeek());
        if(result != -1)
        {
            week_manager.saveWeeks(context);
            if(result == 1)
            {
                week_manager.loadData(context, settings.getFirstDayOfWeek());
                week_manager.updateHasSameFormat();
            }
        }
    }
    //---   ---//

    //--- SHOPPING LIST MANAGER METHODS ---//
    public void updateShoppingList()
    {
        List<RecIngredient> shopping_list_ingredients = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Boolean> values = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        if(week_manager.hasWeekSameFormat())
        {
            for(int i = 0; i < 7; i++)
            {
                for(int j = 0; j < week_manager.getMealsPerDay(); j++)
                {
                    for(int k = 0; k < week_manager.getCoursesDimOfMeal(j); k++)
                    {
                        Recipe rec = recipes.findRecipeOfCollection(week_manager.getCourseOfMealOfDay(k, j, i), week_manager.getTypeOfMeal(k, j));
                        if(rec != null)
                        {
                            List<RecIngredient> ingredients = rec.getCopyOfIngredients();
                            for(int h = 0; h < ingredients.size(); h++)
                            {
                                String ingredient_name = ingredients.get(h).getName();
                                //--- BinaryFind the item if is already in the list ---//
                                int left = 0, right = shopping_list_ingredients.size()-1, mid = -1;
                                boolean found = false;
                                while(left <= right && !found)
                                {
                                    mid = left + ((right - left)/2);
                                    if(Util.compareStrings(ingredient_name, shopping_list_ingredients.get(mid).getName()) == 0) found = true;
                                    else if(Util.compareStrings(ingredient_name, shopping_list_ingredients.get(mid).getName()) < 0) right = mid-1;
                                    else left = mid+1;
                                }
                                //--- ---//

                                //--- AddItem to the list if it's not there, increase amount otherwise ---//
                                if(!found)
                                {
                                    int pos = shopping_list_ingredients.size();
                                    for(int r = 0; r < shopping_list_ingredients.size(); r++)
                                    {
                                        if(Util.compareStrings(ingredient_name, shopping_list_ingredients.get(r).getName()) < 0)
                                        {
                                            pos = r;
                                            break;
                                        }
                                    }
                                    shopping_list_ingredients.add(pos, new RecIngredient(ingredient_name, ingredients.get(h).getAmount()));
                                }
                                else
                                {
                                    shopping_list_ingredients.get(mid).setAmount(shopping_list_ingredients.get(mid).getAmount() + ingredients.get(h).getAmount());
                                }
                                //--- ---//
                            }
                        }
                    }
                }
            }
        }

        for(RecIngredient rec_ingr : shopping_list_ingredients)
        {
            boolean is_std = true;
            Ingredient ingr = std_ingredients.binaryFindIngr(rec_ingr.getName());
            if(ingr == null)
            {
                ingr = mnr_ingredients.binaryFindIngr(rec_ingr.getName());
                is_std = false;
            }

            if(ingr != null)
            {
                if(is_std)
                {
                    int packages = (int) Math.ceil(rec_ingr.getAmount()/ingr.getAmount());
                    labels.add(rec_ingr.getName() + " x" + packages);
                    colors.add(context.getResources().getColor(R.color.colorBlack));
                    values.add(false);
                }
                else
                {
                    labels.add("" + rec_ingr.getName());
                    colors.add(context.getResources().getColor(R.color.colorBlack));
                    values.add(false);
                }
            }
            else
            {
                //TODO eventually change standard unit measure
                labels.add(rec_ingr.getName() + " x" + rec_ingr.getAmount() + "kg");
                colors.add(context.getResources().getColor(R.color.colorErrorRed));
                values.add(false);
            }
        }

        shopping_list.updateShoppingList(labels, colors, values);
    }
    public int getShoppingListSize() { return shopping_list.getSize(); }
    public String getShoppingListLabel(int pos) { return shopping_list.getLabel(pos); }
    public boolean getShoppingListValue(int pos) { return shopping_list.getValue(pos); }
    public int getShoppingListColor(int pos) { return shopping_list.getColor(pos); }
    public String getShoppingListAdditionalText() { return shopping_list.getAdditionalText(); }
    public void setShoppingListValuesAndText(boolean[] vals, String text)
    {
        int result = 1;
        for(int i = 0; i < vals.length; i++)
        {
            result *= shopping_list.setValue(i, vals[i]);
        }
        result *= shopping_list.setAdditionalText(text);
        if(result == 0)
        {
            shopping_list.saveValues(context);
        }
    }
    //---   ---//
}
