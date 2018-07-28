package com.ucai.uvegetable.utils;

import android.util.Log;

import com.ucai.uvegetable.beans.CategoryBean;
import com.ucai.uvegetable.beans.ProductBean;
import com.ucai.uvegetable.view.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lance
 * on 2018/7/19.
 */

public class ProductUtil {
    public static List<ProductBean> getProducts(String response, int index, boolean isHas) {
        Log.e("category", isHas+"");
        if (!isHas) {
            return getTypeProductsByIndex(response, index);
        } else {
            return getTypeProductsByIndeFromHasPriceList(response, index);
        }
    }

    private static List<ProductBean> getTypeProductsByIndex(String response, int index) {
        List<ProductBean> productBeans = new ArrayList<>();
        try {
            JSONObject resp = new JSONObject(response);
            JSONObject data = resp.getJSONArray("data").getJSONObject(index);
            JSONArray products = data.getJSONArray("products");
            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                ProductBean productBean = new ProductBean();
                productBean.setId(product.getString("id"));
                productBean.setName(product.getString("name"));
                productBean.setUnit(product.getString("unit"));
                productBean.setPrice(product.getDouble("price"));
                productBean.setImgfile(product.getString("imgfile"));
                productBean.setNote(product.getString("note"));
                productBean.setPcode(product.getString("pcode"));
                productBeans.add(productBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("categoryGET", productBeans.size()+"");
        return productBeans;
    }

    private static List<ProductBean> getTypeProductsByIndeFromHasPriceList(String response, int index) {
        List<ProductBean> productBeans = new ArrayList<>();
        try {
            JSONObject resp = new JSONObject(response);
            JSONObject data = resp.getJSONObject("data")
                    .getJSONArray("categories").getJSONObject(index);
            JSONArray products = data.getJSONArray("products");
            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                ProductBean productBean = new ProductBean();
                productBean.setId(product.getString("id"));
                productBean.setName(product.getString("name"));
                productBean.setUnit(product.getString("unit"));
                productBean.setUser_price(product.getDouble("num"));
                productBean.setPrice(product.getDouble("marketPrice"));
                productBean.setUser_price(product.getDouble("guestPrice"));
                productBean.setImgfile(product.getString("imgfile"));
                productBean.setNote(product.getString("note"));
                productBean.setPcode(product.getString("pcode"));
                productBeans.add(productBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("categoryGET", productBeans.size()+"");
        return productBeans;
    }

    private static List<ProductBean> getAllProducts(String response) {
        List<ProductBean> productBeans = new ArrayList<>();
        try {
            JSONObject resp = new JSONObject(response);
            JSONArray data = resp.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONArray products = data.getJSONObject(i).getJSONArray("products");
                for (int j = 0; j < products.length(); j++) {
                    JSONObject product = products.getJSONObject(j);
                    ProductBean productBean = new ProductBean();
                    productBean.setId(product.getString("id"));
                    productBean.setName(product.getString("name"));
                    productBean.setUnit(product.getString("unit"));
                    productBean.setPrice(product.getDouble("price"));
                    productBean.setImgfile(product.getString("imgfile"));
                    productBean.setNote(product.getString("note"));
                    productBean.setPcode(product.getString("pcode"));
                    productBeans.add(productBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return productBeans;
    }
}
