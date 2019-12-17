package com.baldino.myapp003.ui.recipes;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baldino.myapp003.RecipeType;
import com.baldino.myapp003.activities.EditRecipeActivity;
import com.baldino.myapp003.R;
import com.baldino.myapp003.singletons.IngredientManagerSingleton;
import com.baldino.myapp003.singletons.RecipeManagerSingleton;
import com.baldino.myapp003.ui.ingredients.IngredientsFragment;

import java.util.ArrayList;
import java.util.List;

public class RecipesFragment extends Fragment {

    private RecipesViewModel recipesViewModel;

    private RecipeManagerSingleton sRecipeManager;

    private ImageButton button_add_fc, button_add_sc, button_add_sd;

    LinearLayout rv_container;
    private List<LinearLayout> headers;
    private List<RecyclerView> lists;
    private int expanded_value= -1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recipesViewModel =
                ViewModelProviders.of(this).get(RecipesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recipes, container, false);

        sRecipeManager = RecipeManagerSingleton.getInstance();

        rv_container = root.findViewById(R.id.container_recipe_types_recyclerview);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();


        headers = new ArrayList<>();
        lists = new ArrayList<>();

        for(int i = 0; i < sRecipeManager.recipe_types.size(); i++)
        {
            final int curr_pos = i;
            RecipeType rec_type = sRecipeManager.recipe_types.get(i);
            LinearLayout rec_container = new LinearLayout(getContext());
            rec_container.setOrientation(LinearLayout.VERTICAL);

            LinearLayout header = new LinearLayout(getContext());
            header.setOrientation(LinearLayout.HORIZONTAL);
            header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

            TextView textView_name = new TextView(getContext());
            textView_name.setTypeface(textView_name.getTypeface(), Typeface.BOLD);
            textView_name.setTextColor(Color.parseColor("#FFFFFF"));
            textView_name.setText(rec_type.getName());
            LinearLayout.LayoutParams name_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            name_params.gravity = Gravity.CENTER_VERTICAL;
            textView_name.setLayoutParams(name_params);

            View filler_view = new View(getContext());
            LinearLayout.LayoutParams filler_params = new LinearLayout.LayoutParams(0, 0);
            filler_params.weight = 1f;
            filler_view.setLayoutParams(filler_params);


            ImageButton button_delete = new ImageButton(getContext());
            button_delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_button_add_32dp));
            button_delete.setBackgroundColor(Color.TRANSPARENT);
            LinearLayout.LayoutParams button_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            button_params.gravity = Gravity.CENTER_VERTICAL;
            button_delete.setLayoutParams(button_params);

            button_delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                }
            });

            header.addView(textView_name);
            header.addView(filler_view);
            header.addView(button_delete);

            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expand(curr_pos);
                }
            });

            RecyclerView rv_recipes = new RecyclerView(getContext());
            rv_recipes.setLayoutManager(new LinearLayoutManager(getContext()));
            rec_type.getListAdapter().recipes_fragment = this;
            rv_recipes.setAdapter(rec_type.getListAdapter());

            headers.add(header);
            lists.add(rv_recipes);

            rv_recipes.setVisibility(View.GONE);

            rec_container.addView(header);
            rec_container.addView(rv_recipes);

            rv_container.addView(rec_container);
        }
    }

    private void expand(int pos)
    {
        if(expanded_value == -1)
        {
            lists.get(pos).setVisibility(View.VISIBLE);
            expanded_value = pos;
        }
        else if(expanded_value == pos)
        {
            lists.get(expanded_value).setVisibility(View.GONE);
            expanded_value = -1;
        }
        else
        {
            lists.get(pos).setVisibility(View.VISIBLE);
            lists.get(expanded_value).setVisibility(View.GONE);
            expanded_value = pos;
        }
    }

    public void editExpandedRecipe()
    {
        Intent intent = new Intent(getActivity(), EditRecipeActivity.class);
        intent.putExtra("Recipe_Type", sRecipeManager.expanded_type);
        intent.putExtra("Recipe_Position", sRecipeManager.expanded_value);
        intent.putExtra("Recipe_New", false);

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void copyExpandedRecipe()
    {
        Intent intent = new Intent(getActivity(), EditRecipeActivity.class);
        intent.putExtra("Recipe_Type", sRecipeManager.expanded_type);
        intent.putExtra("Recipe_Position", sRecipeManager.expanded_value);
        intent.putExtra("Recipe_New", true);

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}