package com.ucai.uvegetable.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtil {
	/**
	 * ����dipתΪpx
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue){
		final float scale = context.getResources().getDisplayMetrics().density;                 
		return (int)(dipValue * scale + 0.5f);         
	}            
	/**
	 * ����pxתΪdip
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;                 
		return (int)(pxValue / scale + 0.5f);         
	} 
	/**
	 * ��ѯ��Ļ�Ŀ��
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context){
		DisplayMetrics dm =context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}
	/**
	 * ��ѯ��Ļ�ĸ߶�
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context){
		DisplayMetrics dm =context.getResources().getDisplayMetrics();
		return  dm.heightPixels;

	}
}
