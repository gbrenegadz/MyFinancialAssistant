package com.gbrenegadzdev.financeassistant.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.gbrenegadzdev.financeassistant.adapters.IncomeRecyclerViewAdapter;
import com.gbrenegadzdev.financeassistant.interfaces.ClickListener;
import com.gbrenegadzdev.financeassistant.models.realm.Income;
import com.gbrenegadzdev.financeassistant.utils.DateTimeUtils;
import com.gbrenegadzdev.financeassistant.utils.DialogUtils;
import com.gbrenegadzdev.financeassistant.utils.SnackbarUtils;
import com.gbrenegadzdev.financeassistant.utils.StringUtils;
import com.github.badoualy.datepicker.DatePickerTimeline;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gbrenegadzdev.financeassistant.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;
import java.util.UUID;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

public class IncomeActivity extends AppCompatActivity {
    private static final String TAG = IncomeActivity.class.getSimpleName();
    private static int INCOME_ADD = 1;
    private static int INCOME_UPDATE = 2;
    private Realm incomeRealm;

    final DateTimeUtils dateTimeUtils = new DateTimeUtils();
    final DialogUtils dialogUtils = new DialogUtils();
    final StringUtils stringUtils = new StringUtils();
    final SnackbarUtils snackbarUtils = new SnackbarUtils();

    private Date currentDate = dateTimeUtils.getCurrentDatetime();

    private RealmResults<Income> incomeRealmResults;

    private String[] autoCompleteIncome;
    private int selectedMonth = 0;

    private Button mAdd;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatePickerTimeline mDatePicketTimeline;
    private TextView mTotalAmount;
    private TextView mTotalMonth;
    private TextView mTotalToday;


