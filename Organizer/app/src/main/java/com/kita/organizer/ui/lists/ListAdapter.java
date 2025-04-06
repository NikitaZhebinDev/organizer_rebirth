package com.kita.organizer.ui.lists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kita.organizer.R;
import com.kita.organizer.data.entity.ListEntity;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private List<ListEntity> listItems = new ArrayList<>();
    public void setLists(List<ListEntity> listItems) {
        this.listItems = listItems;
        notifyDataSetChanged();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        Button listButton;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            listButton = itemView.findViewById(R.id.listButton);
        }

        public void bind(ListEntity listEntity) {
            listButton.setText(listEntity.getName());
            // TODO: handle checkbox or button click here if needed
        }
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        return new ListAdapter.ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.bind(listItems.get(position));

        // Animate item appearance
        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationY(50f);
        holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}

