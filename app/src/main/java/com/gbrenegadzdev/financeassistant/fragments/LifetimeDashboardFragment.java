package com.gbrenegadzdev.financeassistant.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gbrenegadzdev.financeassistant.R;
import com.gbrenegadzdev.financeassistant.models.realm.Expense;
import com.gbrenegadzdev.financeassistant.models.realm.Income;
import com.gbrenegadzdev.financeassistant.utils.ChartUtils;
import com.gbrenegadzdev.financeassistant.utils.StringUtils;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

import io.realm.Case;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmException;

public class LifetimeDashboardFragment extends Fragment {
    private static final String TAG = LifetimeDashboardFragment.class.getSimpleName();
    private Realm dashboardRealm;

    private ChartUtils chartUtils = new ChartUtils();
    private StringUtils stringUtils = new StringUtils();

    private TextView mTotalIncomeAmount;
    private TextView mTotalExpenseAmount;
    private TextView mTotalCashOnHand;
    private HorizontalBarChart mIncomeChart;
    private HorizontalBarChart mExpenseChart;


    private double totalIncomeAmount = 0.0;
    private double totalExpenseAmount = 0.0;
    private double totalCashOnHand = 0.0;
    private boolean doneQueryAllIncome = false;
    private boolean doneQueryAllExpenses = false;

    public LifetimeDashboardFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setupRealm();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.constraint_test_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
        showTopIncome();
        showTopExpenses();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            dashboardRealm.close();
        } catch (RealmException e) {
            e.printStackTrace();
            Log.e(TAG, "Realm Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        }
    }

    private void setupRealm() {
        dashboardRealm = Realm.getDefaultInstance();
    }

    private void initUI() {
        mTotalIncomeAmount = getActivity().findViewById(R.id.txt_total_income);
        mTotalExpenseAmount = getActivity().findViewById(R.id.txt_total_expense);
        mTotalCashOnHand = getActivity().findViewById(R.id.txt_cash_on_hand);

        mIncomeChart = getActivity().findViewById(R.id.top_income_chart);
        mExpenseChart = getActivity().findViewById(R.id.top_expense_chart);
    }

    private void showTopIncome() {
        final RealmResults<Income> incomeRealmResults = dashboardRealm.where(Income.class)
                .sort(Income.AMOUNT, Sort.ASCENDING)
                .findAll();
        if (incomeRealmResults != null) {
            totalIncomeAmount = (double) incomeRealmResults.sum(Income.AMOUNT);
            mTotalIncomeAmount.setText(stringUtils.getDecimal2(totalIncomeAmount));

            // Display Cash-on-Hand
            doneQueryAllIncome = true;
            if (doneQueryAllIncome && doneQueryAllExpenses) {
                getCashOnHand();
            }

            // Setup Income Chart
            chartUtils.setupChart(mIncomeChart);

            RealmResults<Income> distinctIncomes = incomeRealmResults.where()
                    .distinct(Income.INCOME_NAME)
                    .findAll();
            if (distinctIncomes != null) {
                final ArrayList<String> xLabels = new ArrayList<>();
                for (Income distinctIncome : distinctIncomes) {
                    Log.d(TAG, "Realm Object : " + distinctIncome.toString());
                    xLabels.add(distinctIncome.getIncomeName());
                }

                //Add a list of bar entries
                //Set bar entries and add necessary formatting
                ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
                float counter = 0;
                for (String string : xLabels) {
                    if (counter < 5) {
                        double yValues = (double) incomeRealmResults.where().equalTo(Income.INCOME_NAME, string, Case.INSENSITIVE).findAll().sum(Income.AMOUNT);
                        entries.add(new BarEntry(counter, (float) yValues));
                        counter++;
                    }
                }

                chartUtils.setupXAxis(mIncomeChart, xLabels);
                chartUtils.setupYAxis(mIncomeChart, totalIncomeAmount);
                chartUtils.setGraphData(getContext(), mIncomeChart, entries);
            }
        }
    }

    private void showTopExpenses() {
        final RealmResults<Expense> incomeRealmResults = dashboardRealm.where(Expense.class)
                .sort(Expense.AMOUNT, Sort.ASCENDING)
                .findAll();
        if (incomeRealmResults != null) {
            totalExpenseAmount = (double) incomeRealmResults.sum(Expense.AMOUNT);
            mTotalExpenseAmount.setText(stringUtils.getDecimal2(totalExpenseAmount));

            doneQueryAllExpenses = true;
            if (doneQueryAllIncome && doneQueryAllExpenses) {
                getCashOnHand();
            }

            // Setup Income Chart
            chartUtils.setupChart(mExpenseChart);

            RealmResults<Expense> distinctExpenses = incomeRealmResults.where()
                    .distinct(Expense.EXPENSE_NAME)
                    .findAll();
            if (distinctExpenses != null) {
                final ArrayList<String> xLabels = new ArrayList<>();
                for (Expense distinctExpense : distinctExpenses) {
                    Log.d(TAG, "Realm Object : " + distinctExpense.toString());
                    xLabels.add(distinctExpense.getExpenseName());
                }

                //Add a list of bar entries
                //Set bar entries and add necessary formatting
                ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
                float counter = 0;
                for (String string : xLabels) {
                    if (counter < 5) {
                        double yValues = (double) incomeRealmResults.where().equalTo(Expense.EXPENSE_NAME, string, Case.INSENSITIVE).findAll().sum(Expense.AMOUNT);
                        entries.add(new BarEntry(counter, (float) yValues));
                        counter++;
                    }
                }

                chartUtils.setupXAxis(mExpenseChart, xLabels);
                chartUtils.setupYAxis(mExpenseChart, totalExpenseAmount);
                chartUtils.setGraphData(getContext(), mExpenseChart, entries);
            }
        }
    }

    private void getCashOnHand() {
        totalCashOnHand = totalIncomeAmount - totalExpenseAmount;
        mTotalCashOnHand.setText(stringUtils.getDecimal2(totalCashOnHand));

        if (totalCashOnHand < 0) {
            mTotalCashOnHand.setTextColor(getResources().getColor(R.color.colorError));
        }
    }
}