    private IncomeRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupRealm();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        initUI();
        queryIncome();
        querySubCategoriesString();
        initListeners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent result = new Intent();
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            incomeRealm.close();
        } catch (RealmException e) {
            e.printStackTrace();
            Log.e(TAG, "Realm Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        }
    }

    private void setupRealm() {
        incomeRealm = Realm.getDefaultInstance();
    }

    private void initUI() {
        mAdd = findViewById(R.id.btn_add);
        mRecyclerView = findViewById(R.id.recycler_view);
        mTotalAmount = findViewById(R.id.txt_total_amount);
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
                showAddUpdateIncomeDialog(view, INCOME_ADD, null);
            }
        });


        mDatePicketTimeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                queryIncomeSelectedDateAndMonth(year, month, day);
            }
        });
    }

    private void querySubCategoriesString() {
        String[] incomeStringArray = getResources().getStringArray(R.array.income_category);
        for (int i = 0; i < incomeStringArray.length; i++) {
            int counter = 0;
            autoCompleteIncome = new String[incomeStringArray.length];
            for (String incomeString : incomeStringArray) {
                Log.d(TAG, "Income Name : " + incomeString);
                autoCompleteIncome[counter] = incomeString;
                counter++;
            }
        }
    }

    private void populateIncomeList(RealmResults<Income> incomes) {
        if (incomes.isEmpty()) {
            mAdapter = new IncomeRecyclerViewAdapter(null, true);
            mTotalToday.setText(stringUtils.getDecimal2(0.0));
        } else {
            mAdapter = new IncomeRecyclerViewAdapter(incomes, true);
            final Double selectedDateTotalAmount = (Double) incomes.sum(Income.AMOUNT);
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
                showAddUpdateIncomeDialog(view, INCOME_UPDATE, realmObject);
            }

            @Override
            public void onDelete(View view, RealmObject realmObject) {
                showDeleteDialog(view, realmObject);
            }
        });
    }


    private void queryIncome() {
        try {
            incomeRealmResults = incomeRealm.where(Income.class)
                    .findAllAsync();
            incomeRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Income>>() {
                @Override
                public void onChange(RealmResults<Income> incomes, OrderedCollectionChangeSet changeSet) {
                    if (changeSet.isCompleteResult() && incomes.isLoaded()) {
                        if (incomes.isValid()) {
                            if (incomes.size() > 0) {
                                updateSubtitle(incomes.size());
                                setIncomeTotalAmount((Double) incomes.sum(Income.AMOUNT));
                            } else {
                                incomeRealmResults.removeAllChangeListeners();
                                queryIncome();
                            }
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

    private void updateSubtitle(int count) {
        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar().setSubtitle(getString(R.string.count_with_colon).concat(" ").concat(String.valueOf(count)));
        }
    }

    private void setIncomeTotalAmount(double totalAmount) {
        mTotalAmount.setText(stringUtils.getDecimal2(totalAmount));
    }

    private void showAddUpdateIncomeDialog(final View view, final int action, RealmObject realmObject) {
        LayoutInflater inflater = getLayoutInflater();
        View mAlertDialogCustomerView = inflater.inflate(R.layout.constraint_dialog_add_label_and_value, null);
        final AppCompatAutoCompleteTextView mNameAutoComplete = mAlertDialogCustomerView.findViewById(R.id.et_name_auto_complete);
        final TextInputEditText mValue = mAlertDialogCustomerView.findViewById(R.id.et_value);


        Log.d(TAG, "autoCompleteIncome : " + autoCompleteIncome.length);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, autoCompleteIncome);
        mNameAutoComplete.setThreshold(1); //will start working from first character
        mNameAutoComplete.setAdapter(adapter);

        mValue.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Check if action is for Updating
        // If Yes, then query the current Budget to edit
        final Income selectedIncome = (Income) realmObject;
        if (selectedIncome != null) {
            if (action == INCOME_UPDATE) {
                mNameAutoComplete.setText(selectedIncome.getIncomeName());
                mValue.setText(stringUtils.getDecimal2(selectedIncome.getAmount()));
            }
        }

        final AlertDialog alertDialog = dialogUtils.showCustomDialog(IncomeActivity.this, getString(R.string.add_income),
                getString(R.string.save), getString(R.string.cancel),
                mAlertDialogCustomerView,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        saveNewBudget(view, mNameAutoComplete, mValue, action, dialogInterface, selectedIncome);
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

    private void saveNewBudget(final View view, AppCompatAutoCompleteTextView mNameAutoComplete, TextInputEditText mValue, final int action, final DialogInterface dialogInterface, final Income selectedIncome) {
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
            final String finalValue = mValue.getText().toString();

            incomeRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        if (action == INCOME_ADD) {
                            final Date currentDatetime = dateTimeUtils.getCurrentDatetime();
                            final Income newIncome = new Income();
                            newIncome.setIncomeId(UUID.randomUUID().toString());
                            newIncome.setIncomeName(finalName);
                            newIncome.setAmount(Double.parseDouble(finalValue.replace(",", "")));
                            newIncome.setMonth(dateTimeUtils.getStringMonth(currentDatetime));
                            newIncome.setYear(dateTimeUtils.getIntYear(currentDatetime));
                            newIncome.setCreatedDatetime(currentDatetime);
                            newIncome.setModifiedDatetime(currentDatetime);
                            realm.insert(newIncome);

                            Log.d(TAG, "New Income : " + newIncome.toString());

                            // Show Snackbar notification
                            snackbarUtils.create(view,
                                    getString(R.string.budget_added).concat(" \"").concat(newIncome.getIncomeName()).concat("\""),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();
                        } else if (action == INCOME_UPDATE) {
                            selectedIncome.setIncomeName(finalName);
                            selectedIncome.setAmount(Double.parseDouble(finalValue.replace(",", "")));
                            selectedIncome.setModifiedDatetime(dateTimeUtils.getCurrentDatetime());
                            realm.insertOrUpdate(selectedIncome);

                            Log.d(TAG, "Updated Budget : " + selectedIncome.toString());


                            // Show Snackbar notification
                            snackbarUtils.create(view,
                                    getString(R.string.budget_updated).concat(" \"").concat(selectedIncome.getIncomeName()).concat("\""),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    }).show();
                        }
                    } catch (RealmException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Realm Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());

                        // Show Snackbar error notification
                        snackbarUtils.create(view,
                                getString(R.string.failed_to_save).concat(" ").concat(getString(R.string.budget_title)),
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
                                getString(R.string.failed_to_save).concat(" ").concat(getString(R.string.budget_title)),
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
        final Income forDeleteIncome = (Income) realmObject;
        if (forDeleteIncome != null) {
            final String incomeName = forDeleteIncome.getIncomeName();
            dialogUtils.showYesNoDialog(this, getString(R.string.delete_income_question),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            incomeRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    forDeleteIncome.deleteFromRealm();
                                    mAdapter.notifyDataSetChanged();

                                    // Show Snackbar notification
                                    snackbarUtils.create(view,
                                            getString(R.string.income_deleted).concat(" \"").concat(incomeName).concat("\""),
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
        queryIncomeSelectedDate(year, month, day);

        // Get Total Amount for the month
        if (selectedMonth != month) {
            selectedMonth = month;
            queryIncomeSelectedMonth(year, month);
        }
    }

    private void queryIncomeSelectedDate(int year, int month, int day) {
        Log.d(TAG, "Income Today : I'm in!!!");
        final Date startDate = dateTimeUtils.getDate(year, month + 1, day, 0, 0, 0);
        final Date endDate = dateTimeUtils.getDate(year, month + 1, day, 23, 59, 59);
        Log.d(TAG, "Start Date : " + startDate + "\tEnd Date : " + endDate);

        final RealmResults<Income> incomeTodayRealmResults = incomeRealm.where(Income.class)
                .greaterThan(Income.CREATED_DATETIME, startDate)
                .lessThanOrEqualTo(Income.CREATED_DATETIME, endDate)
                .findAllAsync();
        incomeTodayRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Income>>() {
            @Override
            public void onChange(RealmResults<Income> incomes, OrderedCollectionChangeSet changeSet) {
                if (changeSet.isCompleteResult() && incomes.isLoaded()) {
                    if (incomes.isValid()) {
                        populateIncomeList(incomes);
                    }
                }
            }
        });
    }

    private void queryIncomeSelectedMonth(int year, int month) {
        Log.d(TAG, "Income Today : I'm in!!!");
        final Date startDate = dateTimeUtils.getDate(year, month + 1, 1, 0, 0, 0);
        final Date endDate = dateTimeUtils.getDate(year, month + 1, dateTimeUtils.getLastDayOfMonth(year, month), 23, 59, 59);
        Log.d(TAG, "Start Month Date : " + startDate + "\tEnd Month Date : " + endDate);

        final RealmResults<Income> incomeTodayRealmResults = incomeRealm.where(Income.class)
                .greaterThan(Income.CREATED_DATETIME, startDate)
                .lessThanOrEqualTo(Income.CREATED_DATETIME, endDate)
                .findAllAsync();
        incomeTodayRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Income>>() {
            @Override
            public void onChange(RealmResults<Income> incomes, OrderedCollectionChangeSet changeSet) {
                if (changeSet.isCompleteResult() && incomes.isLoaded()) {
                    if (incomes.isValid()) {
                        if (incomes.isEmpty()) {
                            mTotalMonth.setText(stringUtils.getDecimal2(0.0));
                        } else {
                            mTotalMonth.setText(stringUtils.getDecimal2((Double) incomes.sum(Income.AMOUNT)));
                        }
                    }
                }
            }
        });
    }
}
