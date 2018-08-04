package com.ucai.uvegetable.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
        Glide.with(context)
                .load("http://123.206.13.129:8080/manage/" + productBean.getImgfile())
                .skipMemoryCache(false)
                .override(200, 200)
                .into(holder.productImage);
        holder.productName.setText(productBean.getName());
        String productUnit = " 元/" + productBean.getUnit();
        String userPrice = productBean.getUser_price() == 0.0 ? "--" : productBean.getUser_price()+"";
        holder.productPrice.setText("市场价： " + productBean.getPrice()
                + productUnit + "\n优惠价： " + userPrice + productUnit);
        holder.productImage.setOnClickListener((view -> {
            Glide.with(context)
                    .load("http://123.206.13.129:8080/manage/" + productBean.getImgfile())
                    .skipMemoryCache(false)
                    .override(200, 200)
                    .into(holder.productImage);
        }));
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

        ViewHolder(View view) {
            super(view);
            productItemLayout = view.findViewById(R.id.product_item_layout);
            productImage = view.findViewById(R.id.product_image);
            productName = view.findViewById(R.id.product_name);
            productPrice = view.findViewById(R.id.product_price);
        }
    }
}
