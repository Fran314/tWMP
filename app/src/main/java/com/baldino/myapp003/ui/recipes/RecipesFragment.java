package com.baldino.myapp003.ui.recipes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baldino.myapp003.Util;
import com.baldino.myapp003.custom_views.EditRecipeTypeDialog;
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

    LinearLayout rv_container;
    private ImageButton button_add;
    private List<ExpandableRecipeListView> eLists;
    private int expanded_value = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recipesViewModel =
                ViewModelProviders.of(this).get(RecipesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recipes, container, false);

        sRecipeManager = RecipeManagerSingleton.getInstance();

        rv_container = root.findViewById(R.id.container_recipe_types_recyclerview);
        button_add = root.findViewById(R.id.button_add_recipe_type);
        button_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                int pos = sRecipeManager.recipe_types.size();

                //TODO: change New Meal to a string based on the device language
                RecipeType new_recipe_type = new RecipeType("New Recipe Type", getContext());
                sRecipeManager.recipe_types.add(new_recipe_type);
                addRecipeType(pos);
                setButtons(pos);
                sRecipeManager.saveTypeNames();
            }
        });

        eLists = new ArrayList<>();

        for(int i = 0; i < sRecipeManager.recipe_types.size(); i++)
        {
            addRecipeType(i);
            setButtons(i);
        }

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();
    }

    private void addRecipeType(int pos)
    {
        RecipeType rec_type = sRecipeManager.recipe_types.get(pos);
        ExpandableRecipeListView erl = new ExpandableRecipeListView(getContext());
        erl.header_text.setText(rec_type.getName());
        if(pos != expanded_value) erl.rv_recipes.setVisibility(View.GONE);
        else erl.image_arrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up));

        erl.rv_recipes.setLayoutManager(new LinearLayoutManager(getContext()));
        rec_type.getListAdapter().recipes_fragment = this;
        erl.rv_recipes.setAdapter(rec_type.getListAdapter());

        eLists.add(erl);
        rv_container.addView(erl, pos);
    }
    public void updateList(int pos)
    {
        RecipeType rec_type = sRecipeManager.recipe_types.get(pos);
        eLists.get(pos).header_text.setText(rec_type.getName());
        rec_type.getListAdapter().recipes_fragment = this;
        eLists.get(pos).rv_recipes.setAdapter(rec_type.getListAdapter());

    }
    private void setButtons(final int pos)
    {
        eLists.get(pos).header_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expand(pos);
            }
        });

        final RecipesFragment fragment = this;

        eLists.get(pos).button_menu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PopupMenu popup = new PopupMenu(getContext(), eLists.get(pos).button_menu);
                popup.getMenuInflater().inflate(R.menu.recipe_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.item_add_recipe)
                        {
                            Intent intent = new Intent(getActivity(), EditRecipeActivity.class);
                            intent.putExtra("Recipe_Type", pos);
                            intent.putExtra("Recipe_Position", -1);
                            intent.putExtra("Recipe_New", true);

                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        }
                        else if(item.getItemId() == R.id.item_edit_list)
                        {
                            EditRecipeTypeDialog ertd = new EditRecipeTypeDialog(getContext(), pos, fragment);
                            ertd.editable_recipes_name.setText(sRecipeManager.recipe_types.get(pos).getName());
                            ertd.file_name_output.setText(Util.nameToFileName(sRecipeManager.recipe_types.get(pos).getName()) + ".txt");
                            ertd.show();
                        }
                        else if(item.getItemId() == R.id.item_delete_list)
                        {
                            //TODO: change this text to something that makes more sense and is based on
                            // the device language
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Confirm");
                            builder.setMessage("Are you sure?");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    removeRecipeType(pos);
                                    dialog.dismiss();
                                }
                            });

                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    // Do nothing
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                        return true;
                    }
                });

                popup.show();
            }
        });
    }

    public void updateUI()
    {
        for(int i = 0; i < sRecipeManager.recipe_types.size(); i++)
        {
            updateList(i);
        }
    }

    private void removeRecipeType(int pos)
    {
        if(expanded_value == pos) expanded_value = -1;
        else if(expanded_value > pos) expanded_value--;
        rv_container.removeViewAt(pos);
        eLists.remove(pos);
        sRecipeManager.recipe_types.remove(pos);

        //TODO: elimina file
        sRecipeManager.saveTypeNames();

        for(int i = 0; i < eLists.size(); i++)
        {
            setButtons(i);
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