package com.movieapi.movie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.movieapi.movie.R;
import com.movieapi.movie.model.ButtonItem;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder> {
    Context context;
    private List<ButtonItem> buttonItemList;

    public FilterAdapter(Context context, List<ButtonItem> buttonItemList) {
        this.context = context;
        this.buttonItemList = buttonItemList;
    }

    @NonNull
    @Override
    public FilterAdapter.FilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilterHolder(LayoutInflater.from(context).inflate(R.layout.item_filter_btn, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilterAdapter.FilterHolder holder, int position) {
        holder.button.setText(buttonItemList.get(position).getButtonText());
    }

    @Override
    public int getItemCount() {
        return buttonItemList.size();
    }

    public class FilterHolder extends RecyclerView.ViewHolder {
        AppCompatButton button;
        public FilterHolder(@NonNull View itemView) {
            super(itemView);

            button = itemView.findViewById(R.id.btn_filter);
        }
    }
}
