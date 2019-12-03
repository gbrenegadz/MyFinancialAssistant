package com.gbrenegadzdev.financeassistant.activities.ui.expenses;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gbrenegadzdev.financeassistant.R;
import com.gbrenegadzdev.financeassistant.activities.ui.income.IncomeFragment;
import com.gbrenegadzdev.financeassistant.adapters.ExpenseRecyclerViewAdapter;
import com.gbrenegadzdev.financeassistant.interfaces.ClickListener;
import com.gbrenegadzdev.financeassistant.models.realm.Expense;
import com.gbrenegadzdev.financeassistant.models.realm.Income;
import com.gbrenegadzdev.financeassistant.models.realm.PaidToEntity;
import com.gbrenegadzdev.financeassistant.models.realm.SubCategorySetup;
import com.gbrenegadzdev.financeassistant.utils.DateTimeUtils;
import com.gbrenegadzdev.financeassistant.utils.DialogUtils;
import com.gbrenegadzdev.financeassistant.utils.SnackbarUtils;
import com.gbrenegadzdev.financeassistant.utils.StringUtils;
import com.github.badoualy.datepicker.DatePickerTimeline;

import java.util.Date;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;


public class ExpensesFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = IncomeFragment.class.getSimpleName();
    private Realm realm;

    private static int EXPENSE_ADD = 1;
    private static int EXPENSE_UPDATE = 2;

    final DateTimeUtils dateTimeUtils = new DateTimeUtils();
    final DialogUtils dialogUtils = new DialogUtils();
    final StringUtils stringUtils = new StringUtils();
    final SnackbarUtils snackbarUtils = new SnackbarUtils();

    private Date currentDate = dateTimeUtils.getCurrentDatetime();

    private Resources mResources;

    private FragmentActivity mFragmentActivity;
    private ExpensesViewModel expenseViewModel;
    private TextView mToolbarText;
    private Button mAdd;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatePickerTimeline mDatePicketTimeline;
    private TextView mTotalTodayLabel, mTotalCurrentMonthLabel, mTotalMonth, mTotalToday;

    private String[] autoCompleteExpense, autoCompletePaidToEntities;
    private int selectedMonth = 0;

    private ExpenseRecyclerViewAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setupRealm();
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        expenseViewModel =
                ViewModelProviders.of(this).get(ExpensesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_expense, container, false);
        mToolbarText = root.findViewById(R.id.toolbar);
        expenseViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mToolbarText.setText(getString(R.string.menu_expense));
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
        querySubCategoriesAndPaidToEntitiesString();
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

    private void setupRealm() {
        realm = Realm.getDefaultInstance();
    }

    private void initUI(View view) {
        if (mFragmentActivity != null) {
            // Button
            mAdd = view.findViewById(R.id.btn_add);
            mAdd.setOnClickListener(this);

            mRecyclerView = view.findViewById(R.id.recycler_view);
            mTotalTodayLabel = view.findViewById(R.id.txt_total_today_label);
            mTotalCurrentMonthLabel = view.findViewById(R.id.txt_total_month_label);
            mTotalMonth = view.findViewById(R.id.txt_total_month);
            mTotalToday = view.findViewById(R.id.txt_total_today);

            mDatePicketTimeline = view.findViewById(R.id.date_picket_time_line);
            mDatePicketTimeline.setLastVisibleDate(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate), dateTimeUtils.getIntDayOfMonth(currentDate));
            mDatePicketTimeline.setSelectedDate(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate), dateTimeUtils.getIntDayOfMonth(currentDate));
            mDatePicketTimeline.centerOnSelection();
            mDatePicketTimeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
                @Override
                public void onDateSelected(int year, int month, int day, int index) {
                    queryExpenseSelectedDateAndMonth(year, month, day);
                }
            });

        }
    }


    /**============================================================================================
     * Get Sub-categories that will be used to auto complete Expense and Paid to Entities
    ============================================================================================*/
    private void querySubCategoriesAndPaidToEntitiesString(/*final AppCompatAutoCompleteTextView mNameAutoComplete, final AppCompatAutoCompleteTextView mPaidTo*/) {
        final RealmResults<SubCategorySetup> subCategorySetupRealmResults = realm.where(SubCategorySetup.class)
                .findAllAsync();
        subCategorySetupRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<SubCategorySetup>>() {
            @Override
            public void onChange(RealmResults<SubCategorySetup> subCategorySetups, OrderedCollectionChangeSet changeSet) {
                if (changeSet.isCompleteResult() && subCategorySetups.isLoaded()) {
                    if (subCategorySetups.isValid()) {
                        int counter = 0;
                        autoCompleteExpense = new String[subCategorySetupRealmResults.size()];
                        for (SubCategorySetup subCategorySetup : subCategorySetupRealmResults) {
                            Log.d(TAG, "Expense Name : " + subCategorySetup.getSubCategoryName());
                            autoCompleteExpense[counter] = subCategorySetup.getSubCategoryName();
                            counter++;
                        }

                        // Add Auto Complete Adapter to Expense
//                        ArrayAdapter<String> expenseAdapter = new ArrayAdapter<String>
//                                (mFragmentActivity, android.R.layout.simple_list_item_1, autoCompleteExpense);
//                        mNameAutoComplete.setThreshold(1); //will start working from first character
//                        mNameAutoComplete.setAdapter(expenseAdapter);
                    }
                }
            }
        });

        final RealmResults<PaidToEntity> paidToEntityRealmResults = realm.where(PaidToEntity.class)
                .findAllAsync();
        paidToEntityRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<PaidToEntity>>() {
            @Override
            public void onChange(RealmResults<PaidToEntity> paidToEntities, OrderedCollectionChangeSet changeSet) {
                if (changeSet.isCompleteResult() && paidToEntities.isLoaded()) {
                    if (paidToEntities.isValid()) {
                        int counter = 0;
                        autoCompletePaidToEntities = new String[paidToEntityRealmResults.size()];
                        for (PaidToEntity paidToEntity : paidToEntityRealmResults) {
                            Log.d(TAG, "Entity Name : " + paidToEntity.getPaidToEntityName());
                            autoCompletePaidToEntities[counter] = paidToEntity.getPaidToEntityName();
                            counter++;
                        }

//                        // Add Auto Complete Adapter to Paid To Entity
//                        ArrayAdapter<String> entityAdapter = new ArrayAdapter<String>
//                                (mFragmentActivity, android.R.layout.simple_list_item_1, autoCompletePaidToEntities);
//                        mPaidTo.setThreshold(1); //will start working from first character
//                        mPaidTo.setAdapter(entityAdapter);
                    }
                }
            }
        });
    }


    /**============================================================================================
     * Query Expense for a selected Month and Date
     * @param year
     * @param month
     * @param day
     ============================================================================================*/
    private void queryExpenseSelectedDateAndMonth(int year, int month, int day) {
        // Query all income and total amount for selected date
        queryExpenseSelectedDate(year, month, day);

        // Query all income and total amount for selected month
        selectedMonth = month;
        Log.e(TAG, "Selected Month : " + selectedMonth);
        queryIncomeSelectedMonth(year, month);
    }

    private void queryExpenseSelectedDate(int year, int month, int day) {
        Log.d(TAG, "Expense Today : I'm in!!!");
        final Date startDate = dateTimeUtils.getDate(year, month + 1, day, 0, 0, 0);
        final Date endDate = dateTimeUtils.getDate(year, month + 1, day, 23, 59, 59);
        Log.d(TAG, "Start Date : " + startDate + "\tEnd Date : " + endDate);

        final RealmResults<Expense> incomeTodayRealmResults = realm.where(Expense.class)
                .greaterThan(Expense.CREATED_DATETIME, startDate)
                .lessThanOrEqualTo(Expense.CREATED_DATETIME, endDate)
                .findAll();
        if (incomeTodayRealmResults != null) {
            populateIncomeList(incomeTodayRealmResults);
        }

        // Change the Date label
        mTotalTodayLabel.setText(dateTimeUtils.convertDateMMMdd(startDate));

        // Update the current month label
        mTotalCurrentMonthLabel.setText(dateTimeUtils.getStringMonth(startDate));
    }

    private void queryIncomeSelectedMonth(int year, int month) {
        Log.d(TAG, "Expense this month : I'm in!!!");
        final Date startDate = dateTimeUtils.getDate(year, month + 1, 1, 0, 0, 0);
        final Date endDate = dateTimeUtils.getDate(year, month + 1, dateTimeUtils.getLastDayOfMonth(year, month), 23, 59, 59);
        Log.d(TAG, "Start Month Date : " + startDate + "\tEnd Month Date : " + endDate);

        final RealmResults<Expense> incomeCurrentMonthRealmResults = realm.where(Expense.class)
                .greaterThan(Expense.CREATED_DATETIME, startDate)
                .lessThanOrEqualTo(Expense.CREATED_DATETIME, endDate)
                .findAll();
        if (incomeCurrentMonthRealmResults != null) {
            final double amount = (double) incomeCurrentMonthRealmResults.sum(Expense.AMOUNT);
            Log.e(TAG, "Current Month != null => Amount : " + amount);
            mTotalMonth.setText(stringUtils.getDecimal2(amount));
        } else {
            Log.e(TAG, "Current Month == null");
            mTotalMonth.setText(stringUtils.getDecimal2(0.0));
        }
    }

    private void populateIncomeList(RealmResults<Expense> expenses) {
        if (expenses.isEmpty()) {
            mAdapter = new ExpenseRecyclerViewAdapter(null, true);
            mTotalToday.setText(stringUtils.getDecimal2(0.0));
        } else {
            mAdapter = new ExpenseRecyclerViewAdapter(expenses, true);
            final Double selectedDateTotalAmount = (Double) expenses.sum(Income.AMOUNT);
            Log.d(TAG, "Selected Date Amount : " + selectedDateTotalAmount);
            mTotalToday.setText(stringUtils.getDecimal2(selectedDateTotalAmount));
        }

        mLayoutManager = new LinearLayoutManager(mFragmentActivity);
        ((LinearLayoutManager) mLayoutManager).setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnClickListener(new ClickListener() {
            @Override
            public void onSelect(View view, RealmObject realmObject) {

            }

            @Override
            public void onUpdate(View view, RealmObject realmObject) {
//                showAddUpdateIncomeDialog(view, INCOME_UPDATE, realmObject);
            }

            @Override
            public void onDelete(View view, RealmObject realmObject) {
//                showDeleteDialog(view, realmObject);
            }
        });
    }


    @Override
    public void onClick(View view) {

    }
}