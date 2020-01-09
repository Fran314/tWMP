package com.baldino.myapp003.data_classes;

import java.util.ArrayList;
import java.util.List;

import static com.baldino.myapp003.Util.normalizeString;

public class MealFormat
{
    private String name = null;
    private List<Integer> types = null, std_recs = null;

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

    public int removeMeal(int pos)
    {
        if(pos < 0 || pos >= getDim()) return -1;

        types.remove(pos);
        std_recs.remove(pos);
        return 0;
    }

    public int setMeal(int pos, int type, int std_rec)
    {
        if(pos < 0 || pos >= getDim()) return -1;

        types.set(pos, type);
        std_recs.set(pos, std_rec);
        return 0;
    }
    public int setType(int pos, int type)
    {
        if(pos < 0 || pos >= getDim()) return -1;

        types.set(pos, type);
        return 0;
    }
    public int setStd(int pos, int std_rec)
    {
        if(pos < 0 || pos >= getDim()) return -1;

        std_recs.set(pos, std_rec);
        return 0;
    }

    public int getDim(){ return types == null ? 0 : types.size(); }
    public int getType(int pos)
    {
        if(pos < 0 || pos >= getDim()) return -1;
        else return types.get(pos);
    }
    public int getStd(int pos)
    {
        if(pos < 0 || pos >= getDim()) return -1;
        else return std_recs.get(pos);
    }

    public String getName() { return name == null ? "ERR" : name; }
    public void setName(String name) { this.name = normalizeString(name); }
}
