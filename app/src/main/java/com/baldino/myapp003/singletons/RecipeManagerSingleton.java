package com.baldino.myapp003.singletons;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.baldino.myapp003.RecipeType;
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

public class RecipeManagerSingleton {

    private static RecipeManagerSingleton singleton_instance = null;

    private Context context;

    public List<RecipeType> recipe_types;
    public List<String> type_names;
    public ArrayAdapter<String> type_names_adapter = null;

    private RecipeManagerSingleton()
    {
        recipe_types = new ArrayList<>();
        type_names = new ArrayList<>();
    }

    public static RecipeManagerSingleton getInstance()
    {
        if (singleton_instance == null)
            singleton_instance = new RecipeManagerSingleton();

        return singleton_instance;
    }

    public void setContext(Context context) { this.context = context; }

    public void loadTypeNames()
    {
        recipe_types = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        try
        {
            FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), Util.REC_TYPES_PATH));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, Util.STD_CHARSET));
            String line = null;

            while((line = reader.readLine()) != null)
            {
                if(line.length() > 0 && line.charAt(0) != '%')
                {
                    if(line.lastIndexOf(']') != -1) lines.add(line.substring(0, line.lastIndexOf(']') + 1));
                    else lines.add(line);
                }
            }
            fis.close();
        }
        catch (FileNotFoundException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        for(int i = 0; i < lines.size(); i++)
        {
            RecipeType new_recipe_type;
            String name = Util.getRecTypeName(lines.get(i));

            new_recipe_type = new RecipeType(name, context);
            //new_recipe_type.createFakeData();
            new_recipe_type.loadRecipes();
            recipe_types.add(new_recipe_type);
        }

        type_names = new ArrayList<>();
        //type_names.addMeal("-");
        for(RecipeType rec_type : recipe_types)
        {
            type_names.add(rec_type.getName());
        }

        type_names_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, type_names);
        type_names_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }
    public void saveTypeNames()
    {
        StringBuilder output_string = new StringBuilder();
        for(int i = 0; i < recipe_types.size(); i++)
        {
            output_string.append("[");
            output_string.append(recipe_types.get(i).getName());
            output_string.append("]\n");
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), Util.REC_TYPES_PATH));
            fos.write(output_string.toString().getBytes(Util.STD_CHARSET));
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
