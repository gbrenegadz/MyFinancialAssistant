package com.gbrenegadzdev.financeassistant.activities.ui.home;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.gbrenegadzdev.financeassistant.R;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentActivity mFragmentActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.constraint_main_activity, container, false);

        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFragmentActivity = getActivity();

        if (mFragmentActivity != null) {
            TabHost tabs = getActivity().findViewById(R.id.tabhost);
            tabs.setup();

            TabHost.TabSpec spec = tabs.newTabSpec("topIncome");
            spec.setContent(R.id.top_income);
            spec.setIndicator("Top Income");
            tabs.addTab(spec);

            spec = tabs.newTabSpec("topSavings");
            spec.setContent(R.id.top_savings);
            spec.setIndicator("Top Savings");
            tabs.addTab(spec);

            spec = tabs.newTabSpec("topExpenses");
            spec.setContent(R.id.top_expenses);
            spec.setIndicator("Top Expenses");
            tabs.addTab(spec);

            spec = tabs.newTabSpec("topProductExpenses");
            spec.setContent(R.id.top_product_expenses);
            spec.setIndicator("Top Products");
            tabs.addTab(spec);

            spec = tabs.newTabSpec("topStoreExpenses");
            spec.setContent(R.id.top_store_expenses);
            spec.setIndicator("Top Store");
            tabs.addTab(spec);
        }
    }
}