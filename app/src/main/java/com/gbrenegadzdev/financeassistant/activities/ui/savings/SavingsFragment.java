package com.gbrenegadzdev.financeassistant.activities.ui.savings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.gbrenegadzdev.financeassistant.R;

public class SavingsFragment extends Fragment implements View.OnClickListener {

    private SavingsViewModel savingsViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        setupRealm();
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        savingsViewModel =
                ViewModelProviders.of(this).get(SavingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_income, container, false);
//        mToolbarText = root.findViewById(R.id.toolbar);
        savingsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                mToolbarText.setText(getString(R.string.menu_income));
//                mToolbarText.setTextColor(mResources.getColor(R.color.primaryColor));
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        mFragmentActivity = getActivity();
//        mResources = getResources();
//
//        initUI(view);
//        querySubCategoriesString();
    }

    @Override
    public void onClick(View view) {

    }
}
