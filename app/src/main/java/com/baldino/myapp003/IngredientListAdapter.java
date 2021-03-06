package com.baldino.myapp003;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.baldino.myapp003.data_classes.Ingredient;
import com.baldino.myapp003.singletons.Database;
import com.baldino.myapp003.main_fragments.IngredientsFragment;

public class IngredientListAdapter extends RecyclerView.Adapter<IngredientListAdapter.RecViewHolder>
{
    public IngredientsFragment ingredients_fragment;

    public int expanded_val = -1;
    public boolean is_standard = true;

    public IngredientListAdapter(boolean is_standard)
    {
        this.is_standard = is_standard;
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
        Database D = Database.getInstance();

        final Ingredient ingredient;
        if(is_standard) ingredient = D.getStdIngr(position);
        else ingredient = D.getMnrIngr(position);

        holder.bind(ingredient, position);

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Database D = Database.getInstance();
                        int curr_position;
                        if(is_standard) curr_position = D.findStdIngrIndex(ingredient.getName());
                        else curr_position = D.findMnrIngrIndex(ingredient.getName());

                        if(curr_position == expanded_val)
                        {
                            expanded_val = -1;
                            notifyItemChanged(curr_position);
                        }
                        else
                        {
                            int last_position = expanded_val;
                            expanded_val = curr_position;
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
        Database D = Database.getInstance();
        if(is_standard) return D.getStdIngrSize();
        else return D.getMnrIngrSize();
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
            final Database D = Database.getInstance();

            subItem.setVisibility(pos == expanded_val ? View.VISIBLE : View.GONE);

            name.setText(ingredient.getName());

            //TODO eventually change standard unit measure
            amount.setText(String.format("%.2f", ingredient.getAmount()) + " kg");
            price.setText(String.format("%.2f", ingredient.getPrice()) + " " + D.getCurrency());
            ratio.setText(String.format("%.2f", ingredient.getRatio()) + " "  + D.getCurrency() + "/kg");

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle(view.getContext().getResources().getString(R.string.dialog_title_delete_ingredient));
                    builder.setMessage(view.getContext().getResources().getString(R.string.dialog_text_delete_ingredient));
                    builder.setPositiveButton(view.getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if(is_standard)
                            {
                                D.removeStdIngr(expanded_val);
                            }
                            else
                            {
                                D.removeMnrIngr(expanded_val);
                            }
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton(view.getContext().getResources().getString(R.string.no), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
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
        if(is_standard) ingredients_fragment.editStdIngr(expanded_val);
        else ingredients_fragment.editMnrIngr(expanded_val);
    }

    public void copyExpandedIngredient()
    {
        if(is_standard) ingredients_fragment.copyStdIngr(expanded_val);
        else ingredients_fragment.copyMnrIngr(expanded_val);
    }
}