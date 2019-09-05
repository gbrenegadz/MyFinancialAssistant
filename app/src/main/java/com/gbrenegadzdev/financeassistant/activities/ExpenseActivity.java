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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gbrenegadzdev.financeassistant.R;
import com.gbrenegadzdev.financeassistant.adapters.ExpenseRecyclerViewAdapter;
import com.gbrenegadzdev.financeassistant.interfaces.ClickListener;
import com.gbrenegadzdev.financeassistant.models.realm.Expense;
import com.gbrenegadzdev.financeassistant.models.realm.SubCategorySetup;
import com.gbrenegadzdev.financeassistant.utils.DateTimeUtils;
import com.gbrenegadzdev.financeassistant.utils.DialogUtils;
import com.gbrenegadzdev.financeassistant.utils.SnackbarUtils;
import com.gbrenegadzdev.financeassistant.utils.StringUtils;
import com.github.badoualy.datepicker.DatePickerTimeline;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;
import java.util.UUID;

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

    private String[] autoCompleteExpense;

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
        querySubCategoriesString();
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
        mRecyclerView = findViewById(R.id.recycler_view);

        Date currentDate = dateTimeUtils.getCurrentDatetime();
        mDatePicketTimeline = findViewById(R.id.date_picket_time_line);
        mDatePicketTimeline.setLastVisibleDate(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate), dateTimeUtils.getIntDayOfMonth(currentDate));
        mDatePicketTimeline.setSelectedDate(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate), dateTimeUtils.getIntDayOfMonth(currentDate));
        mDatePicketTimeline.centerOnSelection();
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
                snackbarUtils.create(mRecyclerView, "The Date is : " + month + "/" + day + "/" + year,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();
            }
        });
    }

    private void querySubCategoriesString() {
        final RealmResults<SubCategorySetup> subCategorySetupRealmResults = expenseRealm.where(SubCategorySetup.class)
                .findAll();
        if (subCategorySetupRealmResults != null) {
            int counter = 0;
            autoCompleteExpense = new String[subCategorySetupRealmResults.size()];
            for (SubCategorySetup subCategorySetup : subCategorySetupRealmResults) {
                Log.d(TAG, "Expense Name : " + subCategorySetup.getSubCategoryName());
                autoCompleteExpense[counter] = subCategorySetup.getSubCategoryName();
                counter++;
            }
        }
    }

    private void queryExpense() {
        try {
            final RealmResults<Expense> expenseRealmResults = expenseRealm.where(Expense.class)
                    .findAllAsync();
            expenseRealmResults.isLoaded();
            expenseRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Expense>>() {
                @Override
                public void onChange(RealmResults<Expense> expenses, OrderedCollectionChangeSet changeSet) {
                    if (changeSet.isCompleteResult() && expenseRealmResults.isLoaded()) {
                        if (expenses.isValid()) {
                            populateExpenseList(expenses);
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

    private void populateExpenseList(RealmResults<Expense> expenses) {
        mAdapter = new ExpenseRecyclerViewAdapter(expenses, true);
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

    private void showAddUpdateExpenseDialog(final View view, final int action, RealmObject realmObject) {
        LayoutInflater inflater = getLayoutInflater();
        View mAlertDialogCustomerView = inflater.inflate(R.layout.constraint_dialog_add_label_and_value, null);
        final AppCompatAutoCompleteTextView mNameAutoComplete = mAlertDialogCustomerView.findViewById(R.id.et_name_auto_complete);
        final TextInputEditText mValue = mAlertDialogCustomerView.findViewById(R.id.et_value);


        Log.d(TAG, "autoCompleteIncome : " + autoCompleteExpense.length);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, autoCompleteExpense);
        mNameAutoComplete.setThreshold(1); //will start working from first character
        mNameAutoComplete.setAdapter(adapter);

        mValue.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Check if action is for Updating
        // If Yes, then query the current Budget to edit
        final Expense selectedExpense = (Expense) realmObject;
        if (selectedExpense != null) {
            if (action == EXPENSE_UPDATE) {
                mNameAutoComplete.setText(selectedExpense.getExpenseName());
                mValue.setText(stringUtils.getDecimal2(selectedExpense.getAmount()));
            }
        }

        final AlertDialog alertDialog = dialogUtils.showCustomDialog(ExpenseActivity.this, getString(R.string.add_expense),
                getString(R.string.save), getString(R.string.cancel),
                mAlertDialogCustomerView,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        saveNewBudget(view, mNameAutoComplete, mValue, action, dialogInterface, selectedExpense);
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

    private void saveNewBudget(final View view, AppCompatAutoCompleteTextView mNameAutoComplete, TextInputEditText mValue, final int action, final DialogInterface dialogInterface, final Expense selectedExpense) {
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
            expenseRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        if (action == EXPENSE_ADD) {
                            final Date currentDatetime = dateTimeUtils.getCurrentDatetime();
                            final Expense newIncome = new Expense();
                            newIncome.setExpenseId(UUID.randomUUID().toString());
                            newIncome.setExpenseName(finalName);
                            newIncome.setAmount(Double.parseDouble(finalValue.replace(",", "")));
                            newIncome.setMonth(dateTimeUtils.getStringMonth(currentDatetime));
                            newIncome.setYear(dateTimeUtils.getIntYear(currentDatetime));
                            newIncome.setCreatedDatetime(currentDatetime);
                            newIncome.setModifiedDatetime(currentDatetime);
                            realm.insert(newIncome);

                            Log.d(TAG, "New Expense : " + newIncome.toString());

                            // Show Snackbar notification
                            snackbarUtils.create(view,
                                    getString(R.string.expense_added).concat(" \"").concat(newIncome.getExpenseName()).concat("\""),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();
                        } else if (action == EXPENSE_UPDATE) {
                            selectedExpense.setExpenseName(finalName);
                            selectedExpense.setAmount(Double.parseDouble(finalValue.replace(",", "")));
                            selectedExpense.setModifiedDatetime(dateTimeUtils.getCurrentDatetime());
                            realm.insertOrUpdate(selectedExpense);

                            Log.d(TAG, "Updated Expense : " + selectedExpense.toString());


                            // Show Snackbar notification
                            snackbarUtils.create(view,
                                    getString(R.string.expense_updated).concat(" \"").concat(selectedExpense.getExpenseName()).concat("\""),
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
                        mAdapter.notifyDataSetChanged();
                        dialogInterface.dismiss();
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


}
