package com.baldino.myapp003.singletons;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.baldino.myapp003.RecipeCollection;
import com.baldino.myapp003.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class RecipeManager
{
    private List<RecipeCollection> recipe_types;
    private List<String> type_names;
    public ArrayAdapter<String> type_names_adapter = null;

    public RecipeManager()
    {
        recipe_types = new ArrayList<>();
        type_names = new ArrayList<>();
    }

    public void saveTypeNames(Context context)
    {
        StringBuilder output_string = new StringBuilder();
        for(int i = 0; i < recipe_types.size(); i++)
        {
            output_string.append("[");
            output_string.append(recipe_types.get(i).getName());
            output_string.append("]\n");
        }

        Util.saveFile(output_string, new File(context.getFilesDir(), Util.REC_TYPES_PATH));
    }
    public void loadTypeNames(Context context)
    {
        recipe_types = new ArrayList<>();
        List<String> lines = Util.loadFile(new File(context.getFilesDir(), Util.REC_TYPES_PATH));

        if(!"ERR".equals(lines.get(0)))
        {
            for(int i = 0; i < lines.size(); i++)
            {
                RecipeCollection new_recipe_type;
                String name = Util.getStringFromLine(lines.get(i));

                new_recipe_type = new RecipeCollection(name, i, context);

                new_recipe_type.loadRecipes();
                recipe_types.add(new_recipe_type);
            }
        }

        type_names = new ArrayList<>();
        for(RecipeCollection rec_type : recipe_types)
        {
            type_names.add(rec_type.getName());
        }

        type_names_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, type_names);
        type_names_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public RecipeCollection getType(int pos)
    {
        return recipe_types.get(pos);
    }

    public int addRecType(RecipeCollection new_rec_type)
    {
        recipe_types.add(new_rec_type);
        return 0;
    }

    public int removeRecType(int pos)
    {
        //File folder = new File(context.getFilesDir(), Util.TYPES_FOLDER);
        //folder.mkdirs();
        //File to_delete = new File(folder, Util.nameToFileName(recipe_types.get(pos).getName()) + ".txt");
        //if(to_delete.exists()) to_delete.delete();
        //TODO also delete file before calling this method
        if(pos < 0) return -1;
        else if(pos >= recipe_types.size()) return -2;

        recipe_types.remove(pos);
        for(int i = 0; i < recipe_types.size(); i++)
        {
            recipe_types.get(i).resetIndex(i);
        }

        return 0;
    }

    public int typesSize()
    {
        return recipe_types.size();
    }

    public void changeName(String name, int pos)
    {
        recipe_types.get(pos).setName(name);
        type_names.set(pos, name);
    }
}
