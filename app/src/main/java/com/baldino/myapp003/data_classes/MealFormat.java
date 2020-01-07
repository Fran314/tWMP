package com.baldino.myapp003.data_classes;

import java.util.ArrayList;
import java.util.List;

import static com.baldino.myapp003.Util.normalizeString;

public class MealFormat
{
    private String name;
    private List<Integer> types, std_recs;

    public MealFormat(String name)
    {
        setName(name);

        types = new ArrayList<>();
        std_recs = new ArrayList<>();
    }

    public void addMeal(int type, int std_rec)
    {
        types.add(type);
        std_recs.add(std_rec);
    }

    public void removeMeal(int pos)
    {
        types.remove(pos);
        std_recs.remove(pos);
    }

    public void setMeal(int pos, int type, int std_rec)
    {
        types.set(pos, type);
        std_recs.set(pos, std_rec);
    }
    public void setType(int pos, int type)
    {
        types.set(pos, type);
    }
    public void setStd(int pos, int std_rec)
    {
        std_recs.set(pos, std_rec);
    }

    public int getDim() { return types.size(); }
    public int getType(int pos) { return types.get(pos); }
    public int getStd(int pos) { return std_recs.get(pos); }

    public String getName() { return name; }
    public void setName(String name) { this.name = normalizeString(name); }
}
