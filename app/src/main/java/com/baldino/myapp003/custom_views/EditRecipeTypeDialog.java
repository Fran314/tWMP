package com.baldino.myapp003.custom_views;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.baldino.myapp003.R;

public class EditRecipeTypeDialog extends Dialog implements View.OnClickListener
{
    public EditText editable_recipes_name;
    public TextView file_name_output;

    public EditRecipeTypeDialog(@NonNull Context context)
    {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_recipe_type);

        editable_recipes_name = this.findViewById(R.id.editable_recipe_type_name);
        file_name_output = this.findViewById(R.id.text_file_name_output);
    }

    @Override
    public void onClick(View view)
    {

    }
}
