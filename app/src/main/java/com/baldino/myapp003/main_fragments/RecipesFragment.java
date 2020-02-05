package com.baldino.myapp003.main_fragments;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.baldino.myapp003.Util;
import com.baldino.myapp003.custom_views.EditRecipeTypeDialog;
import com.baldino.myapp003.custom_views.ExpandableRecipeListView;
import com.baldino.myapp003.RecipeCollection;
import com.baldino.myapp003.activities.EditRecipeActivity;
import com.baldino.myapp003.R;
import com.baldino.myapp003.singletons.Database;

import java.util.ArrayList;
import java.util.List;

public class RecipesFragment extends Fragment
{
    private Database D;

    private LinearLayout rv_container;
    private List<ExpandableRecipeListView> eLists;
    private int expanded_collection = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_recipes, container, false);

        D = Database.getInstance();

        rv_container = root.findViewById(R.id.container_recipe_types_recyclerview);
        ImageButton button_add = root.findViewById(R.id.button_add_collection);
        button_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                int pos = D.getCollectionsSize();

                RecipeCollection new_collection = new RecipeCollection(getContext().getResources().getString(R.string.standard_new_recipe_collection), pos);
                D.addCollection(new_collection);
                addRecipeType(pos);
                setButtons(pos);
            }
        });

        eLists = new ArrayList<>();

        for(int i = 0; i < D.getCollectionsSize(); i++)
        {
            addRecipeType(i);
            setButtons(i);
        }

        return root;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        updateUI();
    }

    private void addRecipeType(int pos)
    {
        ExpandableRecipeListView erl = new ExpandableRecipeListView(getContext());
        erl.header_text.setText(D.getNameOfCollection(pos));
        if(pos != expanded_collection) erl.rv_recipes.setVisibility(View.GONE);
        else erl.image_arrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up));

        erl.rv_recipes.setLayoutManager(new LinearLayoutManager(getContext()));
        D.setRecipeListAdapterFragment(this, pos);
        erl.rv_recipes.setAdapter(D.getRecipeListAdapterOfCollection(pos));

        eLists.add(erl);
        rv_container.addView(erl, pos);
    }
    public void updateList(int pos)
    {
        eLists.get(pos).header_text.setText(D.getNameOfCollection(pos));
        D.setRecipeListAdapterFragment(this, pos);
        eLists.get(pos).rv_recipes.setAdapter(D.getRecipeListAdapterOfCollection(pos));

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
                            intent.putExtra("Collection", pos);
                            intent.putExtra("Recipe_Position", -1);
                            intent.putExtra("Recipe_New", true);

                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        }
                        else if(item.getItemId() == R.id.item_edit_list)
                        {
                            EditRecipeTypeDialog ertd = new EditRecipeTypeDialog(getContext(), pos, fragment);
                            ertd.editable_recipes_name.setText(D.getNameOfCollection(pos));
                            ertd.file_name_output.setText(Util.nameToFileName(D.getNameOfCollection(pos)) + ".txt");
                            ertd.show();
                        }
                        else if(item.getItemId() == R.id.item_delete_list)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle(getContext().getResources().getString(R.string.dialog_title_delete_recipe_collection) + " \"" + D.getNameOfCollection(pos) + "\"");
                            builder.setMessage(getContext().getResources().getString(R.string.dialog_text_delete_recipe_collection));
                            builder.setPositiveButton(getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    removeRecipeType(pos);
                                    dialog.dismiss();
                                }
                            });

                            builder.setNegativeButton(getContext().getResources().getString(R.string.no), new DialogInterface.OnClickListener()
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
        for(int i = 0; i < D.getCollectionsSize(); i++)
        {
            updateList(i);
        }
    }

    private void removeRecipeType(int pos)
    {
        if(expanded_collection == pos) expanded_collection = -1;
        else if(expanded_collection > pos) expanded_collection--;
        rv_container.removeViewAt(pos);
        eLists.remove(pos);

        D.removeCollection(pos);

        for(int i = 0; i < eLists.size(); i++)
        {
            setButtons(i);
        }
    }

    private void expand(int pos)
    {
        if(expanded_collection == -1)
        {
            eLists.get(pos).expand();
            expanded_collection = pos;
        }
        else if(expanded_collection == pos)
        {
            eLists.get(pos).collapse();
            expanded_collection = -1;
        }
        else
        {
            eLists.get(expanded_collection).collapse();
            eLists.get(pos).expand();

            expanded_collection = pos;
        }
    }

    public void editExpandedRecipe()
    {
        Intent intent = new Intent(getActivity(), EditRecipeActivity.class);
        intent.putExtra("Collection", expanded_collection);
        intent.putExtra("Recipe_Position", D.getExpandedValueOfCollection(expanded_collection));
        intent.putExtra("Recipe_New", false);

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void copyExpandedRecipe()
    {
        Intent intent = new Intent(getActivity(), EditRecipeActivity.class);
        intent.putExtra("Collection", expanded_collection);
        intent.putExtra("Recipe_Position", D.getExpandedValueOfCollection(expanded_collection));
        intent.putExtra("Recipe_New", true);

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}