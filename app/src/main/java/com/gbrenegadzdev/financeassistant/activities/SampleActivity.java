package com.gbrenegadzdev.financeassistant.activities;

import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;

import com.gbrenegadzdev.financeassistant.R;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.constraint_main_activity);

        TabHost tabs = (TabHost) findViewById(R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("topIncome");
        spec.setContent(R.id.top_income);
        spec.setIndicator("Top Income");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("topSavings");
        spec.setContent(R.id.top_savings);
        spec.setIndicator("Top Savings");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("topExpenses");
        spec.setContent(R.id.top_expenses);
        spec.setIndicator("Top Expenses");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("topProductExpenses");
        spec.setContent(R.id.top_product_expenses);
        spec.setIndicator("Top Products");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("topStoreExpenses");
        spec.setContent(R.id.top_store_expenses);
        spec.setIndicator("Top Store");
        tabs.addTab(spec);
    }



}
