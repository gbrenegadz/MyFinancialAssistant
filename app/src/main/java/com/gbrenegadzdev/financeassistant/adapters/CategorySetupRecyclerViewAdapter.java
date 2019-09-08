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
import com.gbrenegadzdev.financeassistant.models.realm.CategorySetup;
import com.gbrenegadzdev.financeassistant.utils.SnackbarUtils;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class CategorySetupRecyclerViewAdapter extends RealmRecyclerViewAdapter<CategorySetup, CategorySetupRecyclerViewAdapter.MyViewHolder> {
    private static final String TAG = CategorySetupRecyclerViewAdapter.class.getSimpleName();
    private SnackbarUtils snackbarUtils = new SnackbarUtils();
    private ClickListener clickListener;

    public CategorySetupRecyclerViewAdapter(@Nullable OrderedRealmCollection<CategorySetup> data, boolean autoUpdate) {
        super(data, autoUpdate);
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.constraint_row_category_setup, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
        if (getData() != null) {
            final CategorySetup categorySetup = getData().get(position);
            if (categorySetup != null) {
                viewHolder.mDeductionName.setText(categorySetup.getCategoryName());
                viewHolder.mUpdate.setVisibility(categorySetup.isEditable() ? View.VISIBLE : View.INVISIBLE);
                viewHolder.mDelete.setVisibility(categorySetup.isDeletable() ? View.VISIBLE : View.INVISIBLE);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickListener != null) {
                            clickListener.onSelect(view, categorySetup);
                        }
                    }
                });

                viewHolder.mUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickListener != null) {
                            clickListener.onUpdate(view, categorySetup);
                        }
                    }
                });

                viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickListener != null) {
                            clickListener.onDelete(view, categorySetup);
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
            mDeductionName = itemView.findViewById(R.id.txt_category_name);
            mDelete = itemView.findViewById(R.id.ibtn_delete);
            mUpdate = itemView.findViewById(R.id.ibtn_update);
        }
    }
}
