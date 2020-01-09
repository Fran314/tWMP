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

import com.baldino.myapp003.data_classes.Ingredient;
import com.baldino.myapp003.data_classes.Recipe;
import com.baldino.myapp003.singletons.Database;
import com.baldino.myapp003.main_fragments.RecipesFragment;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecViewHolder>
{
    public int collection = 0;
    public int expanded_value = -1;

    public RecipesFragment recipes_fragment;
    private Database D;

    public RecipeListAdapter(int collection)
    {
        this.collection = collection;
        D = Database.getInstance();
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
        final Recipe recipe = D.getRecipeOfCollection(position, collection);

        holder.bind(recipe, position);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                int curr_position = D.findRecipeOfCollectionIndex(recipe.getName(), collection);

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
    public int getItemCount() { return D.getSizeOfCollection(collection); }

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
            final Database D = Database.getInstance();

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
                Ingredient curr_ingredient = D.findStdIngr(recipe.ingredients.get(i).getName());
                if(curr_ingredient == null)
                {
                    mnr_ingr = true;
                    curr_ingredient = D.findMnrIngr(recipe.ingredients.get(i).getName());
                }

                TextView name_in_row = new TextView(view.getContext());
                name_in_row.setText(recipe.ingredients.get(i).getName());
                if(curr_ingredient == null) name_in_row.setTextColor(view.getResources().getColor(R.color.colorErrorRed));
                name_in_row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                row.addView(name_in_row);

                if(curr_ingredient == null || !mnr_ingr)
                {
                    //TODO change that kg to some variable stuff
                    TextView amount_in_row = new TextView(view.getContext());
                    amount_in_row.setText(Float.toString(recipe.ingredients.get(i).getAmount()) + " kg");
                    if(curr_ingredient == null) amount_in_row.setTextColor(view.getResources().getColor(R.color.colorErrorRed));
                    amount_in_row.setGravity(Gravity.RIGHT);
                    amount_in_row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                    row.addView(amount_in_row);
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
                    builder.setTitle(view.getContext().getResources().getString(R.string.dialog_title_delete_recipe));
                    builder.setMessage(view.getContext().getResources().getString(R.string.dialog_text_delete_recipe));
                    builder.setPositiveButton(view.getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            D.removeRecipeOfCollection(expanded_value, collection);

                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton(view.getContext().getResources().getString(R.string.no), new DialogInterface.OnClickListener()
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
