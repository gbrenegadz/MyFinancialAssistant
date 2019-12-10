package com.gbrenegadzdev.financeassistant.activities.ui.dashboard;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.gbrenegadzdev.financeassistant.models.realm.MonthlyReport;
import com.gbrenegadzdev.financeassistant.utils.DateTimeUtils;
import com.gbrenegadzdev.financeassistant.utils.StringUtils;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;


public class DashboardFragment extends Fragment {
    private static final String TAG = DashboardFragment.class.getSimpleName();
    private Realm realm;
    private Resources mResources;

    private DateTimeUtils dateTimeUtils = new DateTimeUtils();
    private StringUtils stringUtils = new StringUtils();

    private Date currentDate;
    private int currentMonth, currentYear;
    private String currentMonthString;

    private double totalCashOnHand = 0.0;
    private double totalMonthlyIncome = 0.0;
    private double totalMonthlyExpense = 0.0;
    private double totalMonthlySavings = 0.0;

    private DashboardViewModel homeViewModel;
    private FragmentActivity mFragmentActivity;
    private RecyclerView mTopIncomeRV, mTopExpenseRV, mTopSavingsRV, mTopProductExpenseRV, mTopStoreExpeseRV;
    private ScrollView mScrollView;
    private TextView mToolbarText;

    private TextView mCurrentMonthSummary, mCurrentMonthIncome, mCurrentMonthExpense, mCashOnHand;

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


        final RealmResults<MonthlyReport> monthlyReports = realm.where(MonthlyReport.class)
                .findAll();
        if (monthlyReports != null) {
            Log.d(TAG, "Total Income Monthly : " + monthlyReports.sum(MonthlyReport.AMOUNT));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        queryIncomeSelectedMonth(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate));
        getMonthlyIncomeSummary();
        getMonthlyExpenseSummary();
        getCashOnHand();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            realm.close();
        } catch (RealmException e) {
            e.printStackTrace();
            Log.e(TAG, "Realm Exception Error : " + e.getMessage() + "\nCaused By : " + e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception Error : " + e.getMessage() + "\nCaused By : " + e.getCause());
        }
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
            mScrollView.smoothScrollTo(0, 0);

            // Current Month Summary Label
            mCurrentMonthSummary = view.findViewById(R.id.txt_summary_label);
            mCurrentMonthSummary.setText(getString(R.string.summary).concat(": ").concat(currentMonthString));

            // Current Month Income Total Amount
            mCurrentMonthIncome = view.findViewById(R.id.txt_income);

            // Current Month Expense Total Amount
            mCurrentMonthExpense = view.findViewById(R.id.txt_expenses);

            // Running Cash-on-Hand
            mCashOnHand = view.findViewById(R.id.txt_cash_on_hand);

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


    /**=============================================================================================
     * Query Monthly INCOME Summary and display Amount
     =============================================================================================*/
    private void getMonthlyIncomeSummary() {
        final MonthlyReport incomeMonthlyReport = realm.where(MonthlyReport.class)
                .equalTo(MonthlyReport.MONTH, dateTimeUtils.getStringMonth(currentDate))
                .equalTo(MonthlyReport.YEAR, dateTimeUtils.getIntYear(currentDate))
                .equalTo(MonthlyReport.REPORT_TYPE, MonthlyReport.REPORT_TYPE_INCOME)
                .findFirst();
        if (incomeMonthlyReport != null) {
            totalMonthlyIncome = incomeMonthlyReport.getAmount();

            // Update UI
            mCurrentMonthIncome.setVisibility(View.VISIBLE);
            mCurrentMonthIncome.setText(stringUtils.getDecimal2(totalMonthlyIncome));
        }
    }


    /**=============================================================================================
     * Query Monthly EXPENSE Summary and display Amount
     =============================================================================================*/
    private void getMonthlyExpenseSummary() {
        final MonthlyReport expenseMonthlyReport = realm.where(MonthlyReport.class)
                .equalTo(MonthlyReport.MONTH, dateTimeUtils.getStringMonth(currentDate))
                .equalTo(MonthlyReport.YEAR, dateTimeUtils.getIntYear(currentDate))
                .equalTo(MonthlyReport.REPORT_TYPE, MonthlyReport.REPORT_TYPE_EXPENSE)
                .findFirst();
        if (expenseMonthlyReport != null) {
            totalMonthlyExpense = expenseMonthlyReport.getAmount();
            Log.d(TAG, "Total Monthly Expenses : " + totalMonthlyExpense);

            // Update UI
            mCurrentMonthExpense.setVisibility(View.VISIBLE);
            mCurrentMonthExpense.setText(stringUtils.getDecimal2(totalMonthlyExpense));
        }
    }

    /**=============================================================================================
     * Query Monthly EXPENSE Summary and display Amount
     =============================================================================================*/
    private void getCashOnHand() {
        totalCashOnHand = totalMonthlyIncome - totalMonthlyExpense;
        mCashOnHand.setText(stringUtils.getDecimal2(totalCashOnHand));
    }
}