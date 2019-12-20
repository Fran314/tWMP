package com.baldino.myapp003;

import static com.baldino.myapp003.Util.normalizeString;

public class Ingredient
{
    private String name;
    private String unit;
    private float amount = 0f;
    private float price = 0f;

    public Ingredient(String name, float amount, String unit, float price)
    {
        setName(name);
        setAmount(amount);
        setUnit(unit);
        setPrice(price);
    }

    public String getName() { return name; }
    public String getUnit() { return unit; }
    public float  getAmount() { return amount; }
    public float  getPrice() { return price; }
    public float getRatio() { return (price/amount); }

    public void setName(String name) { this.name = normalizeString(name); }
    public void setUnit(String unit) { this.unit = normalizeString(unit); }
    public void setAmount(float amount) { this.amount = amount; }
    public void setPrice(float price) { this.price = price; }


    public static boolean alphabFirst(Ingredient arg0, Ingredient arg1)
    {
        if(Util.compareStrings(arg0.getName(), arg1.getName())<0) //"a".compareTo("b") := -1
            return true;
        else return false;
    }

    public static boolean areEqual(Ingredient arg0, Ingredient arg1)
    {
        //if(arg0.getName().equals(arg1.getName()) &&
        //        arg0.getAmount() == arg1.getAmount() &&
        //        arg0.getUnit().equals(arg1.getUnit()) &&
        //        arg0.getPrice() == arg1.getPrice())
        if(Util.compareStrings(arg0.getName(), arg1.getName()) == 0)
            return true;
        else return false;
    }
}