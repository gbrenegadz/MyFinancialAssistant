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
import com.gbrenegadzdev.financeassistant.adapters.BudgetRecyclerViewAdapter;
import com.gbrenegadzdev.financeassistant.interfaces.ClickListener;
import com.gbrenegadzdev.financeassistant.models.realm.Budget;
import com.gbrenegadzdev.financeassistant.models.realm.SubCategorySetup;
import com.gbrenegadzdev.financeassistant.utils.DateTimeUtils;
import com.gbrenegadzdev.financeassistant.utils.DialogUtils;
import com.gbrenegadzdev.financeassistant.utils.SnackbarUtils;
import com.gbrenegadzdev.financeassistant.utils.StringUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;
import java.util.UUID;

import io.realm.Case;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

public class BudgetActivity extends AppCompatActivity {
    private static final String TAG = BudgetActivity.class.getSimpleName();
    private static int BUDGET_ADD = 1;
    private static int BUDGET_UPDATE = 2;
    private Realm budgetRealm;

    final DateTimeUtils dateTimeUtils = new DateTimeUtils();
    final DialogUtils dialogUtils = new DialogUtils();
    final StringUtils stringUtils = new StringUtils();
    final SnackbarUtils snackbarUtils = new SnackbarUtils();

    private String[] autoCompleteCategories;

