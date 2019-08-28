package com.gbrenegadzdev.financeassistant.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.gbrenegadzdev.financeassistant.R;
import com.gbrenegadzdev.financeassistant.interfaces.ClickListener;
import com.gbrenegadzdev.financeassistant.models.realm.SalaryDeductionSetup;
import com.gbrenegadzdev.financeassistant.utils.SnackbarUtils;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class SalaryDeductionSetupRecyclerViewAdapter extends RealmRecyclerViewAdapter<SalaryDeductionSetup, SalaryDeductionSetupRecyclerViewAdapter.MyViewHolder> {
    private static final String TAG = SalaryDeductionSetupRecyclerViewAdapter.class.getSimpleName();
    private SnackbarUtils snackbarUtils = new SnackbarUtils();
    private ClickListener clickListener;

    public SalaryDeductionSetupRecyclerViewAdapter(@Nullable OrderedRealmCollection<SalaryDeductionSetup> data, boolean autoUpdate) {
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
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, int position) {
        if (getData() != null) {
            final SalaryDeductionSetup salaryDeductionSetup = getData().get(position);
            if (salaryDeductionSetup != null) {
                Log.e(TAG, "Salary Deduction Setup : " + salaryDeductionSetup.toString());
                viewHolder.mDeductionName.setText(salaryDeductionSetup.getDeductionName());
                viewHolder.mSelected.setChecked(salaryDeductionSetup.isSelected());

                viewHolder.mSelected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickListener != null) {
                            clickListener.onSelect(view, salaryDeductionSetup);
                        }
                    }
                });

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
        private CheckBox mSelected;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.image);
            mDeductionName = itemView.findViewById(R.id.txt_deduction_name);
            mDelete = itemView.findViewById(R.id.ibtn_delete);
            mUpdate = itemView.findViewById(R.id.ibtn_update);
            mSelected = itemView.findViewById(R.id.cb_selected);
        }
    }
}
