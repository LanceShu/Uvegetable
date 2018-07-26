package com.ucai.uvegetable.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.beans.OrderBean;
import com.ucai.uvegetable.view.BaseActivity;

import java.util.List;

/**
 * Created by Lance
 * on 2018/7/26.
 */

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<OrderBean> orderBeans;
    private Context context;

    public OrderAdapter(Context context, List<OrderBean> orderBeans) {
        this.context = context;
        this.orderBeans = orderBeans;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.order_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        OrderBean orderBean = orderBeans.get(position);
        String type = "";
        switch (orderBean.getPcode()) {
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
            default:
                break;
        }
        holder.type.setText(type);
        holder.name.setText(orderBean.getName());
        holder.price.setText(orderBean.getPrice()+"元/"+orderBean.getUnit());
        holder.num.setHint("0");
        holder.total.setText("0.0");
        holder.num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("")) {
                    String nums = editable.toString();
                    int num = Integer.parseInt(nums);
                    double total = Math.round((double)num * orderBean.getPrice() * 100.0) / 100.0;
                    holder.total.setText(total+"");
                    updateOrderList(orderBean.getName(), num, total);
                } else {
                    holder.total.setText("0.0");
                    updateOrderList(orderBean.getName(), 0, 0.0);
                }
            }
        });
    }

    private void updateOrderList(String name, int num, double total) {
        for (OrderBean orderBean : BaseActivity.orderBeans) {
            if (orderBean.getName().equals(name)) {
                orderBean.setNum(num);
                orderBean.setTotalPrice(total);
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderBeans.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView type;
        private TextView name;
        private TextView price;
        private EditText num;
        private TextView total;

        ViewHolder(View view) {
            super(view);
            type = view.findViewById(R.id.order_type);
            name = view.findViewById(R.id.order_name);
            price = view.findViewById(R.id.order_price);
            num = view.findViewById(R.id.order_num);
            total = view.findViewById(R.id.order_total);
        }
    }
}
