package com.gbrenegadzdev.financeassistant.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gbrenegadzdev.financeassistant.R;
import com.gbrenegadzdev.financeassistant.adapters.ExpenseRecyclerViewAdapter;
import com.gbrenegadzdev.financeassistant.interfaces.ClickListener;
import com.gbrenegadzdev.financeassistant.models.realm.Expense;
import com.gbrenegadzdev.financeassistant.models.realm.Income;
import com.gbrenegadzdev.financeassistant.models.realm.MonthlyReport;
import com.gbrenegadzdev.financeassistant.models.realm.PaidToEntity;
import com.gbrenegadzdev.financeassistant.models.realm.SubCategorySetup;
import com.gbrenegadzdev.financeassistant.utils.Constants;
import com.gbrenegadzdev.financeassistant.utils.DateTimeUtils;
import com.gbrenegadzdev.financeassistant.utils.DialogUtils;
import com.gbrenegadzdev.financeassistant.utils.SnackbarUtils;
import com.gbrenegadzdev.financeassistant.utils.StringUtils;
import com.github.badoualy.datepicker.DatePickerTimeline;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;
import java.util.UUID;

import io.realm.Case;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

public class ExpenseActivity extends AppCompatActivity {
    private static final String TAG = ExpenseActivity.class.getSimpleName();
    private static int EXPENSE_ADD = 1;
    private static int EXPENSE_UPDATE = 2;
    private Realm expenseRealm;

    final DateTimeUtils dateTimeUtils = new DateTimeUtils();
    final DialogUtils dialogUtils = new DialogUtils();
    final StringUtils stringUtils = new StringUtils();
    final SnackbarUtils snackbarUtils = new SnackbarUtils();

    private Date currentDate = dateTimeUtils.getCurrentDatetime();

    private RealmResults<Expense> expenseRealmResults;

    private String[] autoCompleteExpense;
    private String[] autoCompletePaidToEntities;
    private TextView mTotalAmount;
    private TextView mTotalMonth;
    private TextView mTotalToday;
    private int selectedMonth = 0;

    private Button mAdd;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatePickerTimeline mDatePicketTimeline;

