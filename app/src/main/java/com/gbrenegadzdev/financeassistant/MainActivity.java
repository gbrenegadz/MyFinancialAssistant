package com.gbrenegadzdev.financeassistant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gbrenegadzdev.financeassistant.activities.BudgetActivity;
import com.gbrenegadzdev.financeassistant.activities.ExpenseActivity;
import com.gbrenegadzdev.financeassistant.activities.IncomeActivity;
import com.gbrenegadzdev.financeassistant.activities.SettingsActivity;
import com.gbrenegadzdev.financeassistant.activities.SetupCategoryActivity;
import com.gbrenegadzdev.financeassistant.fragments.LifetimeDashboardFragment;
import com.gbrenegadzdev.financeassistant.fragments.MonthlyDashboardFragment;
import com.gbrenegadzdev.financeassistant.models.realm.CategorySetup;
import com.gbrenegadzdev.financeassistant.models.realm.SubCategorySetup;
import com.gbrenegadzdev.financeassistant.utils.Constants;
import com.gbrenegadzdev.financeassistant.utils.DateTimeUtils;
import com.gbrenegadzdev.financeassistant.utils.StringUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.exceptions.RealmException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Realm mainActivityRealm;

    private StringUtils stringUtils = new StringUtils();
    private DateTimeUtils dateTimeUtils = new DateTimeUtils();

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private TextView mTotalIncomeAmount;
    private TextView mTotalExpenseAmount;
    private TextView mTotalCashOnHand;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private LoginButton loginButton;

    // Facebook Authentication
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainActivityRealm = Realm.getDefaultInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        initUI();
        initListeners();
        setupDefaultCategories();
        initFacebookButton();
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_dashboard));
        }

        viewPager = findViewById(R.id.viewpager_dashboard);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initListeners() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initFacebookButton() {

        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Collections.singletonList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d(TAG, "Login Success!!!");
                Log.d(TAG, "Result : " + loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
                Log.d(TAG, "Login Cancelled!!!");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG, "Login Failed!!!");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mainActivityRealm.close();
        } catch (RealmException e) {
            e.printStackTrace();
            Log.e(TAG, "Realm Exception Error :" + e.getMessage() + "\nCaused by : " + e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception Error :" + e.getMessage() + "\nCaused by : " + e.getCause());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Handle the camera action
//            Intent intent = new Intent(this, CombinedChartActivity.class);
//            startActivity(intent);
        } else if (id == R.id.nav_income) {
            Intent intent = new Intent(this, IncomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_expense) {
            Intent intent = new Intent(this, ExpenseActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_budget) {
            Intent intent = new Intent(this, BudgetActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_category) {
            Intent intent = new Intent(this, SetupCategoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
//        else if (id == R.id.nav_profile) {
//
//        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LifetimeDashboardFragment(), getString(R.string.lifetime));
        adapter.addFragment(new MonthlyDashboardFragment(), getString(R.string.monthly));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupDefaultCategories() {
        final Date currentDate = dateTimeUtils.getCurrentDatetime();

        mainActivityRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    final String[] expenseCategoryList = getResources().getStringArray(R.array.expense_category);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
