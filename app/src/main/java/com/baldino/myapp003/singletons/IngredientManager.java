package com.baldino.myapp003.singletons;

import android.content.Context;
import android.util.Log;

import com.baldino.myapp003.data_classes.Ingredient;
import com.baldino.myapp003.IngredientListAdapter;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.main_fragments.IngredientsFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IngredientManager
{
    private String path;

    private List<Ingredient> ingredients;
    private IngredientListAdapter ingr_list_adapter;

    public IngredientManager(boolean is_standard)
    {
        if(is_standard) path = Util.STANDARD_INGR_PATH;
        else path = Util.MINOR_INGR_PATH;

        ingredients = new ArrayList<>();
        ingr_list_adapter = new IngredientListAdapter(is_standard);
    }

    public void saveIngr(Context context)
    {
        StringBuilder output_string = new StringBuilder();
        for(int i = 0; i < ingredients.size(); i++)
        {
            output_string.append('[');
            output_string.append(ingredients.get(i).getName());
            output_string.append("][");
            output_string.append(ingredients.get(i).getAmount());
            output_string.append("][");
            output_string.append(ingredients.get(i).getPrice());
            output_string.append("]\n");
        }

        //TODO change file path
        Util.saveFile(output_string, new File(context.getFilesDir(), path));
    }
    public void loadIngr(Context context)
    {
        ingredients = new ArrayList<>();
        //TODO change file path
        List<String> lines = Util.loadFile(new File(context.getFilesDir(), path));

        if(!"ERR".equals(lines.get(0)))
        {
            for(int i = 0; i < lines.size(); i++)
            {
                addIngr(Util.getIngredient(lines.get(i)));
            }
        }
    }

    public int addIngr(Ingredient ingredient)
    {
        boolean exists = false, found_place = false;
        int pos = ingredients.size();
        for(int i = 0; i < ingredients.size() && !exists && !found_place; i++)
        {
            if(Ingredient.areEqual(ingredient, ingredients.get(i)))
                exists = true;
            else if(Ingredient.alphabFirst(ingredient, ingredients.get(i)))
            {
                found_place = true;
                pos = i;
            }
        }
        if(!exists)
        {
            //TODO maybe I need to update the list_adapter item pool?
            ingredients.add(pos, ingredient);
            ingr_list_adapter.notifyItemInserted(pos);
            return 0;
        }
        else return -1;
    }
    public int removeIngr(int pos)
    {
        if(pos < 0 ||pos >= ingredients.size()) return -1;
        ingredients.remove(pos);
        ingr_list_adapter.notifyItemRemoved(pos);

        int last_expanded = ingr_list_adapter.expanded_val;
        ingr_list_adapter.expanded_val = -1;
        if(last_expanded != -1) ingr_list_adapter.notifyItemChanged(last_expanded);

        return 0;
    }

    public Ingredient binaryFindIngr(String name) { return binaryFindIngr(name, 0, ingredients.size()-1); }
    public Ingredient binaryFindIngr(String name, int left, int right)
    {
        if(left  > right) return null;

        int mid = left + ((right - left)/2);
        if(Util.compareStrings(name, ingredients.get(mid).getName()) == 0) return ingredients.get(mid);
        else if(Util.compareStrings(name, ingredients.get(mid).getName()) < 0) return binaryFindIngr(name, left, mid-1);
        else return binaryFindIngr(name, mid+1, right);
    }
    public int binaryFindIndex(String name) { return binaryFindIndex(name, 0, ingredients.size()-1); }
    public int binaryFindIndex(String name, int left, int right)
    {
        if(left  > right) return -1;

        int mid = left + ((right - left)/2);
        if(Util.compareStrings(name, ingredients.get(mid).getName()) == 0) return mid;
        else if(Util.compareStrings(name, ingredients.get(mid).getName()) < 0) return binaryFindIndex(name, left, mid-1);
        else return binaryFindIndex(name, mid+1, right);
    }

    public Ingredient getIngr(int pos)
    {
        if(pos < 0 || pos >= ingredients.size()) return null;
        return ingredients.get(pos);
    }
    public int getSize() { return (ingredients == null ? 0 : ingredients.size()); }

    public void setFragment(IngredientsFragment fragment) { ingr_list_adapter.ingredients_fragment = fragment; }
    public IngredientListAdapter getAdapter() { return ingr_list_adapter; }
}
