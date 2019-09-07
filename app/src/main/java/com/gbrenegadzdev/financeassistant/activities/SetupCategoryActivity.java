package com.gbrenegadzdev.financeassistant.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gbrenegadzdev.financeassistant.R;
import com.gbrenegadzdev.financeassistant.adapters.CategorySetupRecyclerViewAdapter;
import com.gbrenegadzdev.financeassistant.interfaces.ClickListener;
import com.gbrenegadzdev.financeassistant.models.realm.CategorySetup;
import com.gbrenegadzdev.financeassistant.models.realm.SubCategorySetup;
import com.gbrenegadzdev.financeassistant.utils.Constants;
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
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

public class SetupCategoryActivity extends AppCompatActivity {
    private static final String TAG = SetupCategoryActivity.class.getSimpleName();
    private static int CATEGORY_ADD = 1;
    private static int CATEGORY_UPDATE = 2;
    private Realm setupCategoryRealm;

    private DateTimeUtils dateTimeUtils = new DateTimeUtils();
    private final DialogUtils dialogUtils = new DialogUtils();
    private final StringUtils stringUtils = new StringUtils();
    private final SnackbarUtils snackbarUtils = new SnackbarUtils();

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
        setupDefaultCategories();
        queryCategory();
        initListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            setupCategoryRealm.close();
        } catch (RealmException e) {
            e.printStackTrace();
            Log.e(TAG, "Realm Exception Error : " + e.getMessage() + "\nCaused y : " + e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception Error : " + e.getMessage() + "\nCaused y : " + e.getCause());
        }
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

    private void setupDefaultCategories() {
        final Date currentDate = dateTimeUtils.getCurrentDatetime();

        int counter = 0;

        setupCategoryRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    final String[] expenseCategoryList = getResources().getStringArray(R.array.expense_category);
                    for (int ex = 0; ex < expenseCategoryList.length; ex++) {
                        // Check if expense category already exist
                        final CategorySetup category = setupCategoryRealm.where(CategorySetup.class)
                                .equalTo(CategorySetup.CATEGORY_NAME, expenseCategoryList[ex])
                                .findFirst();
                        if (category == null) {
                            // Create object for Category
                            final CategorySetup newCategory = new CategorySetup();
                            newCategory.setCategoryId(UUID.randomUUID().toString());
                            newCategory.setCategoryName(expenseCategoryList[ex]);
                            newCategory.setCreatedDatetime(currentDate);
                            newCategory.setEditable(false);
                            newCategory.setDeletable(false);

                            // Setup Category's Sub-Categories
                            RealmList<SubCategorySetup> foodSubcategoriesList = new RealmList<>();
                            final String[] subCategories = getSubcategoryList(expenseCategoryList[ex]);
                            if (subCategories != null) {
                                for (int i = 0; i < subCategories.length; i++) {
                                    final String subCategory = subCategories[i];
                                    Log.e("Array", "Food #" + i + " : " + subCategory);
                                    final SubCategorySetup newSubCategorySetup = new SubCategorySetup();
                                    newSubCategorySetup.setSubCategoryId(UUID.randomUUID().toString());
                                    newSubCategorySetup.setSubCategoryName(expenseCategoryList[ex].concat(" - ").concat(subCategory));
                                    newSubCategorySetup.setCreatedDatetime(currentDate);
                                    newSubCategorySetup.setEditable(false);
                                    newSubCategorySetup.setDeletable(false);
                                    newSubCategorySetup.setShown(true);

                                    foodSubcategoriesList.add(newSubCategorySetup);
                                }
                            }

                            newCategory.setSubCategoryList(foodSubcategoriesList);
                            realm.insert(newCategory);
                        }
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
    }

    private String[] getSubcategoryList(String category) {
        if (category.equalsIgnoreCase(Constants.FOOD)) {
            return getResources().getStringArray(R.array.food);
        }
        if (category.equalsIgnoreCase(Constants.TAXES)) {
            return getResources().getStringArray(R.array.taxes);
        }
        if (category.equalsIgnoreCase(Constants.TRANSPORTATION)) {
            return getResources().getStringArray(R.array.transportation);
        }
        if (category.equalsIgnoreCase(Constants.HOUSING)) {
            return getResources().getStringArray(R.array.housing);
        }
        if (category.equalsIgnoreCase(Constants.HEALTH_CARE)) {
            return getResources().getStringArray(R.array.health_care);
        }
        if (category.equalsIgnoreCase(Constants.SAVINGS)) {
            return getResources().getStringArray(R.array.savings);
        }
        if (category.equalsIgnoreCase(Constants.INSURANCE)) {
            return getResources().getStringArray(R.array.insurance);
        }
        if (category.equalsIgnoreCase(Constants.SERVICES_MEMBERSHIP)) {
            return getResources().getStringArray(R.array.services_membership);
        }
        if (category.equalsIgnoreCase(Constants.CHILD_EXPENSE)) {
            return getResources().getStringArray(R.array.child_expenses);
        }
        if (category.equalsIgnoreCase(Constants.HOME_SUPPLIES)) {
            return getResources().getStringArray(R.array.home_supplies);
        }
        if (category.equalsIgnoreCase(Constants.UTILITIES)) {
            return getResources().getStringArray(R.array.utilities);
        }
        if (category.equalsIgnoreCase(Constants.PERSONAL_CARE)) {
            return getResources().getStringArray(R.array.personal_care);
        }
        if (category.equalsIgnoreCase(Constants.FUN)) {
            return getResources().getStringArray(R.array.fun);
        }
        if (category.equalsIgnoreCase(Constants.PETS)) {
            return getResources().getStringArray(R.array.pets);
        }
        if (category.equalsIgnoreCase(Constants.CLOTHES)) {
            return getResources().getStringArray(R.array.clothes);
        }
        if (category.equalsIgnoreCase(Constants.CONSUMER_DEBT)) {
            return getResources().getStringArray(R.array.consumer_debt);
        }
        if (category.equalsIgnoreCase(Constants.GIVING)) {
            return getResources().getStringArray(R.array.giving);
        }
        return null;
    }

    private void queryCategory() {
        try {
            final RealmResults<CategorySetup> categorySetupRealmResults = setupCategoryRealm.where(CategorySetup.class)
                    .findAllAsync();
            categorySetupRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<CategorySetup>>() {
                @Override
                public void onChange(RealmResults<CategorySetup> categorySetupRealmResults, OrderedCollectionChangeSet changeSet) {
                    if (changeSet.isCompleteResult() && categorySetupRealmResults.isLoaded()) {
                        if (categorySetupRealmResults.isValid()) {
                            populateCategoryList(categorySetupRealmResults);
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

    private void populateCategoryList(RealmResults<CategorySetup> categorySetupRealmResults) {
        mAdapter = new CategorySetupRecyclerViewAdapter(categorySetupRealmResults, true);
        mLayoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) mLayoutManager).setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnClickListener(new ClickListener() {
            @Override
            public void onSelect(View view, RealmObject realmObject) {
                final CategorySetup categorySetup = (CategorySetup) realmObject;
                String[] subCategories = new String[categorySetup.getSubCategoryList().size()];

                int counter = 0;
                for (SubCategorySetup subCategorySetup : categorySetup.getSubCategoryList()) {
                    Log.e(TAG, "Sub Category : " + subCategorySetup.getSubCategoryName());
                    subCategories[counter] = subCategorySetup.getSubCategoryName().replace(categorySetup.getCategoryName().concat(" - "), "");
                    counter++;
                }

                final AlertDialog.Builder showSubCategory = dialogUtils.showStringListDialogNoAction(SetupCategoryActivity.this,
                        categorySetup.getCategoryName(), subCategories,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addNewSubCategory(categorySetup);
                                dialogInterface.dismiss();
                            }
                        });
                if (showSubCategory != null) {
                    showSubCategory.show();
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


    private boolean wantToCloseDialog = false;

    private void addNewSubCategory(final CategorySetup categorySetup) {
        LayoutInflater inflater = getLayoutInflater();
        final View mAlertDialogCustomerView = inflater.inflate(R.layout.constraint_dialog_single_input, null);
        final AppCompatAutoCompleteTextView mNameAutoComplete = mAlertDialogCustomerView.findViewById(R.id.et_name_auto_complete);

        final AlertDialog alertDialog = dialogUtils.showCustomDialog(SetupCategoryActivity.this, getString(R.string.add_sub_category),
                getString(R.string.save), getString(R.string.cancel),
                mAlertDialogCustomerView, null,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        if (alertDialog != null) {
            alertDialog.show();

            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mNameAutoComplete.getText() != null && !TextUtils.isEmpty(mNameAutoComplete.getText().toString())) {
                        // Save new Sub-Category
                        setupCategoryRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                // Check if Sub-Category already exist
                                final SubCategorySetup subCategorySetup = realm.where(SubCategorySetup.class)
                                        .equalTo(SubCategorySetup.SUB_CATEGORY_NAME, mNameAutoComplete.getText().toString(), Case.INSENSITIVE)
                                        .findFirst();
                                if (subCategorySetup != null) {
                                    mNameAutoComplete.setError(getString(R.string.sub_category_exist));
                                    mNameAutoComplete.requestFocus();
                                    return;
                                }


                                final SubCategorySetup newSubCategorySetup = new SubCategorySetup();
                                newSubCategorySetup.setSubCategoryId(UUID.randomUUID().toString());
                                newSubCategorySetup.setSubCategoryName(mNameAutoComplete.getText().toString());
                                newSubCategorySetup.setCreatedDatetime(dateTimeUtils.getCurrentDatetime());
                                newSubCategorySetup.setEditable(true);
                                newSubCategorySetup.setDeletable(true);
                                newSubCategorySetup.setShown(true);

                                RealmList<SubCategorySetup> subCategorySetupRealmList = categorySetup.getSubCategoryList();
                                subCategorySetupRealmList.add(newSubCategorySetup);

                                realm.insertOrUpdate(categorySetup);

                                wantToCloseDialog = true;
                            }
                        });
                    } else {
                        mNameAutoComplete.setError(getString(R.string.invalid_input));
                        mNameAutoComplete.requestFocus();
                    }

                    //Do stuff, possibly set wantToCloseDialog to true then...
                    if (wantToCloseDialog) {
                        alertDialog.dismiss();
                        snackbarUtils.create(mRecyclerView, getString(R.string.added_new_sub_category), null).show();
                    }
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            });
        }
    }

    private void showAddUpdateBudgetDialog(final View view, final int action, RealmObject realmObject) {
        LayoutInflater inflater = getLayoutInflater();
        View mAlertDialogCustomerView = inflater.inflate(R.layout.constraint_dialog_add_label_and_value, null);
        final AutoCompleteTextView mName = mAlertDialogCustomerView.findViewById(R.id.et_name_auto_complete);
        final TextInputEditText mValue = mAlertDialogCustomerView.findViewById(R.id.et_value);

        mValue.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mValue.setVisibility(View.GONE);

        // Check if action is for Updating
        // If Yes, then query the current Salary Deduction to edit
        final CategorySetup categorySetup = (CategorySetup) realmObject;
        if (action == CATEGORY_UPDATE) {
            mName.setText(categorySetup.getCategoryName());
        }

        final AlertDialog alertDialog = dialogUtils.showCustomDialog(SetupCategoryActivity.this, getString(R.string.new_category),
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
