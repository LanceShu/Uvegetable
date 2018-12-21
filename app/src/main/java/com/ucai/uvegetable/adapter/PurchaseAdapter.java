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
import com.ucai.uvegetable.beans.PurchaseBean;
import com.ucai.uvegetable.view.PurchaseInforActivity;

import java.util.List;

/**
 * Created by Lance
 * on 2018/7/30.
 */

public class PurchaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<PurchaseBean> purchaseBeanList;

    public PurchaseAdapter(Context context, List<PurchaseBean> purchaseBeans) {
        this.context = context;
        this.purchaseBeanList = purchaseBeans;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.order_fragment_item, parent, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        PurchaseBean purchaseBean = purchaseBeanList.get(position);
        holder.date.setText(purchaseBean.getDate());
        String stateNum = purchaseBean.getState();
        switch (stateNum.charAt(0)) {
            case '1':
                holder.state.setTextColor(Color.parseColor("#001b89"));
                holder.state.setText("新采购单");
                break;
            case '2':
                holder.state.setTextColor(Color.parseColor("#896e00"));
                holder.state.setText("待退回");
                break;
            case '3':
                holder.state.setTextColor(Color.parseColor("#89000e"));
                holder.state.setText("已退回");
                break;
            case '4':
                holder.state.setTextColor(Color.parseColor("#178900"));
                holder.state.setText("已发货");
                break;
        }
        holder.inforBtn.setOnClickListener((view -> {
            Intent intent = new Intent(context, PurchaseInforActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("date", purchaseBean.getDate());
            bundle.putString("state", purchaseBean.getState());
            intent.putExtra("data", bundle);
            context.startActivity(intent);
        }));
    }

    @Override
    public int getItemCount() {
        return purchaseBeanList.size();
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
