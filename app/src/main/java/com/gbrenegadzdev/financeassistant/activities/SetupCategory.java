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
import com.gbrenegadzdev.financeassistant.adapters.CategorySetupRecyclerViewAdapter;
import com.gbrenegadzdev.financeassistant.interfaces.ClickListener;
import com.gbrenegadzdev.financeassistant.models.realm.CategorySetup;
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

public class SetupCategory extends AppCompatActivity {
    private static final String TAG = SetupCategory.class.getSimpleName();
    private static int CATEGORY_ADD = 1;
    private static int CATEGORY_UPDATE = 2;
    private Realm setupCategoryRealm;

    final DateTimeUtils dateTimeUtils = new DateTimeUtils();
    final DialogUtils dialogUtils = new DialogUtils();
    final StringUtils stringUtils = new StringUtils();
    final SnackbarUtils snackbarUtils = new SnackbarUtils();

    private CategorySetupRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerView mRecyclerView;
    private Button mAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupRealm();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_category);

        initUI();
        queryCategory();
        initListeners();
    }

    private void setupRealm() {
        setupCategoryRealm = Realm.getDefaultInstance();
    }

    private void initUI() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdd = findViewById(R.id.btn_add);
    }

    private void initListeners() {
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddUpdateBudgetDialog(view, CATEGORY_ADD, null);
            }
        });
    }

    private void queryCategory() {
        try {
            final RealmResults<CategorySetup> salaryDeductionSetupRealmResults = setupCategoryRealm.where(CategorySetup.class)
                    .findAllAsync();
            salaryDeductionSetupRealmResults.isLoaded();
            salaryDeductionSetupRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<CategorySetup>>() {
                @Override
                public void onChange(RealmResults<CategorySetup> categorySetupRealmResults, OrderedCollectionChangeSet changeSet) {
                    if (categorySetupRealmResults.isValid()) {
                        populateSalaryDeductionList(categorySetupRealmResults);
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

    private void populateSalaryDeductionList(RealmResults<CategorySetup> categorySetupRealmResults) {
        mAdapter = new CategorySetupRecyclerViewAdapter(categorySetupRealmResults, true);
        mLayoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) mLayoutManager).setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnClickListener(new ClickListener() {
            @Override
            public void onSelect(View view, RealmObject realmObject) {
                final SalaryDeductionSetup salaryDeductionSetup = (SalaryDeductionSetup) realmObject;
                if (salaryDeductionSetup != null) {
                    setupCategoryRealm.executeTransaction(new Realm.Transaction() {
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
                showAddUpdateBudgetDialog(view, CATEGORY_UPDATE, realmObject);
            }

            @Override
            public void onDelete(View view, RealmObject realmObject) {
                showDeleteDialog(view, realmObject);
            }
        });
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
        final CategorySetup categorySetup = (CategorySetup) realmObject;
        if (action == CATEGORY_UPDATE) {
            mName.setText(categorySetup.getCategoryName());
        }

        final AlertDialog alertDialog = dialogUtils.showCustomDialog(SetupCategory.this, getString(R.string.new_category),
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
                        setupCategoryRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    if (action == CATEGORY_ADD) {
                                        // Check if name already exist
                                        final CategorySetup categorySetup = realm.where(CategorySetup.class)
                                                .equalTo(CategorySetup.CATEGORY_NAME, finalName, Case.INSENSITIVE)
                                                .findFirst();
                                        if (categorySetup != null) {
                                            snackbarUtils.createIndefinite(view, getString(R.string.category_already_exist),
                                                    getString(R.string.close), new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            dialogInterface.dismiss();
                                                        }
                                                    }).show();
                                            return;
                                        }


                                        final Date currentDatetime = dateTimeUtils.getCurrentDatetime();
                                        final CategorySetup newCategory = new CategorySetup();
                                        newCategory.setCategoryId(UUID.randomUUID().toString());
                                        newCategory.setCategoryName(finalName);
                                        newCategory.setCreatedDatetime(currentDatetime);
                                        newCategory.setEditable(true);
                                        newCategory.setDeletable(true);

                                        realm.insert(newCategory);

                                        // Show Snackbar notification
                                        snackbarUtils.create(view,
                                                getString(R.string.category_added).concat(" \"").concat(newCategory.getCategoryName()).concat("\""),
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialogInterface.dismiss();
                                                    }
                                                }).show();
                                    } else if (action == CATEGORY_UPDATE) {
                                        categorySetup.setCategoryName(finalName);
                                        realm.insertOrUpdate(categorySetup);

                                        snackbarUtils.create(view,
                                                getString(R.string.category_updated).concat(" \"").concat(categorySetup.getCategoryName()).concat("\""),
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
        final CategorySetup categorySetup = (CategorySetup) realmObject;
        if (categorySetup != null) {
            final String categoryName = categorySetup.getCategoryName();
            dialogUtils.showYesNoDialog(this, getString(R.string.delete_category_question),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            setupCategoryRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    categorySetup.deleteFromRealm();
                                    mAdapter.notifyDataSetChanged();

                                    snackbarUtils.create(view,
                                            getString(R.string.category_deleted).concat(" \"").concat(categoryName).concat("\""),
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