    private ExpenseRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupRealm();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        initUI();
        queryExpense();
        initListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            expenseRealm.close();
        } catch (RealmException e) {
            e.printStackTrace();
            Log.e(TAG, "Realm Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        }
    }

    private void setupRealm() {
        expenseRealm = Realm.getDefaultInstance();
    }

    private void initUI() {
        mAdd = findViewById(R.id.btn_add);
        mTotalAmount = findViewById(R.id.txt_total_amount);
        mRecyclerView = findViewById(R.id.recycler_view);
        mTotalMonth = findViewById(R.id.txt_total_month);
        mTotalToday = findViewById(R.id.txt_total_today);

        mDatePicketTimeline = findViewById(R.id.date_picket_time_line);
        mDatePicketTimeline.setLastVisibleDate(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate), dateTimeUtils.getIntDayOfMonth(currentDate));
        mDatePicketTimeline.setSelectedDate(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate), dateTimeUtils.getIntDayOfMonth(currentDate));
        mDatePicketTimeline.centerOnSelection();

        queryIncomeSelectedDateAndMonth(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate), dateTimeUtils.getIntDayOfMonth(currentDate));
    }

    private void initListeners() {
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showAddUpdateExpenseDialog(view, EXPENSE_ADD, null);
            }
        });


        mDatePicketTimeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                queryIncomeSelectedDateAndMonth(year, month, day);
            }
        });
    }


    private void queryExpense() {
        try {
            expenseRealmResults = expenseRealm.where(Expense.class)
                    .findAllAsync();
            expenseRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Expense>>() {
                @Override
                public void onChange(RealmResults<Expense> expenses, OrderedCollectionChangeSet changeSet) {
                    if (changeSet.isCompleteResult() && expenseRealmResults.isLoaded()) {
                        if (expenses.size() > 0) {
                            if (expenses.isValid()) {
                                updateSubtitle(expenses.size());
                                setExpenseTotalAmount((Double) expenses.sum(Income.AMOUNT));
                            }
                        } else {
                            expenseRealmResults.removeAllChangeListeners();
                            queryExpense();
                        }
                    }
                }
            });
        } catch (RealmException e) {
            e.printStackTrace();
            Log.e(TAG, "Realm Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        }
    }

    private void querySubCategoriesAndPaidToEntitiesString(final AppCompatAutoCompleteTextView mNameAutoComplete, final AppCompatAutoCompleteTextView mPaidTo) {
        final RealmResults<SubCategorySetup> subCategorySetupRealmResults = expenseRealm.where(SubCategorySetup.class)
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
                        ArrayAdapter<String> expenseAdapter = new ArrayAdapter<String>
                                (ExpenseActivity.this, android.R.layout.simple_list_item_1, autoCompleteExpense);
                        mNameAutoComplete.setThreshold(1); //will start working from first character
                        mNameAutoComplete.setAdapter(expenseAdapter);
                    }
                }
            }
        });

        final RealmResults<PaidToEntity> paidToEntityRealmResults = expenseRealm.where(PaidToEntity.class)
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

                        // Add Auto Complete Adapter to Paid To Entity
                        ArrayAdapter<String> entityAdapter = new ArrayAdapter<String>
                                (ExpenseActivity.this, android.R.layout.simple_list_item_1, autoCompletePaidToEntities);
                        mPaidTo.setThreshold(1); //will start working from first character
                        mPaidTo.setAdapter(entityAdapter);
                    }
                }
            }
        });
    }

    private void populateExpenseList(RealmResults<Expense> expenses) {
        if (expenses.isEmpty()) {
            mAdapter = new ExpenseRecyclerViewAdapter(null, true);
            mTotalToday.setText(stringUtils.getDecimal2(0.0));
        } else {
            mAdapter = new ExpenseRecyclerViewAdapter(expenses, true);
            final Double selectedDateTotalAmount = (Double) expenses.sum(Expense.AMOUNT);
            Log.d(TAG, "Selected Date Amount : " + selectedDateTotalAmount);
            mTotalToday.setText(stringUtils.getDecimal2(selectedDateTotalAmount));
        }

        mLayoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) mLayoutManager).setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnClickListener(new ClickListener() {
            @Override
            public void onSelect(View view, RealmObject realmObject) {

            }

            @Override
            public void onUpdate(View view, RealmObject realmObject) {
                showAddUpdateExpenseDialog(view, EXPENSE_UPDATE, realmObject);
            }

            @Override
            public void onDelete(View view, RealmObject realmObject) {
                showDeleteDialog(view, realmObject);
            }
        });
    }

    private void updateSubtitle(int count) {
        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar().setSubtitle(getString(R.string.count_with_colon).concat(" ").concat(String.valueOf(count)));
        }
    }

    private void setExpenseTotalAmount(double totalAmount) {
        mTotalAmount.setText(stringUtils.getDecimal2(totalAmount));
    }

    private void showAddUpdateExpenseDialog(final View view, final int action, RealmObject realmObject) {
        LayoutInflater inflater = getLayoutInflater();
        View mAlertDialogCustomerView = inflater.inflate(R.layout.constraint_dialog_add_label_and_value, null);
        final TextInputLayout mPaidToCont = mAlertDialogCustomerView.findViewById(R.id.til_paid_to_or_source_auto_complete);
        final AppCompatAutoCompleteTextView mPaidTo = mAlertDialogCustomerView.findViewById(R.id.et_paid_to_or_source_auto_complete);
        final AppCompatAutoCompleteTextView mNameAutoComplete = mAlertDialogCustomerView.findViewById(R.id.et_name_auto_complete);
        final TextInputEditText mValue = mAlertDialogCustomerView.findViewById(R.id.et_value);

        mPaidToCont.setVisibility(View.VISIBLE);

        // Get Expense and Paid To Entities for AutoComplete
        querySubCategoriesAndPaidToEntitiesString(mNameAutoComplete, mPaidTo);

        mValue.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Check if action is for Updating
        // If Yes, then query the current Budget to edit
        final Expense selectedExpense = (Expense) realmObject;
        if (selectedExpense != null) {
            if (action == EXPENSE_UPDATE) {
                mNameAutoComplete.setText(selectedExpense.getExpenseName());
                mPaidTo.setText(selectedExpense.getPaidTo());
                mValue.setText(stringUtils.getDecimal2(selectedExpense.getAmount()));
            }
        }

        final AlertDialog alertDialog = dialogUtils.showCustomDialog(ExpenseActivity.this, getString(R.string.add_expense),
                getString(R.string.save), getString(R.string.cancel),
                mAlertDialogCustomerView,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        saveNewBudget(view, mNameAutoComplete, mValue, mPaidTo, action, dialogInterface, selectedExpense);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        if (alertDialog != null) {
            alertDialog.show();
        }
    }

    private void saveNewBudget(final View view, AppCompatAutoCompleteTextView mNameAutoComplete, TextInputEditText mValue, AppCompatAutoCompleteTextView mPaidTo, final int action, final DialogInterface dialogInterface, final Expense selectedExpense) {
        boolean isValidatedInput = true;
        if (mNameAutoComplete.getText() != null) {
            Log.e(TAG, "mNameAutoComplete.getText() is not null");
            if (TextUtils.isEmpty(mNameAutoComplete.getText().toString())) {
                Log.e(TAG, "mNameAutoComplete.getText().toString() null");
                mNameAutoComplete.setError(getString(R.string.required));
                isValidatedInput = false;
            }
        } else {
            Log.e(TAG, "mNameAutoComplete.getText() null");
            mNameAutoComplete.setError(getString(R.string.required));
            isValidatedInput = false;
        }

        if (mValue.getText() != null) {
            Log.e(TAG, "mValue.getText() is not null");
            if (TextUtils.isEmpty(mValue.getText().toString())) {
                Log.e(TAG, "mValue.getText().toString() null");
                mValue.setError(getString(R.string.required));
                isValidatedInput = false;
            }
        } else {
            Log.e(TAG, "mValue.getText() null");
            mValue.setError(getString(R.string.required));
            isValidatedInput = false;
        }


        if (isValidatedInput) {
            final String finalName = mNameAutoComplete.getText().toString();
            final String finalPaidToEntity = mPaidTo.getText().toString();
            final String finalValue = mValue.getText().toString();

            if (action == EXPENSE_ADD) {
                expenseRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            final Date currentDatetime = dateTimeUtils.getCurrentDatetime();
                            final String stringMonth = dateTimeUtils.getStringMonth(currentDatetime);
                            final int intYear = dateTimeUtils.getIntYear(currentDatetime);

                            final double amount = Double.parseDouble(finalValue.replace(",", ""));

                            final Expense newExpense = new Expense();
                            newExpense.setExpenseId(UUID.randomUUID().toString());
                            newExpense.setExpenseName(finalName);
                            newExpense.setPaidTo(finalPaidToEntity);
                            newExpense.setAmount(amount);
                            newExpense.setMonth(dateTimeUtils.getStringMonth(currentDatetime));
                            newExpense.setYear(dateTimeUtils.getIntYear(currentDatetime));
                            newExpense.setCreatedDatetime(currentDatetime);
                            newExpense.setModifiedDatetime(currentDatetime);
                            realm.insert(newExpense);

                            Log.d(TAG, "New Expense : " + newExpense.toString());

                            // Update the value of Monthly Report
                            // Just Add the value
                            new MonthlyReport(MonthlyReport.REPORT_TYPE_EXPENSE, intYear, stringMonth, amount)
                                    .addUpdateAmount();

                            // Show Snackbar notification
                            snackbarUtils.create(view,
                                    getString(R.string.expense_added).concat(" \"").concat(newExpense.getExpenseName()).concat("\""),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();
                        } catch (RealmException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Realm Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());

                            // Show Snackbar error notification
                            snackbarUtils.create(view,
                                    getString(R.string.failed_to_save).concat(" ").concat(getString(R.string.expense)),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    }).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());

                            // Show Snackbar error notification
                            snackbarUtils.create(view,
                                    getString(R.string.failed_to_save).concat(" ").concat(getString(R.string.expense)),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    }).show();
                        } finally {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mAdapter.getItemCount() == 0) {
                                        queryIncomeSelectedDateAndMonth(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate), dateTimeUtils.getIntDayOfMonth(currentDate));
                                    } else {
                                        mAdapter.notifyDataSetChanged();
                                    }
                                    updateSubtitle(mAdapter.getItemCount());
                                    dialogInterface.dismiss();
                                }
                            });
                        }
                    }
                });
            } else if (action == EXPENSE_UPDATE) {
                expenseRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            final Date currentDatetime = dateTimeUtils.getCurrentDatetime();
                            final String stringMonth = dateTimeUtils.getStringMonth(currentDatetime);
                            final int intYear = dateTimeUtils.getIntYear(currentDatetime);

                            // Compute first the difference between the old amount and the updated amount
                            // This will be used to update the Monthly Report
                            final double oldAmount = selectedExpense.getAmount();
                            final double diff = Double.parseDouble(finalValue.replace(",", "")) - oldAmount;

                            selectedExpense.setExpenseName(finalName);
                            selectedExpense.setPaidTo(finalPaidToEntity);
                            selectedExpense.setAmount(Double.parseDouble(finalValue.replace(",", "")));
                            selectedExpense.setModifiedDatetime(dateTimeUtils.getCurrentDatetime());
                            realm.insertOrUpdate(selectedExpense);

                            Log.d(TAG, "Updated Expense : " + selectedExpense.toString());

                            // Update the value of Monthly Report
                            // Just Add the value
                            new MonthlyReport(MonthlyReport.REPORT_TYPE_EXPENSE, intYear, stringMonth, diff)
                                    .addUpdateAmount();


                            // Show Snackbar notification
                            snackbarUtils.create(view,
                                    getString(R.string.expense_updated).concat(" \"").concat(selectedExpense.getExpenseName()).concat("\""),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    }).show();
                        } catch (RealmException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Realm Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());

                            // Show Snackbar error notification
                            snackbarUtils.create(view,
                                    getString(R.string.failed_to_save).concat(" ").concat(getString(R.string.expense)),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    }).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());

                            // Show Snackbar error notification
                            snackbarUtils.create(view,
                                    getString(R.string.failed_to_save).concat(" ").concat(getString(R.string.expense)),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    }).show();
                        } finally {
                            if (mAdapter.getItemCount() == 0) {
                                queryIncomeSelectedDateAndMonth(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate), dateTimeUtils.getIntDayOfMonth(currentDate));
                            } else {
                                mAdapter.notifyDataSetChanged();
                            }
                            updateSubtitle(mAdapter.getItemCount());
                            dialogInterface.dismiss();
                        }
                    }
                });

            }

            // Check if Paid To Entity exist
            // If false, save it
            expenseRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    final PaidToEntity paidToEntity = realm.where(PaidToEntity.class)
                            .equalTo(PaidToEntity.PAID_TO_ENTITY_NAME, finalPaidToEntity, Case.INSENSITIVE)
                            .findFirst();
                    if (paidToEntity == null) {
                        final PaidToEntity newPaidToEntity = new PaidToEntity();
                        newPaidToEntity.setPaidToId(UUID.randomUUID().toString());
                        newPaidToEntity.setPaidToEntityName(finalPaidToEntity);
                        newPaidToEntity.setCreatedDatetime(dateTimeUtils.getCurrentDatetime());
                        realm.insert(newPaidToEntity);
                    }
                }
            });
        } else {
            // Show Snackbar error notification
            snackbarUtils.create(view,
                    getString(R.string.failed_to_save).concat(". ").concat(getString(R.string.invalid_input).concat(".")),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
        }
    }

    private void showDeleteDialog(final View view, RealmObject realmObject) {
        final Expense forDeleteExpense = (Expense) realmObject;
        if (forDeleteExpense != null) {
            final String incomeName = forDeleteExpense.getExpenseName();
            dialogUtils.showYesNoDialog(this, getString(R.string.delete_income_question),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            expenseRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    forDeleteExpense.deleteFromRealm();
                                    mAdapter.notifyDataSetChanged();

                                    // Show Snackbar notification
                                    snackbarUtils.create(view,
                                            getString(R.string.expense_deleted).concat(" \"").concat(incomeName).concat("\""),
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).show();
                                }
                            });
                        }
                    }).show();
        }
    }

    private void queryIncomeSelectedDateAndMonth(int year, int month, int day) {
        // Query all income and total amount for selected date
        queryExpenseSelectedDate(year, month, day);

        // Get Total Amount for the month
        if (selectedMonth != month) {
            selectedMonth = month;
            queryExpenseSelectedMonth(year, month);
        }
    }

    private void queryExpenseSelectedDate(int year, int month, int day) {
        Log.d(TAG, "Expense Today : I'm in!!!");
        final Date startDate = dateTimeUtils.getDate(year, month + 1, day, 0, 0, 0);
        final Date endDate = dateTimeUtils.getDate(year, month + 1, day, 23, 59, 59);
        Log.d(TAG, "Start Date : " + startDate + "\tEnd Date : " + endDate);

        final RealmResults<Expense> expenseTodayRealmResults = expenseRealm.where(Expense.class)
                .greaterThan(Expense.CREATED_DATETIME, startDate)
                .lessThanOrEqualTo(Expense.CREATED_DATETIME, endDate)
                .findAllAsync();
        expenseTodayRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Expense>>() {
            @Override
            public void onChange(RealmResults<Expense> expenses, OrderedCollectionChangeSet changeSet) {
                if (changeSet.isCompleteResult() && expenses.isLoaded()) {
                    if (expenses.isValid()) {
                        populateExpenseList(expenses);
                    }
                }
            }
        });
    }

    private void queryExpenseSelectedMonth(int year, int month) {
        Log.d(TAG, "Expense Today : I'm in!!!");
        final Date startDate = dateTimeUtils.getDate(year, month + 1, 1, 0, 0, 0);
        final Date endDate = dateTimeUtils.getDate(year, month + 1, dateTimeUtils.getLastDayOfMonth(year, month), 23, 59, 59);
        Log.d(TAG, "Start Month Date : " + startDate + "\tEnd Month Date : " + endDate);

        final RealmResults<Expense> expenseTodayRealmResults = expenseRealm.where(Expense.class)
                .greaterThan(Expense.CREATED_DATETIME, startDate)
                .lessThanOrEqualTo(Expense.CREATED_DATETIME, endDate)
                .findAllAsync();
        expenseTodayRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Expense>>() {
            @Override
            public void onChange(RealmResults<Expense> expenses, OrderedCollectionChangeSet changeSet) {
                if (changeSet.isCompleteResult() && expenses.isLoaded()) {
                    if (expenses.isValid()) {
                        if (expenses.isEmpty()) {
                            mTotalMonth.setText(stringUtils.getDecimal2(0.0));
                        } else {
                            mTotalMonth.setText(stringUtils.getDecimal2((Double) expenses.sum(Income.AMOUNT)));
                        }
                    }
                }
            }
        });
    }

}
