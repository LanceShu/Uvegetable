package com.ucai.uvegetable.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.beans.ProductBean;

import java.util.List;

/**
 * Created by Lance
 * on 2018/7/31.
 */

public class PurchaseInforAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ProductBean> productBeans;
    private Context context;

    public PurchaseInforAdapter(Context context, List<ProductBean> productBeans) {
        this.context = context;
        this.productBeans = productBeans;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.hto_activity_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        ProductBean productBean = productBeans.get(position);
        String type = "";
        switch (productBean.getPcode()) {
            case "0100":
                type = "蔬菜类";
                break;
            case "0200":
                type = "肉类";
                break;
            case "0300":
                type = "鱼类";
                break;
            case "0400":
                type = "干货粮油类";
                break;
            case "0500":
                type = "杂货类";
                break;
        }
        holder.category.setText(type);
        holder.name.setText(productBean.getName());
        holder.price.setText(String.valueOf(productBean.getUser_price()));
        holder.unit.setEnabled(false);
        holder.unit.setBackground(null);
        holder.unit.setText(String.valueOf(productBean.getNum()));
        holder.total.setText(String.valueOf(productBean.getPrice()));
    }

    @Override
    public int getItemCount() {
        return productBeans.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView category;
        private TextView name;
        private TextView price;
        private EditText unit;
        private TextView total;

        public ViewHolder(View view) {
            super(view);
            category = view.findViewById(R.id.order_type);
            name = view.findViewById(R.id.order_name);
            price = view.findViewById(R.id.order_price);
            unit = view.findViewById(R.id.order_num);
            total = view.findViewById(R.id.order_total);
        }
    }
}
