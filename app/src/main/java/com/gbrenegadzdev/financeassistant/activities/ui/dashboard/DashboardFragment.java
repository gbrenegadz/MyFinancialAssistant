package com.gbrenegadzdev.financeassistant.activities.ui.dashboard;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.gbrenegadzdev.financeassistant.R;
import com.gbrenegadzdev.financeassistant.models.realm.Income;
import com.gbrenegadzdev.financeassistant.utils.DateTimeUtils;
import com.gbrenegadzdev.financeassistant.utils.StringUtils;

import java.util.Date;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;


public class DashboardFragment extends Fragment {
    private static final String TAG = DashboardFragment.class.getSimpleName();
    private Realm realm;
    private Resources mResources;

    private DateTimeUtils dateTimeUtils = new DateTimeUtils();
    private StringUtils stringUtils = new StringUtils();

    private Date currentDate;
    private int currentMonth, currentYear;
    private String currentMonthString;

    private DashboardViewModel homeViewModel;
    private FragmentActivity mFragmentActivity;
    private RecyclerView mTopIncomeRV, mTopExpenseRV, mTopSavingsRV, mTopProductExpenseRV, mTopStoreExpeseRV;
    private ScrollView mScrollView;
    private TextView mToolbarText;

    private TextView mCurrentMonthSummary, mCurrentMonthIncome;
    private ProgressBar mIncomeProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setupRealm();
        getCurrentDate();
        super.onCreate(savedInstanceState);
    }

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

        initUI(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        queryIncomeSelectedMonth(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate));
    }

    /**
     * Start of functions
     */

    private void setupRealm() {
        realm = Realm.getDefaultInstance();
    }

    private void getCurrentDate() {
        currentDate = dateTimeUtils.getCurrentDatetime();
        currentYear = dateTimeUtils.getIntYear(currentDate);
        currentMonth = dateTimeUtils.getIntMonth(currentDate);
        currentMonthString = dateTimeUtils.getStringMonth(currentDate);
    }

    private void initUI(View view) {
        if (mFragmentActivity != null) {
            // ScrollView
            mScrollView = view.findViewById(R.id.scrollView);
            mScrollView.smoothScrollTo(0,0);

            // Current Month Summary Label
            mCurrentMonthSummary = view.findViewById(R.id.txt_summary_label);
            mCurrentMonthSummary.setText(getString(R.string.summary).concat(": ").concat(currentMonthString));

            // Current Month Income total amount
            mCurrentMonthIncome = view.findViewById(R.id.txt_income);

            // Income Progress
            mIncomeProgress = view.findViewById(R.id.pb_income);

            setupTabhost();
        }
    }

    private void setupTabhost() {
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

    // Get Income for the current Month() {
    private void queryIncomeSelectedMonth(int year, int month) {
        // Init UI
        mCurrentMonthIncome.setVisibility(View.GONE);
        mIncomeProgress.setVisibility(View.VISIBLE);

        Log.d(TAG, "Income this month : I'm in!!!");
        final Date startDate = dateTimeUtils.getDate(year, month + 1, 1, 0, 0, 0);
        final Date endDate = dateTimeUtils.getDate(year, month + 1, dateTimeUtils.getLastDayOfMonth(year, month), 23, 59, 59);
        Log.d(TAG, "Start Month Date : " + startDate + "\tEnd Month Date : " + endDate);

        final RealmResults<Income> incomeCurrentMonthRealmResults = realm.where(Income.class)
                .greaterThan(Income.CREATED_DATETIME, startDate)
                .lessThanOrEqualTo(Income.CREATED_DATETIME, endDate)
                .findAllAsync();
        incomeCurrentMonthRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Income>>() {
            @Override
            public void onChange(RealmResults<Income> incomes, OrderedCollectionChangeSet changeSet) {
                if (changeSet.isCompleteResult() && incomes.isLoaded()) {
                    if (!incomes.isEmpty()) {
                        final double amount = (double) incomeCurrentMonthRealmResults.sum(Income.AMOUNT);
                        Log.e(TAG, "Current Month != null => Amount : " + amount);
                        mCurrentMonthIncome.setText(stringUtils.getDecimal2(amount));
                    } else {
                        Log.e(TAG, "Current Month == null");
                        mCurrentMonthIncome.setText(stringUtils.getDecimal2(0.0));
                    }

                    // Update UI
                    mCurrentMonthIncome.setVisibility(View.VISIBLE);
                    mIncomeProgress.setVisibility(View.GONE);
                }
            }
        });
    }
}