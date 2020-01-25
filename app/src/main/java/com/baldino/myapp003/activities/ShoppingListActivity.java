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

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity
{
    Database D;
    List<CheckBox> boxes;
    EditText additional_text;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        LinearLayout container = findViewById(R.id.shopping_list_container);

        D = Database.getInstance();

        boxes = new ArrayList<>();
        for(int i = 0; i < D.getShoppingListSize(); i++)
        {
            final int pos = i;
            CheckBox new_item = new CheckBox(this);
            new_item.setText(D.getShoppingListLabel(i));
            new_item.setTextColor(D.getShoppingListColor(i));
            new_item.setChecked(D.getShoppingListValue(i));
            container.addView(new_item);
            boxes.add(new_item);
        }

        additional_text = findViewById(R.id.additonal_items);
        additional_text.setText(D.getShoppingListAdditionalText());
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean[] vals = new boolean[boxes.size()];
        for(int i = 0; i < boxes.size(); i++) vals[i] = boxes.get(i).isChecked();

        D.setShoppingListValuesAndText(vals, additional_text.getText().toString());
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
