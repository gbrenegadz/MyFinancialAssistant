package com.gbrenegadzdev.financeassistant;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gbrenegadzdev.financeassistant.activities.BudgetActivity;
import com.gbrenegadzdev.financeassistant.activities.ExpenseActivity;
import com.gbrenegadzdev.financeassistant.activities.IncomeActivity;
import com.gbrenegadzdev.financeassistant.activities.SettingsActivity;
import com.gbrenegadzdev.financeassistant.activities.SetupCategoryActivity;
import com.gbrenegadzdev.financeassistant.fragments.LifetimeDashboardFragment;
import com.gbrenegadzdev.financeassistant.models.realm.Expense;
import com.gbrenegadzdev.financeassistant.models.realm.Income;
import com.gbrenegadzdev.financeassistant.utils.StringUtils;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.Case;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Realm mainActivityRealm;

    private StringUtils stringUtils = new StringUtils();

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private TextView mTotalIncomeAmount;
    private TextView mTotalExpenseAmount;
    private TextView mTotalCashOnHand;
    private HorizontalBarChart mIncomeChart;
    private HorizontalBarChart mExpenseChart;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private double totalIncomeAmount = 0.0;
    private double totalExpenseAmount = 0.0;
    private double totalCashOnHand = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainActivityRealm = Realm.getDefaultInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initListeners();

        getTotalIncome();
        getTotalExpense();
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        mTotalIncomeAmount = findViewById(R.id.txt_total_income);
        mTotalExpenseAmount = findViewById(R.id.txt_total_expense);
        mTotalCashOnHand = findViewById(R.id.txt_cash_on_hand);
        mIncomeChart = findViewById(R.id.top_income_chart);
        mExpenseChart = findViewById(R.id.top_expense_chart);

        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewpager_dashboard);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initListeners() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mainActivityRealm.close();
        } catch (RealmException e) {
            e.printStackTrace();
            Log.e(TAG, "Realm Exception Error :" + e.getMessage() + "\nCaused by : " + e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception Error :" + e.getMessage() + "\nCaused by : " + e.getCause());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Handle the camera action
        } else if (id == R.id.nav_income) {
            Intent intent = new Intent(this, IncomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_expense) {
            Intent intent = new Intent(this, ExpenseActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_budget) {
            Intent intent = new Intent(this, BudgetActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_category) {
            Intent intent = new Intent(this, SetupCategoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
//        else if (id == R.id.nav_profile) {
//
//        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getCashOnHand() {
        totalCashOnHand = totalIncomeAmount - totalExpenseAmount;
        mTotalCashOnHand.setText(stringUtils.getDecimal2(totalCashOnHand));

        if (totalCashOnHand < 0) {
            mTotalCashOnHand.setTextColor(getResources().getColor(R.color.colorError));
        }
    }

    private boolean doneQueryAllIncome = false;

    private void getTotalIncome() {
        final RealmResults<Income> incomeRealmResults = mainActivityRealm.where(Income.class)
                .sort(Income.AMOUNT, Sort.ASCENDING)
                .findAllAsync();
        incomeRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Income>>() {
            @Override
            public void onChange(RealmResults<Income> incomes, OrderedCollectionChangeSet changeSet) {
                if (changeSet.isCompleteResult() && incomes.isLoaded()) {
                    if (incomes.isValid() && !incomes.isEmpty()) {
                        totalIncomeAmount = (double) incomes.sum(Income.AMOUNT);
                        mTotalIncomeAmount.setText(stringUtils.getDecimal2(totalIncomeAmount));

                        // Display Cash-on-Hand
                        doneQueryAllIncome = true;
                        if (doneQueryAllIncome && doneQueryAllExpenses) {
                            getCashOnHand();
                        }

                        // Setup Income Chart
                        setupChart(mIncomeChart);

                        RealmResults<Income> distinctIncomes = incomes.where()
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
                                    double yValues = (double) incomes.where().equalTo(Income.INCOME_NAME, string, Case.INSENSITIVE).findAll().sum(Income.AMOUNT);
                                    entries.add(new BarEntry(counter, (float) yValues));
                                    counter++;
                                }
                            }

                            setupXAxis(mIncomeChart, xLabels);
                            setupYAxis(mIncomeChart, totalIncomeAmount);
                            setGraphData(mIncomeChart, entries);
                        }
                    }
                }
            }
        });
    }

    private boolean doneQueryAllExpenses = false;

    private void getTotalExpense() {
        final RealmResults<Expense> incomeRealmResults = mainActivityRealm.where(Expense.class)
                .sort(Expense.AMOUNT, Sort.ASCENDING)
                .findAllAsync();
        incomeRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Expense>>() {
            @Override
            public void onChange(RealmResults<Expense> expenses, OrderedCollectionChangeSet changeSet) {
                if (changeSet.isCompleteResult() && expenses.isLoaded()) {
                    if (expenses.isValid() && !expenses.isEmpty()) {
                        totalExpenseAmount = (double) expenses.sum(Expense.AMOUNT);
                        mTotalExpenseAmount.setText(stringUtils.getDecimal2(totalExpenseAmount));

                        doneQueryAllExpenses = true;
                        if (doneQueryAllIncome && doneQueryAllExpenses) {
                            getCashOnHand();
                        }

                        // Setup Income Chart
                        setupChart(mExpenseChart);

                        RealmResults<Expense> distinctExpenses = expenses.where()
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
                                    double yValues = (double) expenses.where().equalTo(Expense.EXPENSE_NAME, string, Case.INSENSITIVE).findAll().sum(Expense.AMOUNT);
                                    entries.add(new BarEntry(counter, (float) yValues));
                                    counter++;
                                }
                            }

                            setupXAxis(mExpenseChart, xLabels);
                            setupYAxis(mExpenseChart, totalExpenseAmount);
                            setGraphData(mExpenseChart, entries);
                        }
                    }
                }
            }
        });
    }

    class Sortbyroll implements Comparator<BarEntry> {
        // Used for sorting in ascending order of
        // roll number
        public int compare(BarEntry a, BarEntry b) {
            int compareInt = (int) (a.getY() - b.getY());
            Log.d(TAG, "Compare Int : " + compareInt);
            return compareInt;
        }
    }


    /**
     * ============================================================================================
     * Start
     * Setup and Populate Bar Chart
     * ============================================================================================
     */
    private void setupChart(HorizontalBarChart barChart) {
        barChart.setDrawBarShadow(false);
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        barChart.getLegend().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawValueAboveBar(false);
    }

    private void setupXAxis(HorizontalBarChart barChart, final ArrayList<String> xLabels) {
        //Display the axis on the left (contains the labels 1*, 2* and so on)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLimitLinesBehindData(false);

        //Set label count to 5 as we are displaying 5 star rating
        if (xLabels.size() < 5) {
            xAxis.setLabelCount(xLabels.size());
        } else {
            xAxis.setLabelCount(5);
        }

        // Now add the labels to be added on the vertical axis
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xLabels.get((int) value);
            }

        });
    }

    private void setupYAxis(HorizontalBarChart barChart, double maxValue) {
        YAxis yLeft = barChart.getAxisLeft();

        //Set the minimum and maximum bar lengths as per the values that they represent
        yLeft.setAxisMaximum((float) maxValue);
        yLeft.setAxisMinimum(0f);
        yLeft.setEnabled(false);
        yLeft.setTextSize(16);

        YAxis yRight = barChart.getAxisRight();
        yRight.setDrawAxisLine(true);
        yRight.setDrawGridLines(true);
        yRight.setEnabled(false);
    }

    private void setGraphData(HorizontalBarChart barChart, ArrayList<BarEntry> entries) {
        // Plot entries to chart
        Collections.sort(entries, new Sortbyroll());
        BarDataSet barDataSet = new BarDataSet(entries, "");

        //Set the colors for bars with first color for 1*, second for 2* and so on
        barDataSet.setColors(
                ContextCompat.getColor(barChart.getContext(), R.color.md_red_500),
                ContextCompat.getColor(barChart.getContext(), R.color.md_deep_orange_400),
                ContextCompat.getColor(barChart.getContext(), R.color.md_yellow_A700),
                ContextCompat.getColor(barChart.getContext(), R.color.md_green_700),
                ContextCompat.getColor(barChart.getContext(), R.color.md_indigo_700));

        //Set bar shadows
        barChart.setDrawBarShadow(true);
        barDataSet.setBarShadowColor(Color.argb(40, 150, 150, 150));
        BarData data = new BarData(barDataSet);

        //Set the bar width
        //Note : To increase the spacing between the bars set the value of barWidth to < 1f
        data.setBarWidth(0.9f);
        data.setValueTextSize(16);
        data.setValueTextColor(getResources().getColor(R.color.white));


        //Finally set the data and refresh the graph
        barChart.setData(data);
        barChart.invalidate();
        barChart.notifyDataSetChanged();

        //Add animation to the graph
        barChart.animateY(1000);
    }

    /**
     * ============================================================================================
     * End
     * Setup and Populate Bar Chart
     * ============================================================================================
     *
     * @param viewPager
     */


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LifetimeDashboardFragment(), getString(R.string.lifetime));
        adapter.addFragment(new LifetimeDashboardFragment(), getString(R.string.monthly));
        adapter.addFragment(new LifetimeDashboardFragment(), getString(R.string.yearly));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
