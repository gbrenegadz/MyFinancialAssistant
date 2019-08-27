package com.gbrenegadzdev.financeassistant.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.gbrenegadzdev.financeassistant.R;
import com.gbrenegadzdev.financeassistant.interfaces.ClickListener;
import com.gbrenegadzdev.financeassistant.models.realm.SalaryDeductionSetup;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class SalaryDeductionRecyclerViewAdapter extends RealmRecyclerViewAdapter<SalaryDeductionSetup, SalaryDeductionRecyclerViewAdapter.MyViewHolder> {
    private static final String TAG = SalaryDeductionRecyclerViewAdapter.class.getSimpleName();
    private ClickListener clickListener;

    public SalaryDeductionRecyclerViewAdapter(@Nullable OrderedRealmCollection<SalaryDeductionSetup> data, boolean autoUpdate) {
        super(data, autoUpdate);
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.constraint_row_salary_deduction_setup, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
        if (getData() != null) {
            final SalaryDeductionSetup salaryDeductionSetup = getData().get(position);
            if (salaryDeductionSetup != null) {
                viewHolder.mDeductionName.setText(salaryDeductionSetup.getDeductionName());

                viewHolder.mUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickListener != null) {
                            clickListener.onUpdate(view, salaryDeductionSetup);
                        }
                    }
                });

                viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickListener != null) {
                            clickListener.onDelete(view, salaryDeductionSetup);
                        }
                    }
                });
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private TextView mDeductionName;
        private ImageButton mDelete;
        private ImageButton mUpdate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.image);
            mDeductionName = itemView.findViewById(R.id.txt_deduction_name);
            mDelete = itemView.findViewById(R.id.ibtn_delete);
            mUpdate = itemView.findViewById(R.id.ibtn_update);
        }
    }
}
