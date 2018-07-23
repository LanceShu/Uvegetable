package com.ucai.uvegetable.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.beans.ProductBean;

import java.util.List;

/**
 * Created by Lance
 * on 2018/7/21.
 */

public class HomeProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ProductBean> productBeanList;

    public HomeProductAdapter(Context context, List<ProductBean> productBeanList) {
        this.context = context;
        this.productBeanList = productBeanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ProductBean productBean = productBeanList.get(position);
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.productImage.setImageResource(R.drawable.me_order);
        holder.productName.setText(productBean.getName());
        String productUnit = " 元/" + productBean.getUnit();
        String userPrice = productBean.getUser_price() == 0.0 ? "--" : productBean.getUser_price()+"";
        holder.productPrice.setText("市场价：" + productBean.getPrice()
                + productUnit + "\n会员价：" + userPrice + productUnit);
        holder.productMinus.setOnClickListener((view -> {
            int num = Integer.valueOf(holder.productNumber.getText().toString());
            if (num != 0) {
                num --;
            }
            holder.productNumber.setText(num+"");
        }));
        holder.productPlus.setOnClickListener((view -> {
            int num = Integer.valueOf(holder.productNumber.getText().toString());
            num ++;
            holder.productNumber.setText(num+"");
        }));
        holder.productNumber.setText("0");
    }

    @Override
    public int getItemCount() {
        return productBeanList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout productItemLayout;
        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;
        private TextView productMinus;
        private EditText productNumber;
        private TextView productPlus;

        ViewHolder(View view) {
            super(view);
            productItemLayout = view.findViewById(R.id.product_item_layout);
            productImage = view.findViewById(R.id.product_image);
            productName = view.findViewById(R.id.product_name);
            productPrice = view.findViewById(R.id.product_price);
            productMinus = view.findViewById(R.id.product_minus);
            productNumber = view.findViewById(R.id.product_number);
            productPlus = view.findViewById(R.id.product_plus);
        }
    }
}
