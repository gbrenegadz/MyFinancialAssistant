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
import com.gbrenegadzdev.financeassistant.models.realm.Income;
import com.gbrenegadzdev.financeassistant.utils.StringUtils;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class IncomeRecyclerViewAdapter extends RealmRecyclerViewAdapter<Income, IncomeRecyclerViewAdapter.MyViewHolder>{
    private static final String TAG = IncomeRecyclerViewAdapter.class.getSimpleName();
    private ClickListener clickListener;

    public IncomeRecyclerViewAdapter(@Nullable OrderedRealmCollection<Income> data, boolean autoUpdate) {
        super(data, autoUpdate);
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Nullable
    @Override
    public Income getItem(int index) {
        return super.getItem(index);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.constraint_row_income, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
        if (getData() != null) {
            final Income income = getData().get(position);
            if (income != null) {
                final StringUtils stringUtils = new StringUtils();
                viewHolder.mIncomeName.setText(income.getIncomeName());
                viewHolder.mIncomeAmount.setText(stringUtils.getDecimal2(income.getAmount()));

                viewHolder.mUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickListener != null) {
                            clickListener.onUpdate(view, income);
                        }
                    }
                });

                viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickListener != null) {
                            clickListener.onDelete(view, income);
                        }
                    }
                });
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mIncomeName;
        private TextView mIncomeAmount;
        private ImageButton mDelete;
        private ImageButton mUpdate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeName = itemView.findViewById(R.id.txt_income_name);
            mIncomeAmount = itemView.findViewById(R.id.txt_income_amount);
            mDelete = itemView.findViewById(R.id.ibtn_delete);
            mUpdate = itemView.findViewById(R.id.ibtn_update);
        }
    }
}
