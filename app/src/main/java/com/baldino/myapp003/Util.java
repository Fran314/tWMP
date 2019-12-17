package com.baldino.myapp003;

public class Util {

    public static final int BYTES_IN_CHAR = 2;
    public static final int BYTES_IN_FLOAT = 4;

    public static final int INGR_NAME_LENGTH = 56;
    public static final int INGR_UNIT_LENGTH = 4;

    public static final int INGR_TOT_BYTES = 128;   // bytes from 0   to 111 (112 in total): name
                                                    // bytes from 112 to 119 (8   in total): unit
                                                    // bytes from 120 to 123 (4   in total): amount
                                                    // bytes from 124 to 127 (4   in total): price
    public static final int INGR_NAME_OFFSET = 0;
    public static final int INGR_UNIT_OFFSET = 112;
    public static final int INGR_AMOUNT_OFFSET = 120;
    public static final int INGR_PRICE_OFFSET = 124;

    public static final int INGR_B_NAME_SIZE = INGR_NAME_LENGTH *BYTES_IN_CHAR;
    public static final int INGR_B_UNIT_SIZE = INGR_UNIT_LENGTH *BYTES_IN_CHAR;
    public static final int INGR_B_AMOUNT_SIZE = BYTES_IN_FLOAT;
    public static final int INGR_B_PRICE_SIZE = BYTES_IN_FLOAT;

    public static final char NULL_CHAR = '\uFFFF';

    public static final String STD_CHARSET = "UTF-16";

    public static String getRecTypeName(String line)
    {
        int pos[] = isValidAndGetPos(line, 1);
        if(pos[0] == -1) return "ERROR_NAME";
        else return line.substring(pos[0]+1, pos[1]);
    }

    public static String getRecipeName(String line)
    {
        int pos[] = isValidAndGetPos(line, 1);
        if(pos[0] == -1) return "ERROR_NAME";
        else return line.substring(pos[0]+1, pos[1]);
    }

    public static int getRecipeIngredientsAmount(String line)
    {
        int pos[] = isValidAndGetPos(line, 1);
        int to_return = -1;
        if(pos[0] != -1)
        {
            try
            {
                to_return = Integer.parseInt(line.substring(pos[0]+1, pos[1]));
            }
            catch(NumberFormatException nfe)
            {
            }
        }
        return to_return;
    }

    public static RecIngredient getRecipeIngredient(String line)
    {
        String name = "ERROR_NAME";
        float amount = 0f;
        int pos[] = isValidAndGetPos(line, 2);
        if(pos[0] != -1)
        {
            name = line.substring(pos[0]+1, pos[2+0]);
            try
            {
                amount = stringToFloat(line.substring(pos[1]+1, pos[2+1]));
            }
            catch(NumberFormatException nfe)
            {

            }
        }

        return new RecIngredient(name, amount);
    }

    public static Ingredient getIngredient(String line)
    {
        String name = "ERROR_NAME";
        float amount = 0f;
        String unit = "ERR";
        float price = 0f;

        int pos[] = isValidAndGetPos(line, 4);
        if(pos[0] != -1)
        {
            name = line.substring(pos[0]+1, pos[4+0]);
            unit = line.substring(pos[2]+1, pos[4+2]);
            try
            {
                amount = stringToFloat(line.substring(pos[1]+1, pos[4+1]));
                price = stringToFloat(line.substring(pos[3]+1, pos[4+3]));
            }
            catch(NumberFormatException nfe)
            {
            }
        }

        return new Ingredient(name, amount, unit, price);
    }

    public static Day getDay(String line)
    {
        String lunch = "ERROR_LUNCH", dinner = "ERROR_DINNER", side_dinner = "ERROR_SIDE";

        int pos[] = isValidAndGetPos(line, 3);
        if(pos[0] != -1)
        {
            lunch = line.substring(pos[0]+1, pos[3+0]);
            dinner = line.substring(pos[1]+1, pos[3+1]);
            side_dinner = line.substring(pos[2]+1, pos[3+2]);
        }

        return new Day(lunch, dinner, side_dinner);
    }

    public static String getFileName(String line)
    {
        int pos[] = isValidAndGetPos(line, 1);
        if(pos[0] == -1) return "";

        return line.substring(pos[0]+1, pos[1]);
    }

    public static int[] isValidAndGetPos(String s, int n_par)
    {
        if(n_par < 1) return new int[] {-1};

        int to_return[] = new int[2*n_par];
        int counter_a = 0;
        int counter_b = 0;
        int counter_bal = 0;
        boolean error = false;

        for(int i = 0; i < s.length() && !error; i++)
        {
            if(s.charAt(i) == '[')
            {
                counter_a++;
                counter_bal++;
                if(counter_a <= n_par && counter_bal <= 1) to_return[counter_a-1] = i;
                else error = true;
            }
            else if(s.charAt(i) == ']')
            {
                counter_b++;
                counter_bal--;
                if(counter_b <= n_par && counter_bal >= 0) to_return[n_par + counter_b-1] = i;
                else error = true;
            }
        }

        if(counter_a != n_par || counter_b != n_par || counter_bal != 0) error = true;
        if(error) to_return[0] = -1;

        return to_return;
    }

    public static String normalizeString(String arg)
    {
        String lines[] = arg.split(System.getProperty("line.separator"));

        String to_return = lines[0];
        to_return.replace("[", "").replace("]", "");
        return to_return;
    }

    public static String nameToFileName(String name)
    {
        return name.toLowerCase().replaceAll("/[^A-Za-z0-9 ]/", "").replace(" ", "_");
    }

    public static Float stringToFloat(String arg)
    {
        Float to_return = 0f;
        try
        {
            to_return = Float.parseFloat(arg);
        }
        catch(NumberFormatException nfe)
        {
        }

        return to_return;
    }
}
