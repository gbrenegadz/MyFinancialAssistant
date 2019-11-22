package com.gbrenegadzdev.financeassistant.activities.ui.dashboard;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.gbrenegadzdev.financeassistant.R;


public class DashboardFragment extends Fragment {
    private static final String TAG = DashboardFragment.class.getSimpleName();
    private Resources mResources;


    private DashboardViewModel homeViewModel;
    private FragmentActivity mFragmentActivity;
    private RecyclerView mTopIncomeRV, mTopExpenseRV, mTopSavingsRV, mTopProductExpenseRV, mTopStoreExpeseRV;
    private ScrollView mScrollView;
    private TextView mToolbarText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.constraint_main_activity, container, false);

        mToolbarText = root.findViewById(R.id.toolbar);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mToolbarText.setText(getString(R.string.menu_dashboard));
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
            // ScrollView
            mScrollView = mFragmentActivity.findViewById(R.id.scrollView);
            mScrollView.smoothScrollTo(0,0);

            TabHost tabs = mFragmentActivity.findViewById(R.id.tabhost);
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

            mTopIncomeRV = mFragmentActivity.findViewById(R.id.rv_top_income);
            mTopIncomeRV.setFocusable(false);

            mTopSavingsRV = mFragmentActivity.findViewById(R.id.rv_top_savings);
            mTopSavingsRV.setFocusable(false);

            mTopExpenseRV = mFragmentActivity.findViewById(R.id.rv_top_expense);
            mTopExpenseRV.setFocusable(false);

            mTopProductExpenseRV = mFragmentActivity.findViewById(R.id.rv_top_product_expenses);
            mTopProductExpenseRV.setFocusable(false);

            mTopStoreExpeseRV = mFragmentActivity.findViewById(R.id.rv_top_store_expenses);
            mTopStoreExpeseRV.setFocusable(false);
        }
    }
}