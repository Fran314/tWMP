package com.baldino.myapp003.singletons;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.baldino.myapp003.RecipeCollection;
import com.baldino.myapp003.RecipeListAdapter;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.data_classes.Recipe;
import com.baldino.myapp003.main_fragments.RecipesFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecipeManager
{
    private List<RecipeCollection> recipe_collections;
    private List<String> collection_names;
    private ArrayAdapter<String> collection_names_adapter = null;

    public RecipeManager()
    {
        recipe_collections = new ArrayList<>();
        collection_names = new ArrayList<>();
    }

    public void saveCollectionsList(Context context)
    {
        StringBuilder output_string = new StringBuilder();
        for(int i = 0; i < recipe_collections.size(); i++)
        {
            output_string.append("[");
            output_string.append(recipe_collections.get(i).getName());
            output_string.append("]\n");
        }

        Util.saveFile(output_string, new File(context.getFilesDir(), Util.REC_COLLECTIONS_PATH));
    }
    public void loadCollectionList(Context context)
    {
        recipe_collections = new ArrayList<>();
        List<String> lines = Util.loadFile(new File(context.getFilesDir(), Util.REC_COLLECTIONS_PATH));

        if(!"ERR".equals(lines.get(0)))
        {
            for(int i = 0; i < lines.size(); i++)
            {
                RecipeCollection new_collection;
                String name = Util.getStringFromLine(lines.get(i));

                new_collection = new RecipeCollection(name, i);

                recipe_collections.add(new_collection);
            }
        }

        collection_names = new ArrayList<>();
        for(RecipeCollection collection : recipe_collections)
        {
            collection_names.add(collection.getName());
        }

        collection_names_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, collection_names);
        collection_names_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }
    public void saveCollections(Context context)
    {
        for(int i = 0; i < recipe_collections.size(); i++)
            saveCollection(i, context);
    }
    public void loadCollections(Context context)
    {
        for(int i = 0; i < recipe_collections.size(); i++)
            loadCollection(i, context);
    }
    public void saveCollection(int collection, Context context)
    {
        recipe_collections.get(collection).saveRecipes(context);
    }
    public void loadCollection(int collection, Context context)
    {
        recipe_collections.get(collection).loadRecipes(context);
    }

    public int addCollection(RecipeCollection new_collection)
    {
        //TODO
        // ADD NAME TO COLLECTION_NAMES
        recipe_collections.add(new_collection);
        return recipe_collections.size()-1;
    }

    public int removeCollection(int pos)
    {
        if(pos < 0 || pos >= getCollectionsSize()) return -1;

        //TODO
        // REMOVE NAME FROM COLLECTION_NAMES
        recipe_collections.remove(pos);
        for(int i = 0; i < recipe_collections.size(); i++)
        {
            recipe_collections.get(i).resetIndex(i);
        }

        return 0;
    }

    public int getCollectionsSize() { return recipe_collections == null ? 0 : recipe_collections.size(); }

    public void updateCollectionName(String name, int pos)
    {
        recipe_collections.get(pos).setName(name);
        collection_names.set(pos, name);
    }

    public RecipeCollection getCollection(int collection)
    {
        if(collection < 0 || collection >= getCollectionsSize()) return null;
        else return recipe_collections.get(collection);
    }
    public Recipe getRecipeOfCollection(int recipe, int collection)
    {
        if(collection < 0 || collection >= getCollectionsSize()
                || recipe < 0 || recipe >= recipe_collections.get(collection).getSize()) return null;
        else return recipe_collections.get(collection).getRecipe(recipe);
    }
    public int findRecipeOfCollectionIndex(String name, int collection)
    {
        if(collection < 0 || collection >= getCollectionsSize()) return -1;
        else return recipe_collections.get(collection).binaryFindIndex(name);
    }
    public Recipe findRecipeOfCollection(String name, int collection)
    {
        if(collection < 0 || collection >= getCollectionsSize()) return null;
        else return recipe_collections.get(collection).binaryFind(name);
    }
    public int getSizeOfCollection(int collection)
    {
        if(collection < 0 || collection >= getCollectionsSize()) return 0;
        else return recipe_collections.get(collection).getSize();
    }
    public int removeRecipeOfCollection(int recipe, int collection)
    {
        if(collection < 0 || collection >= getCollectionsSize()) return -1;
        else return recipe_collections.get(collection).removeRecipe(recipe);
    }
    public int addRecipeOfCollection(Recipe new_recipe, int collection)
    {
        if(collection < 0 || collection >= getCollectionsSize()) return -1;
        else return recipe_collections.get(collection).addRecipe(new_recipe);
    }
    public String getNameOfCollection(int collection)
    {
        if(collection < 0 || collection >= getCollectionsSize()) return "ERR";
        else return recipe_collections.get(collection).getName();
    }
    public ArrayAdapter<String> getNamesAdapterOfCollection(int collection, Context curr_context)
    {
        if(collection < 0 || collection >= getCollectionsSize()) return null;
        else return recipe_collections.get(collection).getNamesAdapter(curr_context);
    }
    public String getNameOfRecipeOfCollection(int recipe, int collection)
    {
        if(collection < 0 || collection >= getCollectionsSize()
                || recipe < 0 || recipe >= recipe_collections.get(collection).getSize()) return "ERR";
        else return recipe_collections.get(collection).getRecipe(recipe).getName();
    }
    public ArrayAdapter<String> getCollectionNamesAdapter() { return collection_names_adapter; }
    public RecipeListAdapter getListAdapterOfCollection(int collection)
    {
        if(collection < 0 || collection >= getCollectionsSize()) return null;
        return recipe_collections.get(collection).getListAdapter();
    }
    public void setListAdapterFragment(RecipesFragment fragment, int collection)
    {
        if(collection < 0 || collection >= getCollectionsSize()) return;
        recipe_collections.get(collection).getListAdapter().recipes_fragment = fragment;
    }
    public int getExpandedValueOfCollection(int collection)
    {
        if(collection < 0 || collection >= getCollectionsSize()) return -1;
        return recipe_collections.get(collection).getListAdapter().expanded_value;
    }
}
