package com.baldino.myapp003.ui.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baldino.myapp003.custom_views.ExpandableRecipeListView;
import com.baldino.myapp003.RecipeType;
import com.baldino.myapp003.activities.EditRecipeActivity;
import com.baldino.myapp003.R;
import com.baldino.myapp003.singletons.RecipeManagerSingleton;

import java.util.ArrayList;
import java.util.List;

public class RecipesFragment extends Fragment {

    private RecipesViewModel recipesViewModel;

    private RecipeManagerSingleton sRecipeManager;

    private ImageButton button_add_fc, button_add_sc, button_add_sd;

    LinearLayout rv_container;
    private List<LinearLayout> headers;
    private List<RecyclerView> lists;
    private List<ExpandableRecipeListView> eLists;
    private int expanded_value = 0;

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

        rv_container.removeAllViews();

        headers = new ArrayList<>();
        lists = new ArrayList<>();

        eLists = new ArrayList<>();

        for(int i = 0; i < sRecipeManager.recipe_types.size(); i++)
        {
            final int curr_pos = i;
            RecipeType rec_type = sRecipeManager.recipe_types.get(i);
            ExpandableRecipeListView erl = new ExpandableRecipeListView(getContext());
            erl.header_text.setText(rec_type.getName());
            if(i != expanded_value) erl.rv_recipes.setVisibility(View.GONE);
            else erl.image_arrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up));

            erl.header_bar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expand(curr_pos);
                }
            });

            erl.button_add.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                }
            });

            erl.rv_recipes.setLayoutManager(new LinearLayoutManager(getContext()));
            rec_type.getListAdapter().recipes_fragment = this;
            erl.rv_recipes.setAdapter(rec_type.getListAdapter());

            eLists.add(erl);
            rv_container.addView(erl);
        }
    }

    private void expand(int pos)
    {
        if(expanded_value == -1)
        {
            eLists.get(pos).expand();
            expanded_value = pos;
        }
        else if(expanded_value == pos)
        {
            eLists.get(pos).collapse();
            expanded_value = -1;
        }
        else
        {
            eLists.get(expanded_value).collapse();
            eLists.get(pos).expand();

            expanded_value = pos;
        }
    }

    public void editExpandedRecipe()
    {
        Intent intent = new Intent(getActivity(), EditRecipeActivity.class);
        intent.putExtra("Recipe_Type", expanded_value);
        intent.putExtra("Recipe_Position", sRecipeManager.recipe_types.get(expanded_value).getListAdapter().expanded_value);
        intent.putExtra("Recipe_New", false);

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void copyExpandedRecipe()
    {
        Intent intent = new Intent(getActivity(), EditRecipeActivity.class);
        intent.putExtra("Recipe_Type", expanded_value);
        intent.putExtra("Recipe_Position", sRecipeManager.recipe_types.get(expanded_value).getListAdapter().expanded_value);
        intent.putExtra("Recipe_New", true);

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}