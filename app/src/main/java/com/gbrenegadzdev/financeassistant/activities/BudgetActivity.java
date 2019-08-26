package com.gbrenegadzdev.financeassistant.activities;

import android.content.DialogInterface;
import android.os.Bundle;

import com.gbrenegadzdev.financeassistant.adapters.BudgetRecyclerViewAdapter;
import com.gbrenegadzdev.financeassistant.interfaces.ClickListener;
import com.gbrenegadzdev.financeassistant.models.realm.Budget;
import com.gbrenegadzdev.financeassistant.utils.DateTimeUtils;
import com.gbrenegadzdev.financeassistant.utils.DialogUtils;
import com.gbrenegadzdev.financeassistant.utils.SnackbarUtils;
import com.gbrenegadzdev.financeassistant.utils.StringUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.gbrenegadzdev.financeassistant.R;
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

    private Button mAdd;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private BudgetRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        budgetRealm = Realm.getDefaultInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        final RealmResults<Budget> budgets = budgetRealm.where(Budget.class)
                .findAll();
        if (budgets != null) {
            for (Budget budget : budgets) {
                Log.d(TAG, "Budget : " + budget.toString());
            }
        }

        initUI();
        queryBudget();
        initListeners();
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

    private void showAddUpdateBudgetDialog(final View view, final int action, RealmObject realmObject) {
        LayoutInflater inflater = getLayoutInflater();
        View mAlertDialogCustomerView = inflater.inflate(R.layout.constraint_dialog_add_label_and_value, null);
        final TextInputEditText mName = mAlertDialogCustomerView.findViewById(R.id.et_name);
        final TextInputEditText mValue = mAlertDialogCustomerView.findViewById(R.id.et_value);

        mValue.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Check if action is for Updating
        // If Yes, then query the current Budget to edit
        final Budget selectedBudget = (Budget) realmObject;
        if (action == BUDGET_UPDATE) {
            mName.setText(selectedBudget.getBudgetName());
            mValue.setText(stringUtils.getDecimal2(selectedBudget.getAmount()));
        }

        final AlertDialog alertDialog = dialogUtils.showCustomDialog(BudgetActivity.this, getString(R.string.add_budget),
                getString(R.string.save), getString(R.string.cancel),
                mAlertDialogCustomerView,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        String name = "";
                        String value = "";

                        if (mName.getText() != null) {
                            name = mName.getText().toString();
                            if (TextUtils.isEmpty(name)) {
                                mName.setError(getString(R.string.required));
                                return;
                            }
                        }

                        if (mValue.getText() != null) {
                            value = mValue.getText().toString();
                            if (TextUtils.isEmpty(value)) {
                                mValue.setError(getString(R.string.required));
                                return;
                            }
                        }

                        final String finalName = name;
                        final String finalValue = value;
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
                                        newBudget.setCategory("");
                                        newBudget.setMonth(dateTimeUtils.getStringMonth(currentDatetime));
                                        newBudget.setYear(dateTimeUtils.getIntYear(currentDatetime));
                                        newBudget.setCreatedDatetime(currentDatetime);
                                        newBudget.setModifiedDatetime(currentDatetime);

                                        realm.insert(newBudget);

                                        snackbarUtils.create(view, getString(R.string.new_budget_has_been_added).concat(" \"").concat(newBudget.getBudgetName()).concat("\"")).show();
                                    } else if (action == BUDGET_UPDATE) {
                                        selectedBudget.setBudgetName(finalName);
                                        selectedBudget.setAmount(Double.parseDouble(finalValue.replace(",", "")));
                                        realm.insertOrUpdate(selectedBudget);

                                        snackbarUtils.create(view, getString(R.string.budget_has_been_updated).concat(" \"").concat(selectedBudget.getBudgetName()).concat("\"")).show();
                                    }
                                } catch (RealmException e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "Realm Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
                                }
                            }
                        });

                        mAdapter.notifyDataSetChanged();
                        dialogInterface.dismiss();
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
        final Budget forDeleteBudget = (Budget) realmObject;
        if (forDeleteBudget != null) {
            final String budgetName = forDeleteBudget.getBudgetName();
            dialogUtils.showYesNoDialog(this, getString(R.string.delete_budget_question),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            budgetRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    forDeleteBudget.deleteFromRealm();
                                    mAdapter.notifyDataSetChanged();

                                    snackbarUtils.create(view, getString(R.string.budget_deleted).concat(" \"").concat(budgetName).concat("\"")).show();
                                }
                            });
                        }
                    }).show();
        }
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

    private void populateBudgetList(RealmResults<Budget> budgets) {
        if (budgets != null && !budgets.isEmpty()) {
            mAdapter = new BudgetRecyclerViewAdapter(budgets, true);
            mLayoutManager = new LinearLayoutManager(this);
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
    }
}
