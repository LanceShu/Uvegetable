package com.ucai.uvegetable.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.beans.DeliverBean;
import com.ucai.uvegetable.view.DeliverActivity;

import java.util.List;

/**
 * Created by Lance
 * on 2018/7/30.
 */

public class DeliverAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<DeliverBean> deliverBeanList;

    public DeliverAdapter(Context context, List<DeliverBean> deliverBeanList) {
        this.context = context;
        this.deliverBeanList = deliverBeanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.order_fragment_item, parent, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        DeliverBean deliverBean = deliverBeanList.get(position);
        holder.date.setText(deliverBean.getDate());
        String stateNum = deliverBean.getState();
        switch (stateNum.charAt(0)) {
            case '1':
                holder.state.setTextColor(Color.parseColor("#c79145"));
                holder.state.setText("送货中");
                break;
            case '2':
                holder.state.setTextColor(Color.parseColor("#3abf93"));
                holder.state.setText("已收货");
                break;
        }
        holder.inforBtn.setOnClickListener((view -> {
            Intent intent = new Intent(context, DeliverActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("date", deliverBean.getDate());
            bundle.putString("state", deliverBean.getState());
            intent.putExtra("data", bundle);
            context.startActivity(intent);
        }));
    }

    @Override
    public int getItemCount() {
        return deliverBeanList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView state;
        private TextView inforBtn;

        ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.ofi_date);
            state = view.findViewById(R.id.ofi_state);
            inforBtn = view.findViewById(R.id.ofi_infor_btn);
        }
    }
}
