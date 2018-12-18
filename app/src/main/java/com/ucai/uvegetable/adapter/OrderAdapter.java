package com.ucai.uvegetable.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.beans.OrderedProductBean;
import com.ucai.uvegetable.utils.ToastUtil;
import com.ucai.uvegetable.view.BaseActivity;
import com.ucai.uvegetable.view.HomeToOrderActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lance
 * on 2018/7/26.
 */

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<OrderedProductBean> orderedProductBeans;
    private Context context;

    private List<OrderedProductBean> currentList;
    private static final int FOOTER_ITEM = 0;
    private static final int NORMAL_ITEM = 1;
    private View VIEW_FOOTER;
    private int total_page;
    private int current_page;
    private int CAPACITY = 20;

    public OrderAdapter(Context context, List<OrderedProductBean> orderedProductBeans) {
        this.context = context;
        this.orderedProductBeans = orderedProductBeans;
        currentList = new ArrayList<>();
        current_page = 1;
        int size = orderedProductBeans.size();
        total_page = size % CAPACITY == 0 ? size / CAPACITY : size / CAPACITY + 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NORMAL_ITEM) {
            return new ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.hto_activity_item, parent, false));
        } else {
            return new FooterViewHolder(VIEW_FOOTER);
        }
    }

    public void addFooterView(View footerView) {
        if (isHaveFooterView()) {
            throw new IllegalStateException("footerView has already exists!");
        } else {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            footerView.setLayoutParams(params);
            VIEW_FOOTER = footerView;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (!isFooterView(position)) {
            ViewHolder holder = (ViewHolder) viewHolder;
            OrderedProductBean orderedProductBean = currentList.get(position);
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
//        holder.num.setHint("0.0");
            holder.total.setText(String.valueOf(orderedProductBean.getTotalPrice()));
//        holder.total.setText("0.0");
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
        } else {
            FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
            footerViewHolder.footer_content.setText(getContent(current_page, total_page));
            footerViewHolder.footer_last.setOnClickListener(view -> {
                if (current_page == 1) {
                    ToastUtil.show(context, "已到最前一页");
                } else {
                    current_page --;
                    notifyDataSetChanged();
//                    HomeToOrderActivity.sendHandler
//                            .obtainMessage(BaseActivity.SCROLL_TO_TOP)
//                            .sendToTarget();
                }
            });
            footerViewHolder.footer_next.setOnClickListener(view -> {
                if (current_page == total_page) {
                    ToastUtil.show(context, "已到最后一页");
                } else {
                    current_page ++;
                    notifyDataSetChanged();
//                    HomeToOrderActivity.sendHandler
//                            .obtainMessage(BaseActivity.SCROLL_TO_TOP)
//                            .sendToTarget();
                }
            });
        }
    }

    private String getContent(int current_page, int total_page) {
        return current_page + "  /  " + total_page;
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
        currentList.clear();
        currentList.addAll(getCurrentOrderList(current_page, orderedProductBeans));
        int count = currentList.size();
        if (VIEW_FOOTER != null) {
            count ++;
        }
        return count;
    }

    private boolean isFooterView(int position) {
        return isHaveFooterView() && position == getItemCount() - 1;
    }

    private boolean isHaveFooterView() {
        return VIEW_FOOTER != null;
    }

    private List<OrderedProductBean> getCurrentOrderList(
            int index, List<OrderedProductBean> orderedProductBeans) {
        List<OrderedProductBean> currentList = new ArrayList<>();
        int size = orderedProductBeans.size();
        for (int i = (index - 1) * CAPACITY; i < index * CAPACITY && i < size; i ++) {
            currentList.add(orderedProductBeans.get(i));
        }
        return currentList;
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterView(position)) {
            return FOOTER_ITEM;
        } else {
            return NORMAL_ITEM;
        }
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

    static class FooterViewHolder extends RecyclerView.ViewHolder{
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
}
