package com.baldino.myapp003.ui.day_format;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baldino.myapp003.R;

public class DayFormatFragment extends Fragment {

    private DayFormatViewModel dayFormatViewModel;

    public static DayFormatFragment newInstance() {
        return new DayFormatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        dayFormatViewModel = ViewModelProviders.of(this).get(DayFormatViewModel.class);
        View root = inflater.inflate(R.layout.fragment_day_format, container, false);

        return root;
    }

}
