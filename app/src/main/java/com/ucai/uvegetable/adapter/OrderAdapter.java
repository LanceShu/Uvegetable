package com.ucai.uvegetable.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.beans.OrderedProductBean;
import com.ucai.uvegetable.view.BaseActivity;

import java.util.List;

/**
 * Created by Lance
 * on 2018/7/26.
 */

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<OrderedProductBean> orderedProductBeans;
    private Context context;

    public OrderAdapter(Context context, List<OrderedProductBean> orderedProductBeans) {
        this.context = context;
        this.orderedProductBeans = orderedProductBeans;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.hto_activity_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        OrderedProductBean orderedProductBean = orderedProductBeans.get(position);
        String type = "";
        switch (orderedProductBean.getPcode()) {
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
        holder.name.setText(orderedProductBean.getName());
        holder.price.setText(orderedProductBean.getPrice()+"元/"+ orderedProductBean.getUnit());
        holder.num.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        holder.num.setHint(String.valueOf(orderedProductBean.getNum()));
        holder.total.setText(String.valueOf(orderedProductBean.getTotalPrice()));
        holder.num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("")
                        && !editable.toString().equals(".")) {
                    String nums = editable.toString();
                    double num = Double.parseDouble(nums);
                    double total = Math.round(num * orderedProductBean.getPrice() * 100.0) / 100.0;
                    holder.total.setText(total+"");
                    updateOrderList(orderedProductBean.getProductId(), num, total);
                    BaseActivity.sendHandler.sendEmptyMessage(BaseActivity.UPDATE_TOTAL_PRICE);
                }  else if (editable.toString().equals("")
                        && !editable.toString().equals(".")){
                    holder.num.setHint("0.0");
                    holder.total.setText("0.0");
                    updateOrderList(orderedProductBean.getProductId(), 0.0, 0.0);
                    BaseActivity.sendHandler.sendEmptyMessage(BaseActivity.UPDATE_TOTAL_PRICE);
                }
            }
        });
    }

    private void updateOrderList(String id, double num, double total) {
        for (OrderedProductBean orderedProductBean : BaseActivity.saveOrderedProductBeans) {
            if (orderedProductBean.getProductId().equals(id)) {
                orderedProductBean.setNum(num);
                orderedProductBean.setTotalPrice(total);
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderedProductBeans.size();
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
