package com.baldino.myapp003.custom_views;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.baldino.myapp003.R;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.singletons.RecipeManagerSingleton;
import com.baldino.myapp003.main_fragments.RecipesFragment;

import java.io.File;

public class EditRecipeTypeDialog extends Dialog
{
    private RecipeManagerSingleton sRecipeManager;
    public EditText editable_recipes_name;
    public TextView file_name_output;

    public ImageButton button_save;

    public int curr_pos;

    public EditRecipeTypeDialog(@NonNull final Context context, int pos, final RecipesFragment fragment)
    {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_recipe_type);

        sRecipeManager = RecipeManagerSingleton.getInstance();

        this.curr_pos = pos;

        editable_recipes_name = this.findViewById(R.id.editable_recipe_type_name);
        file_name_output = this.findViewById(R.id.text_file_name_output);

        editable_recipes_name.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable)
            {
                file_name_output.setText(Util.nameToFileName(editable.toString()) + ".txt");
            }
        });

        button_save = this.findViewById(R.id.button_save_recipe_type);
        button_save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                boolean sameFileName = false;
                for(int i = 0; i < sRecipeManager.typesSize() && !sameFileName; i++)
                {
                    if(i != curr_pos && Util.compareStrings(Util.nameToFileName(editable_recipes_name.getText().toString()), sRecipeManager.getType(i).getName()) == 0)
                        sameFileName = true;
                }

                if(!sameFileName)
                {
                    String last_name = sRecipeManager.getType(curr_pos).getName();
                    sRecipeManager.changeName(editable_recipes_name.getText().toString(), curr_pos);
                    sRecipeManager.getType(curr_pos).saveRecipes();
                    sRecipeManager.saveTypeNames();

                    File folder = new File(getContext().getFilesDir(), Util.TYPES_FOLDER);
                    folder.mkdirs();

                    File to_delete = new File(folder, Util.nameToFileName(last_name) + ".txt");
                    to_delete.delete();

                    fragment.updateList(curr_pos);
                    dismiss();
                }
                else
                {
                    Toast.makeText(getContext(), context.getResources().getString(R.string.toast_cant_save_type_name), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
