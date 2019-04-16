package com.ucai.uvegetable.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ucai.uvegetable.R;

import java.util.List;

/**
 * Created by Lance
 * on 2019/4/16.
 */

public class PalmListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> palmUrlsList;
    private Context context;

    public PalmListAdapter(Context context, List<String> palmUrlsList) {
        this.palmUrlsList = palmUrlsList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.palm_item, parent, false);
        return new PalmListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PalmListViewHolder viewHolder = (PalmListViewHolder) holder;
        String imageUrl = palmUrlsList.get(position);
        Glide.with(context).load(imageUrl).skipMemoryCache(false).into(viewHolder.palmImage);
        viewHolder.palmName.setText(imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.length()));
    }

    @Override
    public int getItemCount() {
        return palmUrlsList.size();
    }

    static class PalmListViewHolder extends RecyclerView.ViewHolder {
        private ImageView palmImage;
        private TextView palmName;

        PalmListViewHolder(View view) {
            super(view);
            palmImage = view.findViewById(R.id.palm_image);
            palmName = view.findViewById(R.id.palm_name);
        }
    }
}
