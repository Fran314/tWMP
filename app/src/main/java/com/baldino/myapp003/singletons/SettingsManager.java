package com.baldino.myapp003.singletons;

import android.content.Context;

import com.baldino.myapp003.Util;

import java.io.File;
import java.time.DayOfWeek;
import java.time.temporal.WeekFields;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class SettingsManager
{
    private String currency = "";
    private int first_day_of_week = 1;

    public void saveSettings(Context context)
    {
        StringBuilder output_string = new StringBuilder();
        output_string.append("[").append(Util.APP_VERSION).append("]\n");
        output_string.append("[").append(currency).append("]\n");
        output_string.append("[").append(first_day_of_week).append("]");

        Util.saveFile(output_string, new File(context.getFilesDir(), Util.SETTINGS_PATH));
    }
    public void loadSettings(Context context)
    {
        List<String> lines = Util.loadFile(new File(context.getFilesDir(), Util.SETTINGS_PATH));

        if(lines.size() >= 3)
        {
            currency = Util.getStringFromLine(lines.get(1));
            first_day_of_week = Util.getIntFromLine(lines.get(2));
        }
        else
        {
            //TODO
            // Add a warn if it couldn't load the settings properly
            Locale locale = Locale.getDefault();
            Currency std_currency = Currency.getInstance(locale);
            DayOfWeek std_first_day_of_week = WeekFields.of(locale).getFirstDayOfWeek();

            currency = std_currency.getSymbol();
            first_day_of_week = std_first_day_of_week.getValue();
        }
    }

    public String getCurrency() { return currency; }
    public int getFirstDayOfWeek() { return first_day_of_week; }
    public int setCurrency(String new_currency)
    {
        if(currency.equals(new_currency)) return -1;

        currency = new_currency;
        return 0;
    }
    public int setFirstDayOfWeek(int new_fdow)
    {
        if(first_day_of_week == new_fdow) return -1;

        first_day_of_week = new_fdow;
        return 0;
    }
}
