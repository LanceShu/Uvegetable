package com.ucai.uvegetable.utils;

import android.graphics.Bitmap;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.ucai.uvegetable.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

/**
 * 功能：
 *
 * @author yanzi E-mail: yangq@rd.netease.com
 * @version 创建时间: 2016-06-04 下午5:13
 */
public class Utils {

    private static String getParams(Map<String, Object> params){
        if(params == null){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Object> e : params.entrySet()){
            sb.append(e.getKey());
            sb.append("=");
            sb.append(e.getValue());
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    public static String paraseUrl(String url, Map<String, Object> params){
        String url2 = url;
        if(!url.endsWith("?") && !url.endsWith("#")){
            url2+="?";
        }
        return url2 + getParams(params);
    }

    public static DisplayImageOptions getPalmDisplayOptions(){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.big_image_loading)
                .showImageForEmptyUri(R.mipmap.big_image_loading)
                .showImageOnFail(R.mipmap.big_image_loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new RoundedBitmapDisplayer(30))
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        return options;
    }

    /**
     * 将double类型的多位保留3位有效数字
     * @param d
     * @return
     */
    public static float formatDouble(double d){
        NumberFormat format = new DecimalFormat("0.000");
        String dd = format.format(d);
        return Float.parseFloat(dd);
    }

    public static String getCertifyResult(float diff, long time){
        StringBuilder builder = new StringBuilder();
        builder.append("差异度 =" + diff)
                .append("耗时 = " + time + "毫秒");
        return builder.toString();
    }

    public static boolean isCertifySuccess(float diff){
        return diff <= Constant.THRESHOLD;
    }
}
