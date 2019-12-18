package com.baldino.myapp003.ui.day_format;

import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.renderscript.Script;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.baldino.myapp003.R;
import com.baldino.myapp003.custom_views.EditableMealFormatView;
import com.baldino.myapp003.singletons.WeekManagerSingleton;

public class DayFormatFragment extends Fragment {

    private DayFormatViewModel dayFormatViewModel;

    private LinearLayout days_container;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        dayFormatViewModel = ViewModelProviders.of(this).get(DayFormatViewModel.class);
        View root = inflater.inflate(R.layout.fragment_day_format, container, false);

        days_container = root.findViewById(R.id.container);

        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();
        for(int i = 0; i < sWeekManager.daily_meals.size(); i++)
        {
            EditableMealFormatView emfv = new EditableMealFormatView(getContext(), i);

            View separator = new View(getContext());
            LinearLayout.LayoutParams separator_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
            separator_params.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            separator_params.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            separator_params.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            separator_params.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            separator.setLayoutParams(separator_params);
            separator.setBackgroundColor(getResources().getColor(R.color.colorLightGray));

            days_container.addView(emfv);
            days_container.addView(separator);
        }

        ImageButton button_add = new ImageButton(getContext());
        button_add.setImageDrawable(getResources().getDrawable(R.drawable.ic_button_add_black_32dp));
        button_add.setBackgroundColor(Color.TRANSPARENT);
        TableRow.LayoutParams button_params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        button_params.gravity = Gravity.CENTER;
        button_add.setLayoutParams(button_params);

        days_container.addView(button_add);

        return root;
    }
}
