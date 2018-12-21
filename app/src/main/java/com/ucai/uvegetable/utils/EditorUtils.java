package com.ucai.uvegetable.utils;

import android.content.SharedPreferences;

/**
 * Created by Lance
 * on 2018/7/14.
 */

public class EditorUtils {
    public static void saveEditorData(SharedPreferences.Editor editor,
                                      boolean isLogin, String phone, String pwd) {
        editor.putBoolean("isLogined", isLogin);
        editor.putString("phone", phone);
        editor.putString("pwd", pwd);
        editor.commit();
    }
}
