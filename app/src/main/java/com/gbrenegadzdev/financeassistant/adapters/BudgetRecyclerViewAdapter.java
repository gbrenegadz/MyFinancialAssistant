package com.gbrenegadzdev.financeassistant.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.gbrenegadzdev.financeassistant.R;
import com.gbrenegadzdev.financeassistant.interfaces.ClickListener;
import com.gbrenegadzdev.financeassistant.models.realm.Budget;
import com.gbrenegadzdev.financeassistant.utils.StringUtils;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class BudgetRecyclerViewAdapter extends RealmRecyclerViewAdapter<Budget, BudgetRecyclerViewAdapter.MyViewHolder> {
    private static final String TAG = BudgetRecyclerViewAdapter.class.getSimpleName();
    private ClickListener clickListener;

    public BudgetRecyclerViewAdapter(@Nullable OrderedRealmCollection<Budget> data, boolean autoUpdate) {
        super(data, autoUpdate);
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Nullable
    @Override
    public Budget getItem(int index) {
        return super.getItem(index);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.constraint_row_budget, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
        final Budget budget = getData().get(position);
        if (budget != null) {
            final StringUtils stringUtils = new StringUtils();
            viewHolder.mBudgetName.setText(budget.getBudgetName());
            viewHolder.mBudgetAmount.setText(stringUtils.getDecimal2(budget.getAmount()));

            viewHolder.mUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.onUpdate(view, budget);
                    }
                }
            });

            viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.onDelete(view, budget);
                    }
                }
            });
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mBudgetName;
        private TextView mBudgetAmount;
        private ImageButton mDelete;
        private ImageButton mUpdate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mBudgetName = itemView.findViewById(R.id.txt_budget_name);
            mBudgetAmount = itemView.findViewById(R.id.txt_budget_amount);
            mDelete = itemView.findViewById(R.id.ibtn_delete);
            mUpdate = itemView.findViewById(R.id.ibtn_update);
        }
    }
}
