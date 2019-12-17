package com.baldino.myapp003;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baldino.myapp003.singletons.IngredientManagerSingleton;
import com.baldino.myapp003.singletons.RecipeManagerSingleton;
import com.baldino.myapp003.ui.recipes.RecipesFragment;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecViewHolder>
{
    private int meal_type;

    public RecipesFragment recipes_fragment;

    public RecipeListAdapter(int meal_type)
    {
        this.meal_type = meal_type;
    }

    @NonNull
    @Override
    public RecipeListAdapter.RecViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);

        return new RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeListAdapter.RecViewHolder holder, int position)
    {
        RecipeManagerSingleton sRecipeManager = RecipeManagerSingleton.getInstance();
        final Recipe recipe = sRecipeManager.getRecipe(position, meal_type);

        holder.bind(recipe, position);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                RecipeManagerSingleton sRecipeManager = RecipeManagerSingleton.getInstance();
                int curr_position = sRecipeManager.binaryFindIndex(recipe, meal_type);

                if(sRecipeManager.expanded_type == meal_type && curr_position == sRecipeManager.expanded_value)
                {
                    sRecipeManager.expanded_value = -1;
                    sRecipeManager.notifyItemChanged(curr_position, meal_type);
                }
                else
                {
                    int last_type = sRecipeManager.expanded_type;
                    int last_position = sRecipeManager.expanded_value;
                    sRecipeManager.expanded_type = meal_type;
                    sRecipeManager.expanded_value = -1;
                    if(last_position != -1) sRecipeManager.notifyItemChanged(last_position, last_type);
                    sRecipeManager.expanded_value = curr_position;
                    sRecipeManager.notifyItemChanged(curr_position, meal_type);
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        RecipeManagerSingleton sRecipeManager = RecipeManagerSingleton.getInstance();
        return sRecipeManager.getSize(meal_type);
    }

    public class RecViewHolder extends RecyclerView.ViewHolder
    {
        private TextView name;
        private TableLayout rec_ingredients;

        private ImageButton delete, copy, edit;

        private View view;

        private View subItem;

        public RecViewHolder(@NonNull View itemView)
        {
            super(itemView);

            view = itemView;

            name = itemView.findViewById(R.id.recipe_name);
            rec_ingredients = itemView.findViewById(R.id.recipe_ingredients_table);

            subItem = itemView.findViewById(R.id.sub_item);

            delete = itemView.findViewById(R.id.button_delete_recipe);
            copy = itemView.findViewById(R.id.button_copy_recipe);
            edit = itemView.findViewById(R.id.button_edit_recipe);
        }

        private void bind(Recipe recipe, int pos)
        {
            final RecipeManagerSingleton sRecipeManager = RecipeManagerSingleton.getInstance();
            IngredientManagerSingleton sIngredientManager = IngredientManagerSingleton.getInstance();

            if(pos == sRecipeManager.expanded_value && meal_type == sRecipeManager.expanded_type)
                subItem.setVisibility(View.VISIBLE);
            else subItem.setVisibility(View.GONE);

            name.setText(recipe.getName());

            rec_ingredients.removeAllViews();

            for(int i = 0; i < recipe.ingredients.size(); i++)
            {
                TableRow row = new TableRow(view.getContext());
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                Ingredient curr_ingredient = sIngredientManager.binaryFind(recipe.ingredients.get(i).getName());

                TextView name_in_row = new TextView(view.getContext());
                name_in_row.setText(recipe.ingredients.get(i).getName());
                if(curr_ingredient == null) name_in_row.setTextColor(view.getResources().getColor(R.color.colorErrorRed));
                name_in_row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                TextView amount_in_row = new TextView(view.getContext());
                amount_in_row.setText(Float.toString(recipe.ingredients.get(i).getAmount()));
                if(curr_ingredient == null) amount_in_row.setTextColor(view.getResources().getColor(R.color.colorErrorRed));
                amount_in_row.setGravity(Gravity.RIGHT);
                amount_in_row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                row.addView(name_in_row);
                row.addView(amount_in_row);

                if(curr_ingredient != null)
                {
                    TextView unit_in_row = new TextView(view.getContext());
                    unit_in_row.setText(curr_ingredient.getUnit());
                    unit_in_row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    row.addView(unit_in_row);
                }


                rec_ingredients.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }

            edit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    editExpandedRecipe();
                }
            });
            copy.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    copyExpandedRecipe();
                }
            });
            delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            sRecipeManager.removeRecipe(sRecipeManager.expanded_value, sRecipeManager.expanded_type);
                            sRecipeManager.saveData(sRecipeManager.expanded_type);
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
            });
        }
    }

    public void editExpandedRecipe()
    {
        recipes_fragment.editExpandedRecipe();
    }

    public void copyExpandedRecipe()
    {
        recipes_fragment.copyExpandedRecipe();
    }
}
