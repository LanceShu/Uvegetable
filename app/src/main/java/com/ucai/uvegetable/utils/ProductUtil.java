package com.ucai.uvegetable.utils;

import android.util.Log;

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
    public static List<String> getCategoryList(String response) {
        List<String> categories = new ArrayList<>();
        try {
            JSONObject resp = new JSONObject(response);
            JSONArray data = resp.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject catedata = data.getJSONObject(i);
                String category = catedata.getString("categoryName");
                categories.add(category);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return categories;
    }
}
