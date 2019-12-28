package com.baldino.myapp003;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.baldino.myapp003.main_fragments.IngredientsFragment;
import com.baldino.myapp003.singletons.IngredientManagerSingleton;

public class MnrIngrListAdapter extends RecyclerView.Adapter<MnrIngrListAdapter.RecViewHolder>
{
    public IngredientsFragment ingredients_fragment;

    public MnrIngrListAdapter()
    {
    }

    @Override
    public RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_std_ingredient, parent, false);

        return new RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecViewHolder holder, int position)
    {
        IngredientManagerSingleton sIngredientManager = IngredientManagerSingleton.getInstance();
        final Ingredient ingredient = sIngredientManager.standard_ingredients.get(position);

        holder.bind(ingredient, position);

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IngredientManagerSingleton sIngredientManager = IngredientManagerSingleton.getInstance();
                        int curr_position = sIngredientManager.standard_ingredients.indexOf(ingredient);

                        if(curr_position == sIngredientManager.expandedVal)
                        {
                            sIngredientManager.expandedVal = -1;
                            notifyItemChanged(curr_position);
                        }
                        else
                        {
                            int last_position = sIngredientManager.expandedVal;
                            sIngredientManager.expandedVal = curr_position;
                            if(last_position != -1) notifyItemChanged(last_position);
                            notifyItemChanged(curr_position);
                        }
                    }
                }
        );
    }

    @Override
    public int getItemCount()
    {
        IngredientManagerSingleton sIngredientManager = IngredientManagerSingleton.getInstance();
        return sIngredientManager.standard_ingredients == null ? 0 : sIngredientManager.standard_ingredients.size();
    }

    public class RecViewHolder extends RecyclerView.ViewHolder
    {
        private TextView name;

        private TextView amount;
        private TextView price;
        private TextView ratio;

        private ImageButton edit;
        private ImageButton delete;
        private ImageButton copy;

        private View subItem;

        public RecViewHolder(View itemView)
        {
            super(itemView);

            name = itemView.findViewById(R.id.ingredient_name);

            amount = itemView.findViewById(R.id.ingredient_amount);
            price = itemView.findViewById(R.id.ingredient_price);
            ratio = itemView.findViewById(R.id.ingredient_ratio);

            edit = itemView.findViewById(R.id.button_edit_ingredient);
            copy = itemView.findViewById(R.id.button_copy_ingredient);
            delete = itemView.findViewById(R.id.button_delete_ingredient);

            subItem = itemView.findViewById(R.id.sub_item);
        }

        private void bind(Ingredient ingredient, int pos)
        {
            final IngredientManagerSingleton sIngredientManager = IngredientManagerSingleton.getInstance();

            subItem.setVisibility(pos == sIngredientManager.expandedVal ? View.VISIBLE : View.GONE);

            name.setText(ingredient.getName());

            amount.setText(String.format("%.2f", ingredient.getAmount()) + " " + ingredient.getUnit());
            price.setText(String.format("%.2f", ingredient.getPrice()) + " €");
            ratio.setText(String.format("%.2f", ingredient.getRatio()) + " €/" + ingredient.getUnit());

            edit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    editExpandedIngredient();
                }
            });
            copy.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    copyExpandedIngredient();
                }
            });
            delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    //TODO: change this text to something that makes more sense and is based on
                    // the device language
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            sIngredientManager.removeIngredient(sIngredientManager.expandedVal);
                            sIngredientManager.saveIngredients();
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

    public void editExpandedIngredient()
    {
        ingredients_fragment.editExpandedIngredient();
    }

    public void copyExpandedIngredient()
    {
        ingredients_fragment.copyExpandedIngredient();
    }
}