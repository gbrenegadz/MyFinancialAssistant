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
import com.gbrenegadzdev.financeassistant.models.realm.Expense;
import com.gbrenegadzdev.financeassistant.utils.StringUtils;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class ExpenseRecyclerViewAdapter extends RealmRecyclerViewAdapter<Expense, ExpenseRecyclerViewAdapter.MyViewHolder> {
    private static final String TAG = ExpenseRecyclerViewAdapter.class.getSimpleName();
    private ClickListener clickListener;

    public ExpenseRecyclerViewAdapter(@Nullable OrderedRealmCollection<Expense> data, boolean autoUpdate) {
        super(data, autoUpdate);
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Nullable
    @Override
    public Expense getItem(int index) {
        return super.getItem(index);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.constraint_row_expense, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
        if (getData() != null) {
            final Expense expense = getData().get(position);
            if (expense != null) {
                final StringUtils stringUtils = new StringUtils();
                viewHolder.mExpenseName.setText(expense.getExpenseName());
                viewHolder.mPaidTo.setText(expense.getPaidTo());
                viewHolder.mExpenseAmount.setText(stringUtils.getDecimal2(expense.getAmount()));

                viewHolder.mUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickListener != null) {
                            clickListener.onUpdate(view, expense);
                        }
                    }
                });

                viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickListener != null) {
                            clickListener.onDelete(view, expense);
                        }
                    }
                });
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mExpenseName;
        private TextView mPaidTo;
        private TextView mExpenseAmount;
        private ImageButton mDelete;
        private ImageButton mUpdate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseName = itemView.findViewById(R.id.txt_expense_name);
            mPaidTo = itemView.findViewById(R.id.txt_paid_to);
            mExpenseAmount = itemView.findViewById(R.id.txt_expense_amount);
            mDelete = itemView.findViewById(R.id.ibtn_delete);
            mUpdate = itemView.findViewById(R.id.ibtn_update);
        }
    }
}
