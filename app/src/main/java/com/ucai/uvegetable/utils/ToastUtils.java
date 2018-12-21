package com.ucai.uvegetable.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Lance
 * on 2018/7/14.
 */

public class ToastUtils {
    public static void show (Context context, int stringId) {
        Toast.makeText(context, ResourceUtils.getStringFromResource(context, stringId), Toast.LENGTH_SHORT).show();
    }

    public static void show (Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
