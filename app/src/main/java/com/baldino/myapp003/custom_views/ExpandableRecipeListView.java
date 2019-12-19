package com.baldino.myapp003.custom_views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.baldino.myapp003.R;
import com.baldino.myapp003.Util;

public class ExpandableRecipeListView extends LinearLayout {

    public LinearLayout header_bar;
    public TextView header_text;
    public ImageView image_arrow;
    public ImageButton button_menu;
    public RecyclerView rv_recipes;

    public ExpandableRecipeListView(Context context)
    {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_expandable_recipe_list, this);

        header_bar = this.findViewById(R.id.header_bar);
        header_text = this.findViewById(R.id.header_text);
        image_arrow = this.findViewById(R.id.image_arrow);
        button_menu = this.findViewById(R.id.button_menu);
        rv_recipes = this.findViewById(R.id.recyclerview_recipes);
    }

    public void expand()
    {
        image_arrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up));
        Util.expand(rv_recipes);
    }

    public void collapse()
    {
        image_arrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down));
        Util.collapse(rv_recipes);
    }
}
