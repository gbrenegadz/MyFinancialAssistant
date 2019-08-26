package com.gbrenegadzdev.financeassistant.interfaces;

import android.view.View;

import com.gbrenegadzdev.financeassistant.models.realm.Budget;

import io.realm.RealmObject;

public interface ClickListener {
    void onSelect(View view, RealmObject realmObject);

    void onUpdate(View view, RealmObject realmObject);

    void onDelete(View view, RealmObject realmObject);
}
