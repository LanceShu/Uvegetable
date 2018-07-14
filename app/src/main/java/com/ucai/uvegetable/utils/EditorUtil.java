package com.ucai.uvegetable.utils;

import com.ucai.uvegetable.view.BaseActivity;

/**
 * Created by Lance
 * on 2018/7/14.
 */

public class EditorUtil {
    public static void saveEditorData(boolean isLogin, String phone, String pwd) {
        BaseActivity.editor.putBoolean("isLogined", isLogin);
        BaseActivity.editor.putString("phone", phone);
        BaseActivity.editor.putString("pwd", pwd);
        BaseActivity.editor.commit();
    }
}
