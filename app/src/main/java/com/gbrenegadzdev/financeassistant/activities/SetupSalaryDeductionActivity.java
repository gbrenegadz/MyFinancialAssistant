package com.gbrenegadzdev.financeassistant.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gbrenegadzdev.financeassistant.R;
import com.gbrenegadzdev.financeassistant.adapters.SalaryDeductionRecyclerViewAdapter;
import com.gbrenegadzdev.financeassistant.interfaces.ClickListener;
import com.gbrenegadzdev.financeassistant.models.realm.SalaryDeductionSetup;
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

public class SetupSalaryDeductionActivity extends AppCompatActivity {
    private static final String TAG = SetupSalaryDeductionActivity.class.getSimpleName();
    private static int SALARY_DEDUCTION_ADD = 1;
    private static int SALARY_DEDUCTION_UPDATE = 2;
    private Realm setupDeductionRealm;

    final DateTimeUtils dateTimeUtils = new DateTimeUtils();
    final DialogUtils dialogUtils = new DialogUtils();
    final StringUtils stringUtils = new StringUtils();
    final SnackbarUtils snackbarUtils = new SnackbarUtils();

    private SalaryDeductionRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerView mRecyclerView;
    private Button mAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupRealm();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_salary_deduction);

        final RealmResults<SalaryDeductionSetup> salaryDeductionSetups = setupDeductionRealm.where(SalaryDeductionSetup.class)
                .findAll();
        if (salaryDeductionSetups != null) {
            for (SalaryDeductionSetup salaryDeductionSetup : salaryDeductionSetups) {
                Log.e(TAG, "Salary Deduction Setup : " + salaryDeductionSetup.toString());
            }
        }

        initUI();
        queryBudget();
        initListeners();
    }

    private void setupRealm() {
        setupDeductionRealm = Realm.getDefaultInstance();
    }

    private void initUI() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdd = findViewById(R.id.btn_add);
    }


    private void initListeners() {
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddUpdateBudgetDialog(view, SALARY_DEDUCTION_ADD, null);
            }
        });
    }

    private void queryBudget() {
        try {
            final RealmResults<SalaryDeductionSetup> salaryDeductionSetupRealmResults = setupDeductionRealm.where(SalaryDeductionSetup.class)
                    .findAllAsync();
            salaryDeductionSetupRealmResults.isLoaded();
            salaryDeductionSetupRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<SalaryDeductionSetup>>() {
                @Override
                public void onChange(RealmResults<SalaryDeductionSetup> salaryDeductionSetups, OrderedCollectionChangeSet changeSet) {
                    if (salaryDeductionSetups.isValid()) {
                        populateSalaryDeductionList(salaryDeductionSetups);
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

    private void populateSalaryDeductionList(RealmResults<SalaryDeductionSetup> salaryDeductionSetups) {
        if (salaryDeductionSetups != null && !salaryDeductionSetups.isEmpty()) {
            mAdapter = new SalaryDeductionRecyclerViewAdapter(salaryDeductionSetups, true);
            mLayoutManager = new LinearLayoutManager(this);
            ((LinearLayoutManager) mLayoutManager).setOrientation(RecyclerView.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);

            mAdapter.setOnClickListener(new ClickListener() {
                @Override
                public void onSelect(View view, RealmObject realmObject) {
                    final SalaryDeductionSetup salaryDeductionSetup = (SalaryDeductionSetup) realmObject;
                    if (salaryDeductionSetup != null) {
                        setupDeductionRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                salaryDeductionSetup.setSelected(!salaryDeductionSetup.isSelected());
                                realm.insertOrUpdate(salaryDeductionSetup);
                            }
                        });
                    }
                }

                @Override
                public void onUpdate(View view, RealmObject realmObject) {
                    showAddUpdateBudgetDialog(view, SALARY_DEDUCTION_UPDATE, realmObject);
                }

                @Override
                public void onDelete(View view, RealmObject realmObject) {
                    showDeleteDialog(view, realmObject);
                }
            });
        }
    }

    private void showAddUpdateBudgetDialog(final View view, final int action, RealmObject realmObject) {
        LayoutInflater inflater = getLayoutInflater();
        View mAlertDialogCustomerView = inflater.inflate(R.layout.constraint_dialog_add_label_and_value, null);
        final TextInputEditText mName = mAlertDialogCustomerView.findViewById(R.id.et_name);
        final TextInputEditText mValue = mAlertDialogCustomerView.findViewById(R.id.et_value);

        mValue.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mValue.setVisibility(View.GONE);

        // Check if action is for Updating
        // If Yes, then query the current Salary Deduction to edit
        final SalaryDeductionSetup selectedSalaryDeductionSetup = (SalaryDeductionSetup) realmObject;
        if (action == SALARY_DEDUCTION_UPDATE) {
            mName.setText(selectedSalaryDeductionSetup.getDeductionName());
        }

        final AlertDialog alertDialog = dialogUtils.showCustomDialog(SetupSalaryDeductionActivity.this, getString(R.string.new_salary_deduction),
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

                        final String finalName = name;
                        final String finalValue = value;
                        setupDeductionRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    if (action == SALARY_DEDUCTION_ADD) {
                                        // Check if name already exist
                                        final SalaryDeductionSetup salaryDeductionSetup = realm.where(SalaryDeductionSetup.class)
                                                .equalTo(SalaryDeductionSetup.DEDUCTION_NAME, finalName, Case.INSENSITIVE)
                                                .findFirst();
                                        if (salaryDeductionSetup != null) {
                                            snackbarUtils.createIndefinite(view, getString(R.string.salary_deduction_already_exist),
                                                    getString(R.string.close), new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            dialogInterface.dismiss();
                                                        }
                                                    }).show();
                                            return;
                                        }


                                        final Date currentDatetime = dateTimeUtils.getCurrentDatetime();
                                        final SalaryDeductionSetup newBudget = new SalaryDeductionSetup();
                                        newBudget.setSalaryDeductionId(UUID.randomUUID().toString());
                                        newBudget.setDeductionName(finalName);
                                        newBudget.setCreatedDatetime(currentDatetime);
                                        newBudget.setModifiedDatetime(currentDatetime);

                                        realm.insert(newBudget);

                                        // Show Snackbar notification
                                        snackbarUtils.create(view,
                                                getString(R.string.deduction_added).concat(" \"").concat(newBudget.getDeductionName()).concat("\""),
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialogInterface.dismiss();
                                                    }
                                                }).show();
                                    } else if (action == SALARY_DEDUCTION_UPDATE) {
                                        selectedSalaryDeductionSetup.setDeductionName(finalName);
                                        realm.insertOrUpdate(selectedSalaryDeductionSetup);

                                        snackbarUtils.create(view,
                                                getString(R.string.deduction_updated).concat(" \"").concat(selectedSalaryDeductionSetup.getDeductionName()).concat("\""),
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialogInterface.dismiss();
                                                    }
                                                }).show();
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
        final SalaryDeductionSetup forDeleteSalaryDeductionSetup = (SalaryDeductionSetup) realmObject;
        if (forDeleteSalaryDeductionSetup != null) {
            final String salaryDeductionName = forDeleteSalaryDeductionSetup.getDeductionName();
            dialogUtils.showYesNoDialog(this, getString(R.string.delete_budget_question),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            setupDeductionRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    forDeleteSalaryDeductionSetup.deleteFromRealm();
                                    mAdapter.notifyDataSetChanged();

                                    snackbarUtils.create(view,
                                            getString(R.string.deduction_deleted).concat(" \"").concat(salaryDeductionName).concat("\""),
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
