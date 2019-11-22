package com.gbrenegadzdev.financeassistant.activities.ui.income;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.gbrenegadzdev.financeassistant.R;


public class IncomeFragment extends Fragment {
    private static final String TAG = IncomeFragment.class.getSimpleName();
    private Resources mResources;

    private FragmentActivity mFragmentActivity;
    private IncomeViewModel dashboardViewModel;
    private TextView mToolbarText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(IncomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_income, container, false);
        mToolbarText = root.findViewById(R.id.toolbar);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mToolbarText.setText(getString(R.string.menu_income));
                mToolbarText.setTextColor(mResources.getColor(R.color.primaryColor));
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentActivity = getActivity();
        mResources = getResources();

        initUI();

    }

    private void initUI() {
        if (mFragmentActivity != null) {
            TextView mToolbar = mFragmentActivity.findViewById(R.id.toolbar);
            mToolbar.setText(getString(R.string.income));
            mToolbar.setTextColor(mResources.getColor(R.color.primaryColor));

        }
    }

}