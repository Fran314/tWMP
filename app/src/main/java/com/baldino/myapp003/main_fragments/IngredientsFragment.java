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
import com.baldino.myapp003.singletons.Database;
import com.baldino.myapp003.R;

public class IngredientsFragment extends Fragment
{
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_ingredients, container, false);

        Database D = Database.getInstance();
        D.setStdIngrFragment(this);
        D.setMnrIngrFragment(this);

        RecyclerView std_ingr_rv = root.findViewById(R.id.recyclerview_ingredients);
        std_ingr_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        std_ingr_rv.setAdapter(D.getStdIngrAdapter());

        RecyclerView mnr_ingr_rv = root.findViewById(R.id.recyclerview_minor_ingredients);
        mnr_ingr_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mnr_ingr_rv.setAdapter(D.getMnrIngrAdapter());

        ImageButton button_add_std_ingr = root.findViewById(R.id.button_add_standard_ingredient);
        button_add_std_ingr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditIngredientActivity.class);
                intent.putExtra("Ingredient_Position", -1);
                intent.putExtra("Ingredient_New", true);
                intent.putExtra("Is_Standard", true);

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        ImageButton button_add_mnr_ingr = root.findViewById(R.id.button_add_minor_ingredient);
        button_add_mnr_ingr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditIngredientActivity.class);
                intent.putExtra("Ingredient_Position", -1);
                intent.putExtra("Ingredient_New", true);
                intent.putExtra("Is_Standard", false);

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        return root;
    }

    public void editStdIngr(int pos)
    {
        Intent intent = new Intent(getActivity(), EditIngredientActivity.class);

        intent.putExtra("Ingredient_Position", pos);
        intent.putExtra("Ingredient_New", false);
        intent.putExtra("Is_Standard", true);

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
    public void copyStdIngr(int pos)
    {
        Intent intent = new Intent(getActivity(), EditIngredientActivity.class);

        intent.putExtra("Ingredient_Position", pos);
        intent.putExtra("Ingredient_New", true);
        intent.putExtra("Is_Standard", true);

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void editMnrIngr(int pos)
    {
        Intent intent = new Intent(getActivity(), EditIngredientActivity.class);

        intent.putExtra("Ingredient_Position", pos);
        intent.putExtra("Ingredient_New", false);
        intent.putExtra("Is_Standard", false);

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
    public void copyMnrIngr(int pos)
    {
        Intent intent = new Intent(getActivity(), EditIngredientActivity.class);

        intent.putExtra("Ingredient_Position", pos);
        intent.putExtra("Ingredient_New", true);
        intent.putExtra("Is_Standard", false);

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}