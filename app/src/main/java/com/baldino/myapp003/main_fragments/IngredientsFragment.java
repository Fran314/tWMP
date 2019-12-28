package com.baldino.myapp003.main_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baldino.myapp003.activities.EditIngredientActivity;
import com.baldino.myapp003.singletons.IngredientManagerSingleton;
import com.baldino.myapp003.R;

public class IngredientsFragment extends Fragment
{
    private IngredientManagerSingleton sIngredientManager;

    private ImageButton button_add_ingredient;

    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_ingredients, container, false);

        sIngredientManager = IngredientManagerSingleton.getInstance();
        sIngredientManager.standard_ingr_list_adapter.ingredients_fragment = this;

        RecyclerView recycler_view = root.findViewById(R.id.recyclerview_ingredients);
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_view.setAdapter(sIngredientManager.standard_ingr_list_adapter);

        button_add_ingredient = root.findViewById(R.id.button_add_ingredient);
        button_add_ingredient.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditIngredientActivity.class);
                intent.putExtra("Ingredient_Position", -1);
                intent.putExtra("Ingredient_New", true);

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        return root;
    }

    public void editExpandedIngredient()
    {
        Intent intent = new Intent(getActivity(), EditIngredientActivity.class);

        intent.putExtra("Ingredient_Position", sIngredientManager.expandedVal);
        intent.putExtra("Ingredient_New", false);

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void copyExpandedIngredient()
    {
        Intent intent = new Intent(getActivity(), EditIngredientActivity.class);

        intent.putExtra("Ingredient_Position", sIngredientManager.expandedVal);
        intent.putExtra("Ingredient_New", true);

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}