package com.gbrenegadzdev.financeassistant.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.gbrenegadzdev.financeassistant.R;
import com.gbrenegadzdev.financeassistant.models.realm.CategorySetup;
import com.gbrenegadzdev.financeassistant.models.realm.SubCategorySetup;
import com.gbrenegadzdev.financeassistant.utils.Constants;
import com.gbrenegadzdev.financeassistant.utils.DateTimeUtils;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.exceptions.RealmException;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = SplashScreenActivity.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if Access token is active
        if (AccessToken.isCurrentAccessTokenActive()) {

            new SetupCategoriesAndSubCategories(this) {

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    Log.d(TAG, "Logged In");
                    Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }.execute();
        } else {
            Log.d(TAG, "Logged Out");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private static class SetupCategoriesAndSubCategories extends AsyncTask<Void, Void, Void> {
        @SuppressLint("StaticFieldLeak")
        private Context mContext;

        SetupCategoriesAndSubCategories(Context context) {
            this.mContext = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final DateTimeUtils dateTimeUtils = new DateTimeUtils();
            final Date currentDate = dateTimeUtils.getCurrentDatetime();
            final Realm realm = Realm.getDefaultInstance();
            try {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final String[] expenseCategoryList = mContext.getResources().getStringArray(R.array.expense_category);
                        for (int ex = 0; ex < expenseCategoryList.length; ex++) {
                            // Check if expense category already exist
                            final CategorySetup category = realm.where(CategorySetup.class)
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
                    }
                });
            } catch (RealmException e) {
                e.printStackTrace();
                Log.e(TAG, "Realm Exception Error : " + e.getMessage() + "\nCaused By : " + e.getCause());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception Error : " + e.getMessage() + "\nCaused By : " + e.getCause());
            } finally {
                realm.close();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "Setup Category Started");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "Setup Category Completed");
        }

        private String[] getSubcategoryList(String category) {
            if (category.equalsIgnoreCase(Constants.FOOD)) {
                return mContext.getResources().getStringArray(R.array.food);
            }
            if (category.equalsIgnoreCase(Constants.TAXES)) {
                return mContext.getResources().getStringArray(R.array.taxes);
            }
            if (category.equalsIgnoreCase(Constants.TRANSPORTATION)) {
                return mContext.getResources().getStringArray(R.array.transportation);
            }
            if (category.equalsIgnoreCase(Constants.HOUSING)) {
                return mContext.getResources().getStringArray(R.array.housing);
            }
            if (category.equalsIgnoreCase(Constants.HEALTH_CARE)) {
                return mContext.getResources().getStringArray(R.array.health_care);
            }
            if (category.equalsIgnoreCase(Constants.SAVINGS)) {
                return mContext.getResources().getStringArray(R.array.savings);
            }
            if (category.equalsIgnoreCase(Constants.INSURANCE)) {
                return mContext.getResources().getStringArray(R.array.insurance);
            }
            if (category.equalsIgnoreCase(Constants.SERVICES_MEMBERSHIP)) {
                return mContext.getResources().getStringArray(R.array.services_membership);
            }
            if (category.equalsIgnoreCase(Constants.CHILD_EXPENSE)) {
                return mContext.getResources().getStringArray(R.array.child_expenses);
            }
            if (category.equalsIgnoreCase(Constants.HOME_SUPPLIES)) {
                return mContext.getResources().getStringArray(R.array.home_supplies);
            }
            if (category.equalsIgnoreCase(Constants.UTILITIES)) {
                return mContext.getResources().getStringArray(R.array.utilities);
            }
            if (category.equalsIgnoreCase(Constants.PERSONAL_CARE)) {
                return mContext.getResources().getStringArray(R.array.personal_care);
            }
            if (category.equalsIgnoreCase(Constants.FUN)) {
                return mContext.getResources().getStringArray(R.array.fun);
            }
            if (category.equalsIgnoreCase(Constants.PETS)) {
                return mContext.getResources().getStringArray(R.array.pets);
            }
            if (category.equalsIgnoreCase(Constants.CLOTHES)) {
                return mContext.getResources().getStringArray(R.array.clothes);
            }
            if (category.equalsIgnoreCase(Constants.CONSUMER_DEBT)) {
                return mContext.getResources().getStringArray(R.array.consumer_debt);
            }
            if (category.equalsIgnoreCase(Constants.GIVING)) {
                return mContext.getResources().getStringArray(R.array.giving);
            }
            return null;
        }
    }
}
