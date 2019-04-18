package com.ucai.uvegetable.utils;

import android.hardware.Camera.Size;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraPara {
	private static final String tag = "YanZi_CameraPara";
	private CameraSizeComparator sizeComparator = new CameraSizeComparator();
	private static CameraPara myCamPara = null;
	private CameraPara(){
		
	}
	public static CameraPara getInstance(){
		if(myCamPara == null){
			myCamPara = new CameraPara();
			return myCamPara;
		}
		else{
			return myCamPara;
		}
	}
	
	public Size getPreviewSize(List<Size> list, float r, int miniPreviewWidth){
		Collections.sort(list, sizeComparator);
		
		int i = 0;
		for(Size s:list){
			if((s.width >= miniPreviewWidth) && equalRate(s, r)){
				Log.i(tag, "getPreviewSize:w = " + s.width + "h = " + s.height);
                break;
			}
			i++;
		}

		return list.get(i);
	}
	public Size getPictureSize(List<Size> list, float rate, int minWidth){
		Collections.sort(list, sizeComparator);
		
		int i = 0;
		for(Size s:list){
			if((s.width > minWidth) && equalRate(s, rate)){
				Log.i(tag, "getPictureSize:w = " + s.width + "h = " + s.height);
                break;
			}
			i++;
		}

		return list.get(i);
	}
	
	public boolean equalRate(Size s, float rate){
		float r = (float)(s.width)/(float)(s.height);
		if(Math.abs(r - rate) <= 0.2)
		{
			return true;
		}
		else{
			return false;
		}
	}
	
	public  class CameraSizeComparator implements Comparator<Size> {
		public int compare(Size lhs, Size rhs) {
			if(lhs.width == rhs.width){
			return 0;
			}
			else if(lhs.width > rhs.width){
				return 1;
			}
			else{
				return -1;
			}
		}
		
	}
}

