package com.baldino.myapp003.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.baldino.myapp003.R;
import com.baldino.myapp003.singletons.Database;

public class ShoppingListActivity extends AppCompatActivity
{
    private LinearLayout container;

    Database D;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        container = findViewById(R.id.shopping_list_container);

        D = Database.getInstance();

        for(int i = 0; i < D.getShoppingListSize(); i++)
        {
            final int pos = i;
            CheckBox new_item = new CheckBox(this);
            new_item.setText(D.getShoppingListLabel(i));
            new_item.setTextColor(D.getShoppingListColor(i));
            new_item.setChecked(D.getShoppingListValue(i));
            new_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    D.setShoppingListValue(pos, isChecked);
                }
            });
            container.addView(new_item);
        }

        EditText additional_text = findViewById(R.id.additonal_items);
        additional_text.setText(D.getShoppingListAdditionalText());
        additional_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s)
            {
                D.setShoppingListAdditionalText(s.toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
