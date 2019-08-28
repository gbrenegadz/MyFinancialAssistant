package com.gbrenegadzdev.financeassistant.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.LinearLayout;

import com.gbrenegadzdev.financeassistant.R;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private LinearLayout mSalaryDeductionCont;
    private LinearLayout mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initUI();
        initListeners();
    }

    private void initUI() {
        mCategory = findViewById(R.id.ll_category_cont);
        mSalaryDeductionCont = findViewById(R.id.ll_salary_deduction_cont);
    }

    private void initListeners() {
        mCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, SetupCategory.class);
                startActivity(intent);
            }
        });

        mSalaryDeductionCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, SetupSalaryDeductionActivity.class);
                startActivity(intent);
            }
        });
    }

}
