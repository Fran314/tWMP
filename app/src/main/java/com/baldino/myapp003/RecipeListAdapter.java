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
import com.baldino.myapp003.main_fragments.RecipesFragment;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecViewHolder>
{
    private RecipeType recipe_type;
    public int expanded_value = -1;

    public RecipesFragment recipes_fragment;

    public RecipeListAdapter(RecipeType recipe_type)
    {
        this.recipe_type = recipe_type;
    }

    @NonNull
    @Override
    public RecipeListAdapter.RecViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);

        return new RecipeListAdapter.RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeListAdapter.RecViewHolder holder, int position)
    {
        final Recipe recipe = recipe_type.getRecipe(position);

        holder.bind(recipe, position);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                int curr_position = recipe_type.binaryFindIndex(recipe.getName());

                if(curr_position == expanded_value)
                {
                    expanded_value = -1;
                    notifyItemChanged(curr_position);
                }
                else
                {
                    int last_position = expanded_value;
                    expanded_value = curr_position;
                    if(last_position != -1) notifyItemChanged(last_position);
                    notifyItemChanged(curr_position);
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return recipe_type.getSize();
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
            IngredientManagerSingleton sIngredientManager = IngredientManagerSingleton.getInstance();

            if(pos == expanded_value)
            {
                subItem.setVisibility(View.VISIBLE);
            }
            else
            {
                subItem.setVisibility(View.GONE);
            }

            name.setText(recipe.getName());

            rec_ingredients.removeAllViews();

            for(int i = 0; i < recipe.ingredients.size(); i++)
            {
                TableRow row = new TableRow(view.getContext());
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                boolean mnr_ingr = false;
                Ingredient curr_ingredient = sIngredientManager.binaryFindStdIngr(recipe.ingredients.get(i).getName());
                if(curr_ingredient == null)
                {
                    mnr_ingr = true;
                    curr_ingredient = sIngredientManager.binaryFindMnrIngr(recipe.ingredients.get(i).getName());
                }

                TextView name_in_row = new TextView(view.getContext());
                name_in_row.setText(recipe.ingredients.get(i).getName());
                if(curr_ingredient == null) name_in_row.setTextColor(view.getResources().getColor(R.color.colorErrorRed));
                name_in_row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                row.addView(name_in_row);

                if(curr_ingredient == null || !mnr_ingr)
                {
                    TextView amount_in_row = new TextView(view.getContext());
                    amount_in_row.setText(Float.toString(recipe.ingredients.get(i).getAmount()));
                    if(curr_ingredient == null) amount_in_row.setTextColor(view.getResources().getColor(R.color.colorErrorRed));
                    amount_in_row.setGravity(Gravity.RIGHT);
                    amount_in_row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                    row.addView(amount_in_row);
                }

                if(curr_ingredient != null && !mnr_ingr)
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
                    //TODO: translate text
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            recipe_type.removeRecipe(expanded_value);
                            recipe_type.saveRecipes();
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
