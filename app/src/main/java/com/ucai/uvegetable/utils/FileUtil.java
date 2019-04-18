package com.ucai.uvegetable.utils;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    private static final String PARENT_PATH = "/mnt/sdcard/aPalm";
    private static final String ROI_PATH = "/mnt/sdcard/aPalm/identify";

    public static void init(){
        initSavePath();
        initRoiPath(); //jni里roi生成的文件在这个目录,所以提前app创建
    }
    public static String initSavePath(){
    /*    File dateDir = Environment.getExternalStorageDirectory();
        String path = dateDir.getAbsolutePath() + "/FaceDetect/";*/
        File folder = new File(PARENT_PATH);
        if(!folder.exists()) {
            folder.mkdir();
        }
        return PARENT_PATH;
    }

    public static void initRoiPath(){
        File folder = new File(ROI_PATH);
        if(!folder.exists()) {
            folder.mkdir();
        }
    }
    
    public static String getTempPath(){
        File f = new File(PARENT_PATH, "temp");
        if(!f.exists()){
            f.mkdir();
        }
        return f.getAbsolutePath();
    }

    public static String saveJpeg(Bitmap bm) {

        long dataTake = System.currentTimeMillis();
		String jpegName = initSavePath() + "/" + dataTake +".jpg";
//        String jpegName = initSavePath() + "/" + "temp" + ".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jpegName;
    }
}
