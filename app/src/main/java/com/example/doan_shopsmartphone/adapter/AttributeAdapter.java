package com.example.doan_shopsmartphone.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_shopsmartphone.R;

import java.util.ArrayList;
import java.util.List;

public class AttributeAdapter extends RecyclerView.Adapter<AttributeAdapter.ViewHolder> {

    private List<AttributeItem> items;
    private OnAttributeClickListener listener;
    private String selectedValue = null;

    public interface OnAttributeClickListener {
        void onAttributeClick(String value);
    }

    public AttributeAdapter(List<String> values, OnAttributeClickListener listener) {
        this.items = new ArrayList<>();
        for (String v : values) {
            this.items.add(new AttributeItem(v, true));
        }
        this.listener = listener;
    }

    public void updateStates(List<String> validValues) {
        for (AttributeItem item : items) {
            item.enabled = validValues.contains(item.value);
            // If currently selected item becomes invalid, deselect it
            if (!item.enabled && item.value.equals(selectedValue)) {
                selectedValue = null;
            }
        }
        notifyDataSetChanged();
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(String value) {
        this.selectedValue = value;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attribute_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttributeItem item = items.get(position);
        holder.tvName.setText(item.value);
        
        holder.itemView.setEnabled(item.enabled);
        holder.itemView.setSelected(item.value.equals(selectedValue));
        
        if (!item.enabled) {
            holder.tvName.setTextColor(Color.LTGRAY);
        } else if (item.value.equals(selectedValue)) {
            holder.tvName.setTextColor(Color.parseColor("#2196F3"));
        } else {
            holder.tvName.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> {
            if (item.enabled) {
                if (item.value.equals(selectedValue)) {
                    selectedValue = null; // Deselect if click again
                } else {
                    selectedValue = item.value;
                }
                notifyDataSetChanged();
                listener.onAttributeClick(selectedValue);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAttributeName);
        }
    }

    static class AttributeItem {
        String value;
        boolean enabled;

        AttributeItem(String value, boolean enabled) {
            this.value = value;
            this.enabled = enabled;
        }
    }
}
