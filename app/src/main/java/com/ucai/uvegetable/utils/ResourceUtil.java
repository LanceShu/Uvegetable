package com.ucai.uvegetable.utils;

import android.content.Context;

/**
 * Created by Lance
 * on 2018/7/14.
 */

public class ResourceUtil {
    public static String getStringFromResource (Context context, int stringId) {
        return (String) context.getResources().getText(stringId);
    }
}
