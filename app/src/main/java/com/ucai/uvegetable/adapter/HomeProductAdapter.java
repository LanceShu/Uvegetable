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
import com.ucai.uvegetable.utils.ToastUtil;
import com.ucai.uvegetable.view.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lance
 * on 2018/7/21.
 */

public class HomeProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ProductBean> productBeanList;
    private List<ProductBean> currentList;
    private int preSize;
    private final static int NORMAL_ITEM_VIEW = 1;
    private final static int FOOT_ITEM_VIEW = 0;
    private int CAPACITY = 20;

    private View VIEW_FOOTER;
    private int totalPage;
    private int currectPage;

    public HomeProductAdapter(Context context, List<ProductBean> productBeanList) {
        this.context = context;
        this.productBeanList = productBeanList;
        currentList = new ArrayList<>();
        currectPage = 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (!isFooterView(position)) {
            return NORMAL_ITEM_VIEW;
        } else {
            return FOOT_ITEM_VIEW;
        }
    }

    private boolean isFooterView(int position) {
        return haveFooterView() && position == getItemCount() - 1;
    }

    private boolean haveFooterView() {
        return VIEW_FOOTER != null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOT_ITEM_VIEW) {
            return new FooterViewHolder(VIEW_FOOTER);
        } else {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.product_item, parent, false);
            return new NormalViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (!isFooterView(position)) {
            ProductBean productBean = currentList.get(position);
            NormalViewHolder holder = (NormalViewHolder) viewHolder;
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
        } else if (isFooterView(position)){
            FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
            footerViewHolder.footer_content.setText(getContent(currectPage, totalPage));
            footerViewHolder.footer_last.setOnClickListener(view -> {
                if (currectPage != 1) {
                    currectPage --;
                    notifyDataSetChanged();
                    BaseActivity.sendHandler
                            .obtainMessage(BaseActivity.SCROLL_TO_TOP).sendToTarget();
                } else {
                    ToastUtil.show(context, "已到最前一页");
                }
            });
            footerViewHolder.footer_next.setOnClickListener(view -> {
                if (currectPage != totalPage) {
                    currectPage ++;
                    notifyDataSetChanged();
                    BaseActivity.sendHandler
                            .obtainMessage(BaseActivity.SCROLL_TO_TOP).sendToTarget();
                } else {
                    ToastUtil.show(context, "已到最后一页");
                }
            });

        }
    }

    private String getContent(int currectPage, int totalPage) {
        return currectPage + "  /  " + totalPage;
    }

    @Override
    public int getItemCount() {
        if (productBeanList.size() != preSize) {
            currectPage = 1;
            int count = (productBeanList == null ? 0 : productBeanList.size());
            totalPage = count % CAPACITY == 0 ? count / CAPACITY : count / CAPACITY + 1;
            preSize = productBeanList.size();
        }
        currentList.clear();
        currentList.addAll(getCurrentPageProducts(currectPage, productBeanList));
        int size = currentList.size();
        if (VIEW_FOOTER != null) {
            size ++;
        }
        return size;
    }

    private List<ProductBean> getCurrentPageProducts(int index, List<ProductBean> productBeans) {
        List<ProductBean> currentList = new ArrayList<>();
        for (int i = (index - 1) * CAPACITY;
             i < index * CAPACITY && i < productBeans.size(); i++) {
            currentList.add(productBeans.get(i));
        }
        return currentList;
    }

    public void addFooterView(View footerView) {
        if (haveFooterView()) {
            throw new IllegalStateException("footerView has already exists!");
        } else {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            footerView.setLayoutParams(params);
            VIEW_FOOTER = footerView;
//            notifyItemInserted(getItemCount() - 1);
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        private TextView footer_content;
        private ImageView footer_last;
        private ImageView footer_next;

        FooterViewHolder(View view) {
            super(view);
            footer_content = view.findViewById(R.id.footer_content);
            footer_last = view.findViewById(R.id.footer_last);
            footer_next = view.findViewById(R.id.footer_next);
        }
    }

    static class NormalViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout productItemLayout;
        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;

        NormalViewHolder(View view) {
            super(view);
            productItemLayout = view.findViewById(R.id.product_item_layout);
            productImage = view.findViewById(R.id.product_image);
            productName = view.findViewById(R.id.product_name);
            productPrice = view.findViewById(R.id.product_price);
        }
    }
}
