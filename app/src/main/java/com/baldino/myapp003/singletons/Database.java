package com.baldino.myapp003.singletons;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.baldino.myapp003.IngredientListAdapter;
import com.baldino.myapp003.RecipeCollection;
import com.baldino.myapp003.RecipeListAdapter;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.data_classes.Ingredient;
import com.baldino.myapp003.data_classes.Recipe;
import com.baldino.myapp003.main_fragments.IngredientsFragment;
import com.baldino.myapp003.main_fragments.RecipesFragment;

import java.io.File;

public class Database
{
    private static Database instance = null;
    private Context context = null;

    private IngredientManager std_ingredients, mnr_ingredients;
    private RecipeManager recipes;
    private WeekManager week_manager;

    private Database()
    {
        std_ingredients = new IngredientManager(true);
        mnr_ingredients = new IngredientManager(false);

        recipes = new RecipeManager();

        week_manager = new WeekManager();
    }

    public synchronized static Database getInstance()
    {
        if(instance == null) {
            instance = new Database();
        }

        return instance;
    }

    public void setContext(Context context)
    {
        this.context = context.getApplicationContext();
    }

    public void loadAll()
    {
        if(context != null)
        {
            std_ingredients.loadIngr(context);
            mnr_ingredients.loadIngr(context);

            recipes.loadCollectionList(context);
            recipes.loadCollections(context);

            week_manager.loadWeeks(context);
            week_manager.loadDailyMeals(context);
            week_manager.loadData(context);
        }
    }

    //---   STD & MNR INGREDIENTS METHODS   ---//
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
        if(result == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            std_ingredients.saveIngr(context);
        }
    }
    public void removeMnrIngr(int pos)
    {
        int result = mnr_ingredients.removeIngr(pos);
        if(result == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            mnr_ingredients.saveIngr(context);
        }
    }
    public void updateStdIngr(int pos, Ingredient new_ingredient)
    {
        int remove = std_ingredients.removeIngr(pos);
        int add = std_ingredients.addIngr(new_ingredient);
        if(remove == 0 || add == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            std_ingredients.saveIngr(context);
        }
    }
    public void updateMnrIngr(int pos, Ingredient new_ingredient)
    {
        int remove = mnr_ingredients.removeIngr(pos);
        int add = mnr_ingredients.addIngr(new_ingredient);
        if(remove == 0 || add == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            mnr_ingredients.saveIngr(context);
        }
    }
    public void addStdIngr(Ingredient new_ingredient)
    {
        int result = std_ingredients.addIngr(new_ingredient);
        if(result == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            std_ingredients.saveIngr(context);
        }
    }
    public void addMnrIngr(Ingredient new_ingredient)
    {
        int result = mnr_ingredients.addIngr(new_ingredient);
        if(result == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            mnr_ingredients.saveIngr(context);
        }
    }
    //---   ---//

    //---   RECIPES METHODS ---//
    public Recipe getRecipeOfCollection(int recipe, int collection_index){ return recipes.getRecipeOfCollection(recipe, collection_index); }
    public int findRecipeOfCollectionIndex(String name, int collection) { return recipes.findRecipeOfCollectionIndex(name, collection); }
    public Recipe findRecipeOfCollection(String name, int collection) { return recipes.findRecipeOfCollection(name, collection); }
    public int getSizeOfCollection(int collection) { return recipes.getSizeOfCollection(collection); }
    public void removeRecipeOfCollection(int recipe, int collection)
    {
        int result = recipes.removeRecipeOfCollection(recipe, collection);
        if(result == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            recipes.saveCollection(collection, context);
        }
    }
    public void updateRecipeOfCollection(int recipe, int collection, Recipe new_recipe)
    {
        int remove = recipes.removeRecipeOfCollection(recipe, collection);
        int add = recipes.addRecipeOfCollection(new_recipe, collection);
        if(remove == 0 || add == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            recipes.saveCollectionsList(context);
        }
    }
    public void addRecipeOfCollection(Recipe new_recipe, int collection)
    {
        int result = recipes.addRecipeOfCollection(new_recipe, collection);
        if(result == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            recipes.saveCollectionsList(context);
        }
    }
    public String getNameOfCollection(int collection) { return recipes.getNameOfCollection(collection); }
    public ArrayAdapter<String> getNamesAdapterOfCollection(int collection, Context curr_context) { return recipes.getNamesAdapterOfCollection(collection, curr_context); }
    public String getNameOfRecipeOfCollection(int recipe, int collection) { return recipes.getNameOfRecipeOfCollection(recipe, collection); }
    public ArrayAdapter<String> getCollectionNamesAdapter() { return recipes.getCollectionNamesAdapter(); }
    public RecipeListAdapter getRecipeListAdapterOfCollection(int collection) { return recipes.getListAdapterOfCollection(collection); }
    public int getCollectionsSize() { return recipes.getCollectionsSize(); }
    public void updateCollectionName(String name, int collection)
    {
        String last_name = getNameOfCollection(collection);

        recipes.updateCollectionName(name, collection);
        recipes.saveCollection(collection, context);
        recipes.saveCollectionsList(context);

        //TODO
        // UPDATE OTHER STUFF

        File folder = new File(context.getFilesDir(), Util.COLLECTIONS_FOLDER);
        folder.mkdirs();

        File to_delete = new File(folder, Util.nameToFileName(last_name) + ".txt");
        to_delete.delete();
    }
    public void addCollection(RecipeCollection new_collection)
    {
        recipes.addCollection(new_collection);
        recipes.saveCollectionsList(context);

        //TODO
        // UPDATE OTHER STUFF
    }
    public void removeCollection(int pos)
    {
        File folder = new File(context.getFilesDir(), Util.COLLECTIONS_FOLDER);
        folder.mkdirs();
        File to_delete = new File(folder, Util.nameToFileName(recipes.getNameOfCollection(pos)) + ".txt");
        if(to_delete.exists()) to_delete.delete();

        int result = recipes.removeCollection(pos);
        if(result == 0)
        {
            //TODO
            // UPDATE OTHER STUFF
            recipes.saveCollectionsList(context);
        }
    }
    public void setRecipeListAdapterFragment(RecipesFragment fragment, int collection) { recipes.setListAdapterFragment(fragment, collection); }
    public int getExpandedValueOfCollection(int collection) { return recipes.getExpandedValueOfCollection(collection); }
    //---   ---//
}
