package com.baldino.myapp003.data_classes;

import static com.baldino.myapp003.Util.normalizeString;

public class RecIngredient
{
    private String name;
    private float amount;

    public RecIngredient(String name, float amount)
    {
        setName(name);
        setAmount(amount);
    }

    public String getName() { return name; }
    public float getAmount() { return amount; }

    public void setName(String name) { this.name = normalizeString(name); }
    public void setAmount(float amount) { this.amount = amount; }
}
