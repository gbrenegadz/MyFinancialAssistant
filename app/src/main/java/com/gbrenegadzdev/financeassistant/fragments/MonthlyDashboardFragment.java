package com.gbrenegadzdev.financeassistant.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gbrenegadzdev.financeassistant.R;

import io.realm.Realm;
import io.realm.exceptions.RealmException;

public class MonthlyDashboardFragment extends Fragment {
    private static final String TAG = MonthlyDashboardFragment.class.getSimpleName();
    private Realm monthlyDashboardRealm;

    public MonthlyDashboardFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setupRealm();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.constraint_test_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            monthlyDashboardRealm.close();
        } catch (RealmException e) {
            e.printStackTrace();
            Log.e(TAG,"Realm Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"Exception Error : " + e.getMessage() + "\nCaused by : " + e.getCause());
        }
    }

    private void setupRealm() {
        monthlyDashboardRealm = Realm.getDefaultInstance();
    }
}