    private Button mAdd;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private BudgetRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupRealm();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        initUI();
        queryBudget();
        querySubCategoriesString();
        initListeners();
    }

    private void setupRealm() {
        budgetRealm = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            budgetRealm.close();
        } catch (RealmException e) {
            e.printStackTrace();
            Log.e(TAG, "Realm Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        }
    }

    private void initUI() {
        mAdd = findViewById(R.id.btn_add);
        mRecyclerView = findViewById(R.id.recycler_view);
    }

    private void initListeners() {
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showAddUpdateBudgetDialog(view, BUDGET_ADD, null);
            }
        });
    }

    private void queryBudget() {
        try {
            final RealmResults<Budget> budgetRealmResults = budgetRealm.where(Budget.class)
                    .findAllAsync();
            budgetRealmResults.isLoaded();
            budgetRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Budget>>() {
                @Override
                public void onChange(RealmResults<Budget> budgets, OrderedCollectionChangeSet changeSet) {
                    if (budgets.isValid()) {
                        populateBudgetList(budgets);
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

    private void querySubCategoriesString() {
        final RealmResults<SubCategorySetup> subCategorySetupRealmResults = budgetRealm.where(SubCategorySetup.class)
                .findAll();
        if (subCategorySetupRealmResults != null) {
            int counter = 0;
            autoCompleteCategories = new String[subCategorySetupRealmResults.size()];
            for (SubCategorySetup subCategorySetup : subCategorySetupRealmResults) {
                Log.d(TAG, "Sub Category Setup : " + subCategorySetup.getSubCategoryName());
                autoCompleteCategories[counter] = subCategorySetup.getSubCategoryName();
                counter++;
            }
        }
    }

    private void populateBudgetList(RealmResults<Budget> budgets) {
        if (budgets != null && !budgets.isEmpty()) {
            updateSubtitle(budgets.size());
        }

        mAdapter = new BudgetRecyclerViewAdapter(budgets, true);
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
                showAddUpdateBudgetDialog(view, BUDGET_UPDATE, realmObject);
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

    private void showAddUpdateBudgetDialog(final View view, final int action, RealmObject realmObject) {
        LayoutInflater inflater = getLayoutInflater();
        View mAlertDialogCustomerView = inflater.inflate(R.layout.constraint_dialog_add_label_and_value, null);
        final AppCompatAutoCompleteTextView mNameAutoComplete = mAlertDialogCustomerView.findViewById(R.id.et_name_auto_complete);
        final TextInputEditText mValue = mAlertDialogCustomerView.findViewById(R.id.et_value);


        Log.d(TAG, "autoCompleteCategories : " + autoCompleteCategories.length);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, autoCompleteCategories);
        mNameAutoComplete.setThreshold(1); //will start working from first character
        mNameAutoComplete.setAdapter(adapter);

        mValue.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Check if action is for Updating
        // If Yes, then query the current Budget to edit
        final Budget selectedBudget = (Budget) realmObject;
        if (selectedBudget != null) {
            if (action == BUDGET_UPDATE) {
                mNameAutoComplete.setText(selectedBudget.getBudgetName());
                mValue.setText(stringUtils.getDecimal2(selectedBudget.getAmount()));
            }
        }

        final AlertDialog alertDialog = dialogUtils.showCustomDialog(BudgetActivity.this, getString(R.string.add_budget),
                getString(R.string.save), getString(R.string.cancel),
                mAlertDialogCustomerView,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        saveNewBudget(view, mNameAutoComplete, mValue, action, dialogInterface, selectedBudget);
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

    private void saveNewBudget(final View view, AppCompatAutoCompleteTextView mNameAutoComplete, TextInputEditText mValue, final int action, final DialogInterface dialogInterface, final Budget selectedBudget) {
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
            budgetRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        if (action == BUDGET_ADD) {
                            // Check if name already exist
                            final Budget searchBudget = realm.where(Budget.class)
                                    .equalTo(Budget.BUDGET_NAME, finalName, Case.INSENSITIVE)
                                    .findFirst();
                            if (searchBudget != null) {
                                snackbarUtils.createIndefinite(view, getString(R.string.budget_already_exist),
                                        getString(R.string.close), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogInterface.dismiss();
                                            }
                                        }).show();
                                return;
                            }


                            final Date currentDatetime = dateTimeUtils.getCurrentDatetime();
                            final Budget newBudget = new Budget();
                            newBudget.setBudgetId(UUID.randomUUID().toString());
                            newBudget.setBudgetName(finalName);
                            newBudget.setAmount(Double.parseDouble(finalValue.replace(",", "")));
                            newBudget.setCategory(stringUtils.getString(finalName, 0));
                            newBudget.setMonth(dateTimeUtils.getStringMonth(currentDatetime));
                            newBudget.setYear(dateTimeUtils.getIntYear(currentDatetime));
                            newBudget.setCreatedDatetime(currentDatetime);
                            newBudget.setModifiedDatetime(currentDatetime);
                            realm.insert(newBudget);

                            Log.d(TAG, "New Budget : " + newBudget.toString());

                            // Show Snackbar notification
                            snackbarUtils.create(view,
                                    getString(R.string.budget_added).concat(" \"").concat(newBudget.getBudgetName()).concat("\""),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();
                        } else if (action == BUDGET_UPDATE) {
                            selectedBudget.setBudgetName(finalName);
                            selectedBudget.setCategory(stringUtils.getString(finalName, 0));
                            selectedBudget.setAmount(Double.parseDouble(finalValue.replace(",", "")));
                            selectedBudget.setModifiedDatetime(dateTimeUtils.getCurrentDatetime());
                            realm.insertOrUpdate(selectedBudget);

                            Log.d(TAG, "Updated Budget : " + selectedBudget.toString());


                            // Show Snackbar notification
                            snackbarUtils.create(view,
                                    getString(R.string.budget_updated).concat(" \"").concat(selectedBudget.getBudgetName()).concat("\""),
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
        final Budget forDeleteBudget = (Budget) realmObject;
        if (forDeleteBudget != null) {
            final String budgetName = forDeleteBudget.getBudgetName();
            dialogUtils.showYesNoDialog(this, getString(R.string.delete_budget_question),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            budgetRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    forDeleteBudget.deleteFromRealm();
                                    mAdapter.notifyDataSetChanged();

                                    // Show Snackbar notification
                                    snackbarUtils.create(view,
                                            getString(R.string.budget_deleted).concat(" \"").concat(budgetName).concat("\""),
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
