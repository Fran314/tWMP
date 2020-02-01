package com.baldino.myapp003.singletons;

import android.content.Context;

import com.baldino.myapp003.R;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.data_classes.Ingredient;
import com.baldino.myapp003.data_classes.RecIngredient;
import com.baldino.myapp003.data_classes.Recipe;
import com.baldino.myapp003.data_classes.WeekData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListManager
{
    private List<String> labels = null;
    private List<Integer> colors = null;
    private List<Boolean> values = null;

    private String additional_text = null;

    public ShoppingListManager()
    {
        labels = new ArrayList<>();
        colors = new ArrayList<>();
        values = new ArrayList<>();

        additional_text = "";
    }


    public void updateShoppingList(List<String> labels, List<Integer> colors, List<Boolean> values)
    {
        this.labels = labels;
        this.colors = colors;
        this.values = values;
    }

    public void saveValues(Context context)
    {
        StringBuilder output_string = new StringBuilder();
        output_string.append("[").append(values.size()).append("]\n");
        for(int i = 0; i < values.size(); i++)
        {
            output_string.append("[");
            output_string.append(values.get(i));
            output_string.append("]\n");
        }
        output_string.append(additional_text);

        Util.saveFile(output_string, new File(context.getFilesDir(), Util.SL_VALUES_PATH));
    }
    public void loadValues(Context context)
    {
        List<String> lines = new ArrayList<>();

        try
        {
            FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), Util.SL_VALUES_PATH));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, Util.STD_CHARSET));
            String line;

            while((line = reader.readLine()) != null)
            {
                if(line.length() > 0 && line.charAt(0) != '%')
                {
                    if(line.lastIndexOf(']') != -1) lines.add(line.substring(0, line.lastIndexOf(']') + 1));
                    else lines.add(line);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if(lines.size() > 0)
        {
            int numb_values = Util.getIntFromLine(lines.get(0));

            for(int i = 0; i < numb_values && i < values.size() && i+1 < lines.size(); i++)
            {
                values.set(i, Util.getBooleanFromLine(lines.get(i+1)));
            }

            StringBuilder temp = new StringBuilder();
            for(int i = numb_values+1; i < lines.size(); i++)
            {
                temp.append(lines.get(i));
                if(i != lines.size() - 1) temp.append("\n");
            }

            additional_text = temp.toString();
        }
    }

    /*
    private void addItem(String name, float amount)
    {
        int index = binaryFindIndex(name);
        if(index == -1)
        {
            int pos = shopping_list.size();
            for(int i = 0; i < shopping_list.size(); i++)
            {
                if(Util.compareStrings(name, shopping_list.get(i).getName()) < 0)
                {
                    pos = i;
                    break;
                }
            }
            shopping_list.add(pos, new RecIngredient(name, amount));
        }
        else
        {
            shopping_list.get(index).setAmount(shopping_list.get(index).getAmount() + amount);
        }
    }

    private int binaryFindIndex(String name) { return binaryFindIndex(name, 0, shopping_list.size()-1); }
    private int binaryFindIndex(String name, int left, int right)
    {
        if(left  > right) return -1;

        int mid = left + ((right - left)/2);
        if(Util.compareStrings(name, shopping_list.get(mid).getName()) == 0) return mid;
        else if(Util.compareStrings(name, shopping_list.get(mid).getName()) < 0) return binaryFindIndex(name, left, mid-1);
        else return binaryFindIndex(name, mid+1, right);
    }
     */

    public int getSize()
    {
        return (labels == null ? 0 : labels.size());
    }
    public String getLabel(int pos)
    {
        if(pos < 0 || pos >= getSize()) return "ERR";
        else return labels.get(pos);
    }
    public boolean getValue(int pos)
    {
        if(pos < 0 || pos >= getSize()) return false;
        else return values.get(pos);
    }
    public int getColor(int pos)
    {
        if(pos < 0 || pos >= getSize()) return -1;
        else return colors.get(pos);
    }
    public int setValue(int pos, boolean new_val)
    {
        if(pos < 0 || pos >= getSize()) return -1;

        if(values.get(pos) != new_val)
        {
            values.set(pos, new_val);
            return 0;
        }
        else return -1;
    }
    public String getAdditionalText() { return (additional_text == null ? "ERR" : additional_text); }
    public int setAdditionalText(String new_additional_text)
    {
        if(additional_text.equals(new_additional_text)) return -1;

        additional_text = new_additional_text;
        return 0;
    }
}
