package com.baldino.myapp003.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.baldino.myapp003.R;
import com.baldino.myapp003.Util;

public class ExpandableRecipeListView extends LinearLayout {

    public LinearLayout header_bar;
    public TextView header_text;
    public ImageView image_arrow;
    public ImageButton button_add;
    public RecyclerView rv_recipes;

    public ExpandableRecipeListView(Context context)
    {
        super(context);
        initializeViews(context);
    }

    private void initializeViews(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_expandable_recipe_list, this);

        header_bar = this.findViewById(R.id.header_bar);
        header_text = this.findViewById(R.id.header_text);
        image_arrow = this.findViewById(R.id.image_arrow);
        button_add = this.findViewById(R.id.button_add_recipe);
        rv_recipes = this.findViewById(R.id.recyclerview_recipes);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
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
