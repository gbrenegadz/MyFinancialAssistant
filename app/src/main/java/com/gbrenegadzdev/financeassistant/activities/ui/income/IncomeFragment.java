package com.gbrenegadzdev.financeassistant.activities.ui.income;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gbrenegadzdev.financeassistant.R;
import com.gbrenegadzdev.financeassistant.adapters.IncomeRecyclerViewAdapter;
import com.gbrenegadzdev.financeassistant.interfaces.ClickListener;
import com.gbrenegadzdev.financeassistant.models.realm.Income;
import com.gbrenegadzdev.financeassistant.models.realm.MonthlyReport;
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

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;


public class IncomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = IncomeFragment.class.getSimpleName();
    private Realm realm;

    private static int INCOME_ADD = 1;
    private static int INCOME_UPDATE = 2;

    final DateTimeUtils dateTimeUtils = new DateTimeUtils();
    final DialogUtils dialogUtils = new DialogUtils();
    final StringUtils stringUtils = new StringUtils();
    final SnackbarUtils snackbarUtils = new SnackbarUtils();

    private Date currentDate = dateTimeUtils.getCurrentDatetime();

    private Resources mResources;

    private RealmResults<Income> incomeRealmResults;

    private FragmentActivity mFragmentActivity;
    private IncomeViewModel dashboardViewModel;
    private TextView mToolbarText;
    private Button mAdd;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatePickerTimeline mDatePicketTimeline;
    private TextView mTotalTodayLabel, mTotalCurrentMonthLabel, mTotalMonth, mTotalToday;

    private String[] autoCompleteIncome;
    private int selectedMonth = 0;

    private IncomeRecyclerViewAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setupRealm();
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(IncomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_income, container, false);
        mToolbarText = root.findViewById(R.id.toolbar);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mToolbarText.setText(getString(R.string.menu_income));
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
        querySubCategoriesString();
    }

    @Override
    public void onResume() {
        super.onResume();
        queryIncomeSelectedDateAndMonth(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate), dateTimeUtils.getIntDayOfMonth(currentDate));
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
                    queryIncomeSelectedDateAndMonth(year, month, day);
                }
            });

        }
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


    /**============================================================================================
     * On Click Listner
     * @param view
    ============================================================================================*/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                showAddUpdateIncomeDialog(view, INCOME_ADD, null);
                break;
        }
    }


    /**============================================================================================
     * Query existing income data
     ============================================================================================*/

    private void queryIncomeSelectedDateAndMonth(int year, int month, int day) {
        // Query all income and total amount for selected date
        queryIncomeSelectedDate(year, month, day);

        // Query all income and total amount for selected month
        selectedMonth = month;
        Log.e(TAG, "Selected Month : " + selectedMonth);
        queryIncomeSelectedMonth(year, month);
    }

    private void queryIncomeSelectedDate(int year, int month, int day) {
        Log.d(TAG, "Income Today : I'm in!!!");
        final Date startDate = dateTimeUtils.getDate(year, month + 1, day, 0, 0, 0);
        final Date endDate = dateTimeUtils.getDate(year, month + 1, day, 23, 59, 59);
        Log.d(TAG, "Start Date : " + startDate + "\tEnd Date : " + endDate);

        final RealmResults<Income> incomeTodayRealmResults = realm.where(Income.class)
                .greaterThan(Income.CREATED_DATETIME, startDate)
                .lessThanOrEqualTo(Income.CREATED_DATETIME, endDate)
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
        Log.d(TAG, "Income this month : I'm in!!!");
        final Date startDate = dateTimeUtils.getDate(year, month + 1, 1, 0, 0, 0);
        final Date endDate = dateTimeUtils.getDate(year, month + 1, dateTimeUtils.getLastDayOfMonth(year, month), 23, 59, 59);
        Log.d(TAG, "Start Month Date : " + startDate + "\tEnd Month Date : " + endDate);

        final RealmResults<Income> incomeCurrentMonthRealmResults = realm.where(Income.class)
                .greaterThan(Income.CREATED_DATETIME, startDate)
                .lessThanOrEqualTo(Income.CREATED_DATETIME, endDate)
                .findAll();
        if (incomeCurrentMonthRealmResults != null) {
            final double amount = (double) incomeCurrentMonthRealmResults.sum(Income.AMOUNT);
            Log.e(TAG, "Current Month != null => Amount : " + amount);
            mTotalMonth.setText(stringUtils.getDecimal2(amount));
        } else {
            Log.e(TAG, "Current Month == null");
            mTotalMonth.setText(stringUtils.getDecimal2(0.0));
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
                showAddUpdateIncomeDialog(view, INCOME_UPDATE, realmObject);
            }

            @Override
            public void onDelete(View view, RealmObject realmObject) {
                showDeleteDialog(view, realmObject);
            }
        });
    }



    /**============================================================================================
     * Message Dialogs
     ============================================================================================*/

    private void showAddUpdateIncomeDialog(final View view, final int action, RealmObject realmObject) {
        LayoutInflater inflater = getLayoutInflater();
        View mAlertDialogCustomerView = inflater.inflate(R.layout.constraint_dialog_add_label_and_value, null);
        final AppCompatAutoCompleteTextView mNameAutoComplete = mAlertDialogCustomerView.findViewById(R.id.et_name_auto_complete);
        final TextInputLayout mSourceCont = mAlertDialogCustomerView.findViewById(R.id.til_paid_to_or_source_auto_complete);
        final AppCompatAutoCompleteTextView mSourceAutoComplete = mAlertDialogCustomerView.findViewById(R.id.et_paid_to_or_source_auto_complete);
        final TextInputEditText mValue = mAlertDialogCustomerView.findViewById(R.id.et_value);


        // Show Source text view since it is initially hidden
        mSourceCont.setVisibility(View.VISIBLE);
        mSourceCont.setHint(getString(R.string.source));

        // Get the strings that will be used for auto complete for income
        Log.d(TAG, "autoCompleteIncome : " + autoCompleteIncome.length);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (mFragmentActivity, android.R.layout.simple_list_item_1, autoCompleteIncome);
        mNameAutoComplete.setThreshold(1); //will start working from first character
        mNameAutoComplete.setAdapter(adapter);

        mValue.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Check if action is for Updating
        // If Yes, then query the current Income to edit
        final Income selectedIncome = (Income) realmObject;
        if (selectedIncome != null) {
            if (action == INCOME_UPDATE) {
                mNameAutoComplete.setText(selectedIncome.getIncomeName());
                mValue.setText(stringUtils.getDecimal2(selectedIncome.getAmount()));
            }
        }

        final AlertDialog alertDialog = dialogUtils.showCustomDialog(mFragmentActivity, getString(R.string.add_income),
                getString(R.string.save), getString(R.string.cancel),
                mAlertDialogCustomerView,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        saveNewIncome(view, mNameAutoComplete, mSourceAutoComplete, mValue, action, dialogInterface, selectedIncome);
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

    private void showDeleteDialog(final View view, RealmObject realmObject) {
        final Income forDeleteIncome = (Income) realmObject;
        if (forDeleteIncome != null) {
            final String incomeName = forDeleteIncome.getIncomeName();
            dialogUtils.showYesNoDialog(mFragmentActivity, getString(R.string.delete_income_question),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            realm.executeTransaction(new Realm.Transaction() {
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

    /**============================================================================================
     * Save Income
     ============================================================================================*/
    private void saveNewIncome(final View view, AppCompatAutoCompleteTextView mNameAutoComplete, AppCompatAutoCompleteTextView mSourceAutoComplete, TextInputEditText mValue, final int action, final DialogInterface dialogInterface, final Income selectedIncome) {
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

        if (mSourceAutoComplete.getText() != null) {
            Log.e(TAG, "mSourceAutoComplete.getText() is not null");
            if (TextUtils.isEmpty(mSourceAutoComplete.getText().toString())) {
                Log.e(TAG, "mSourceAutoComplete.getText().toString() null");
                mSourceAutoComplete.setError(getString(R.string.required));
                isValidatedInput = false;
            }
        } else {
            Log.e(TAG, "mSourceAutoComplete.getText() null");
            mSourceAutoComplete.setError(getString(R.string.required));
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
            final String finalSource = mSourceAutoComplete.getText().toString();
            final String finalValue = mValue.getText().toString();

            if (action == INCOME_ADD) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            final Date currentDatetime = dateTimeUtils.getCurrentDatetime();
                            final String stringMonth = dateTimeUtils.getStringMonth(currentDatetime);
                            final int intYear = dateTimeUtils.getIntYear(currentDatetime);

                            final double amount = Double.parseDouble(finalValue.replace(",", ""));

                            final Income newIncome = new Income();
                            newIncome.setIncomeId(UUID.randomUUID().toString());
                            newIncome.setIncomeName(finalName);
                            newIncome.setIncomeSource(finalSource);
                            newIncome.setAmount(amount);
                            newIncome.setMonth(stringMonth);
                            newIncome.setYear(intYear);
                            newIncome.setCreatedDatetime(currentDatetime);
                            newIncome.setModifiedDatetime(currentDatetime);
                            realm.insert(newIncome);

                            // Update the value of Monthly Report
                            // Just Add the value
                            new MonthlyReport(MonthlyReport.REPORT_TYPE_INCOME, intYear, stringMonth, amount)
                                    .addUpdateAmount();

                            Log.d(TAG, "New Income : " + newIncome.toString());

                            // Show Snackbar notification
                            snackbarUtils.create(view,
                                    getString(R.string.income_added).concat(" \"").concat(newIncome.getIncomeName()).concat("\""),
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
                                    getString(R.string.failed_to_save).concat(" ").concat(getString(R.string.income_title)),
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
                                    getString(R.string.failed_to_save).concat(" ").concat(getString(R.string.income_title)),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    }).show();
                        } finally {
                        }
                    }
                });


                // Update Monthly Total Amount
                if (mAdapter.getItemCount() == 0) {
                    Log.e(TAG, "Update total Amount == 0");
                    queryIncomeSelectedDateAndMonth(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate), dateTimeUtils.getIntDayOfMonth(currentDate));
                } else {
                    Log.e(TAG, "Update total Amount != 0");
                    queryIncomeSelectedDateAndMonth(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate), dateTimeUtils.getIntDayOfMonth(currentDate));
                    mAdapter.notifyDataSetChanged();
                }
//                                    updateSubtitle(mAdapter.getItemCount());
                dialogInterface.dismiss();
            } else if (action == INCOME_UPDATE) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            final Date currentDatetime = dateTimeUtils.getCurrentDatetime();
                            final String stringMonth = dateTimeUtils.getStringMonth(currentDatetime);
                            final int intYear = dateTimeUtils.getIntYear(currentDatetime);

                            // Compute first the difference between the old amount and the updated amount
                            // This will be used to update the Monthly Report
                            final double oldAmount = selectedIncome.getAmount();
                            final double diff = Double.parseDouble(finalValue.replace(",", "")) - oldAmount;

                            selectedIncome.setIncomeName(finalName);
                            selectedIncome.setAmount(Double.parseDouble(finalValue.replace(",", "")));
                            selectedIncome.setModifiedDatetime(dateTimeUtils.getCurrentDatetime());
                            realm.insertOrUpdate(selectedIncome);

                            Log.d(TAG, "Updated Income : " + selectedIncome.toString());

                            // Update the value of Monthly Report
                            // Just Add the value
                            new MonthlyReport(MonthlyReport.REPORT_TYPE_INCOME, intYear, stringMonth, diff)
                                    .addUpdateAmount();

                            // Show Snackbar notification
                            snackbarUtils.create(view,
                                    getString(R.string.income_updated).concat(" \"").concat(selectedIncome.getIncomeName()).concat("\""),
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
                                    getString(R.string.failed_to_save).concat(" ").concat(getString(R.string.income_title)),
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
                                    getString(R.string.failed_to_save).concat(" ").concat(getString(R.string.income_title)),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    }).show();
                        } finally {
                        }
                    }
                });

                // Update Monthly Total Amount
                if (mAdapter.getItemCount() == 0) {
                    Log.e(TAG, "Update total Amount == 0");
                    queryIncomeSelectedDateAndMonth(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate), dateTimeUtils.getIntDayOfMonth(currentDate));
                } else {
                    Log.e(TAG, "Update total Amount != 0");
                    queryIncomeSelectedDateAndMonth(dateTimeUtils.getIntYear(currentDate), dateTimeUtils.getIntMonth(currentDate), dateTimeUtils.getIntDayOfMonth(currentDate));
                    mAdapter.notifyDataSetChanged();
                }
//                            updateSubtitle(mAdapter.getItemCount());
                dialogInterface.dismiss();
            }
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
}