package com.ucai.uvegetable.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucai.uvegetable.R;
import com.ucai.uvegetable.utils.CameraPara;
import com.ucai.uvegetable.utils.DisplayUtil;
import com.ucai.uvegetable.utils.FileUtil;
import com.ucai.uvegetable.view.MePalmListActivity;
import com.ucai.uvegetable.widget.SVDraw;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import yan.guoqi.palm.LibPalmNative;


public class PhotoActivity extends Activity implements SurfaceHolder.Callback, AutoFocusCallback, PreviewCallback {
	/** Called when the activity is first created. */
	private static final String TAG = "PhotoActivity";
	private static final String KEY_FROM = "_key_from";
	public static final String KEY_IMAGE_PATH = "_key_image_path";

	public static final int REQUEST_ADD = 0;
	public static final int REQUEST_CERTIFY = 1;

	public TextView infoTV = null;
	public String infoShow = "欢迎您采集掌纹！";
	boolean isDisOk = false;
	private static final int msgKey = 1;
	private static final float RATE = 1.33333f; 
	private  int preview_width, preview_height, picture_widht, picture_height;

	private String currFile = "";
	SurfaceView mySurfaceView;
	SurfaceHolder mySurfaceHolder;

	ImageView mPhotoImgBtn;
	Camera myCamera;
	Camera.Parameters myParameters;
	boolean isPreview = false;
	Bitmap mBitmap;
	int cntSave = 0;

	private SVDraw mDraw;
	private PalmTask mPalmTask;
    private int mFrome;

	/********Go按键的长按，聚焦分析ROI监听***********/
	private final OnLongClickListener mImgBtnGoOnLong = new OnLongClickListener(){
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			if(myCamera != null) {
			  myCamera.setOneShotPreviewCallback(PhotoActivity.this);
//				myCamera.startPreview();				
//				myCamera.autoFocus(TestPhotoActivity.this);
			}
			return true;
		}
	};

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        onReadIntent();
		//设置全屏无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		Window myWindow = this.getWindow();
		myWindow.setFlags(flag, flag);
		setContentView(R.layout.test_photo_layout); //设置布局
		initPreviewRoot();
		infoTV = findViewById(R.id.disTextView);

		mySurfaceView = findViewById(R.id.mySurfaceView);
		mySurfaceView.setZOrderOnTop(false);
		//mySurfaceHolder.setFormat(PixelFormat.TRANSPARENT);

		mySurfaceHolder = mySurfaceView.getHolder();
		mySurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mySurfaceHolder.addCallback(this);

		mDraw = findViewById(R.id.mDraw);
		
		//拍照按键
		mPhotoImgBtn = findViewById(R.id.imgBtnGo);
		//手动设置拍照ImageButton的大小为120×120,原图片大小是64×64
		LayoutParams lp = mPhotoImgBtn.getLayoutParams();
		lp.width = DisplayUtil.dip2px(getApplicationContext(), 50);
		lp.height = DisplayUtil.dip2px(getApplicationContext(), 50);
		mPhotoImgBtn.setLayoutParams(lp);

		mPhotoImgBtn.setOnLongClickListener(mImgBtnGoOnLong);
		mPhotoImgBtn.setOnClickListener(new PhotoOnClickListener());
        mPhotoImgBtn.setOnTouchListener(new MyOnTouchListener());
		
        /******启用更新线程******/
		new Thread(new UpdateThread()).start();
		new Handler().postDelayed(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				mDraw.drawGuideLines();
			}
		}, 500);

//		//按此键截屏
//		Button takeShotBtn = (Button)findViewById(R.id.takeShotBtn);
//		takeShotBtn.setOnClickListener(new OnClickListener() {
//			public void onClick(View arg0) {
////				Bitmap shotBitmap = ScreenShot.takeScreenShot(PhotoActivity.this);
////				saveJpeg(shotBitmap, "/mnt/sdcard/aPalm/shot/");
//
//			}
//		});
	}

    private void onReadIntent(){
        mFrome = getIntent().getIntExtra(KEY_FROM, Source.ADD_PALM);
    }
	
	private void initPreviewRoot(){
	    FrameLayout previewRoot = findViewById(R.id.preview_root);
	    android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) previewRoot.getLayoutParams();
	    int w = DisplayUtil.getScreenWidth(getApplicationContext());
	    int h = (int)(RATE * w);
	    params.height = h;
	    previewRoot.setLayoutParams(params);
	}

	ShutterCallback myShutterCallback = new ShutterCallback() {
		public void onShutter() {
			// TODO Auto-generated method stub
		}
	};

//	PictureCallback myRawCallback = new PictureCallback() {
//
//		public void onPictureTaken(byte[] data, Camera camera) {
//			// TODO Auto-generated method stub
//		}
//	};

	PictureCallback myJpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub		
			if(data != null){
				//data是字节数据，将其解析成位图
				mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				myCamera.stopPreview();
				isPreview = false;
			}
			//720 540
			Bitmap sizeBitmap = Bitmap.createScaledBitmap(mBitmap, getPreviewWidth()
					, getPreviewHeight(), true);
			// 掌纹图片的路径;
			String path = FileUtil.saveJpeg(sizeBitmap);
			// 回到上一级Activity;
            goToMainActivity(path);
			//再次进入预览
			/*myCamera.startPreview();
			isPreview = true;*/
		}
	};
	

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		initCamera();
		Log.i(TAG, "SurfaceHolder.Callback:surfaceChanged!");
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		myCamera = Camera.open();
		try {
			myCamera.setPreviewDisplay(mySurfaceHolder);
			Log.i(TAG, "SurfaceHolder.Callback: surfaceCreated!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(null != myCamera){
				myCamera.release();
				myCamera = null;
			}
			e.printStackTrace();
		}

	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		if(null != myCamera)
		{
			myCamera.setOneShotPreviewCallback(null);
			//myCamera.setPreviewCallback(null); /*在启动PreviewCallback时这个必须在前不然退出出错。
			//这里实际上注释掉也没关系*/			
			myCamera.stopPreview(); 
			isPreview = false; 
			myCamera.release();
			myCamera = null;     
		}
	}

	//初始化摄像头
	public void initCamera()
	{
		if(isPreview)
		{
			myCamera.stopPreview();
		}
		if(null != myCamera)
		{
			myParameters = myCamera.getParameters();
			myParameters.setPictureFormat(PixelFormat.JPEG);
			List<Size> previewSizes =myParameters.getSupportedPreviewSizes();
			Size size = CameraPara.getInstance().getPreviewSize(previewSizes, RATE, 600);
			myParameters.setPreviewSize(size.width, size.height); //720, 540
			
			List<Size> pictureSizes = myParameters.getSupportedPictureSizes();
            Size size2 = CameraPara.getInstance().getPictureSize(pictureSizes, RATE, 1000);
            myParameters.setPictureSize(size.width, size.height); // 1280 960比例也是1.3   2048, 1152。1280, 720
			myParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			myCamera.setDisplayOrientation(90); 
			myCamera.setParameters(myParameters);
			
			myCamera.startPreview();
			isPreview = true;
		}
	}

	/****拍照按键的监听*****/
	public class PhotoOnClickListener implements OnClickListener {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(isPreview && myCamera != null){
				myCamera.takePicture(myShutterCallback, null, myJpegCallback);	
			}
		}
	}

	/**给定savePath，生成一个名字**/
	public String createName(String path){
		int  b =0, c=1;
		File fTest = new File(path + b + c + ".jpg");
		while(fTest.exists()){
			if(c==9){
				b++;
				c = 0;
			}
			else
				c++;
			if(b==9 && c==9){    					
				b = 0;
				c = 1;
			}
			fTest = new File(path + b + c + ".jpg");
		}
		String name = path + b + c +".jpg";
		return name;
	}

//    // 给定一个Bitmap，进行保存
//	public void saveJpeg(Bitmap bm, String path){
//		File folder = new File(path);
//		if(!folder.exists()) //如果文件夹不存在则创建
//		{
//			folder.mkdir();
//		}
//		//long dataTake = System.currentTimeMillis();
//		String jpegName = createName(path);
//
//		try {
//			FileOutputStream fout = new FileOutputStream(jpegName);
//			BufferedOutputStream bos = new BufferedOutputStream(fout);
//
//			//			//如果需要改变大小(默认的是宽960×高1280),如改成宽600×高800
//			//			Bitmap newBM = bm.createScaledBitmap(bm, 600, 800, false);
//
//			bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//			bos.flush();
//			bos.close();
//			cntSave++;
//			currFile = jpegName;
//			Toast.makeText(PhotoActivity.this, "您拍的第"+cntSave+"张"+jpegName,
//					Toast.LENGTH_SHORT).show();
//
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	/********更新提示栏的handler*******/
	@SuppressLint("HandlerLeak")
	private Handler updateHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if( isDisOk){
				infoTV.setText("边长/角度 =  " + infoShow+"\n");
				String[] infoSplit = infoShow.split("/");
				int dis = Integer.parseInt(infoSplit[0]);
				if(dis <= 200) {
					infoTV.append("Tips：太短，请调整距离！");
				} else if(dis < 220) {
					infoTV.append("Tips： 可能太短！");
				} else if(dis < 255) {
					infoTV.append("Tips：合适，请采集！");
				} else {
					infoTV.append("Tips：太长，请调整！");
				}

			} else {
				infoTV.setText("边长/角度  = ..."+"\n" );
				infoTV.append("Tips: 欢迎您采集掌纹！");
			}
		}


	};

	/*******更新提示栏的线程类*********/
	public class UpdateThread implements Runnable {
		public void run() {
			// TODO Auto-generated method stub
			for (;;)
			{
				updateHandler.sendEmptyMessage(0);
				try {
					Thread.sleep(1000);   //1秒刷新一次
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**************PalmTask，用来获取previewFrame，并解析关键点******************/
	private class PalmTask extends AsyncTask<Void, Void, Void> {

		private byte[] mData;			
		/*构造函数*/
		PalmTask(byte[] data)
		{
			mData = data;
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			int n  = 0;
			Size size = myParameters.getPreviewSize();
			final int w = size.width; //mySurfaceView.getWidth();
			final int h = size.height; //mySurfaceView.getHeight();
			final YuvImage image = new YuvImage(mData, ImageFormat.NV21, w, h, null);
			ByteArrayOutputStream os = new ByteArrayOutputStream(mData.length);
			if(!image.compressToJpeg(new Rect(0,0, w, h), 100, os))
			{
				return null;
			}
			byte[] tmp = os.toByteArray();
			Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
			
			File tmpFile = new File(FileUtil.getTempPath() + File.separator +"xxx" + n + ".JPG");
			try {
				FileOutputStream fos1 = new FileOutputStream(tmpFile);

				BufferedOutputStream bos1 = new BufferedOutputStream(fos1);
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos1);
				//bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				bos1.flush();
				bos1.close();			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i(TAG,"预览分析：" + tmpFile.getAbsolutePath());
			LibPalmNative.DetectKey(tmpFile.getAbsolutePath());
			getKey();	
			Log.i(TAG, "getKey运行结束！");

			return null;
		}
	}

	/***********解析key.xml,从jni层得到关键点数据********/
	public void getKey(){
		isDisOk = false;
		/*首先解析/mnt/sdcard/aPalm/key.xml文件*/
		//SAXBuilder builder = new SAXBuilder();
		float wWidth = mySurfaceView.getWidth();
		float wHeight = mySurfaceView.getHeight();
		int width = getPreviewWidth();
		int height = getPreviewHeight();
		Log.i(TAG, "getPreviewWidth = " + getPreviewWidth() + " getPreviewHeight = " + getPreviewHeight());
		float sx = wWidth/height;
		float sy = wHeight/width;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		List<Point> points = new ArrayList<>(3);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File(
					"/mnt/sdcard/aPalm/temp/key.xml"));
			Element rootElement = document.getDocumentElement();
			//Element element = rootElement.getElementsByTagName(name)
			NodeList nodes = rootElement.getElementsByTagName("data");
			//Log.i(TAG, "nodes的长度"+nodes.getLength());
			String tmp = nodes.item(0).getFirstChild().getNodeValue();
			Log.i(TAG, tmp);
			if(!tmp.isEmpty())
			{
				String tmp1[] = tmp.trim().split(" ");
				for(int i=0; i<3; i++)
				{
					String subX = tmp1[i].substring(0, tmp1[i].length()-1);
					String subY = tmp1[i+3].substring(0, tmp1[i+3].length()-1);
					int x = (int)((getPreviewHeight() - Integer.parseInt(subY)) * sx);
					int y = (int)(Integer.parseInt(subX)*sy);
					Log.i(TAG, i + "个关键点－－－反转前：x ＝ "+ Integer.parseInt(subX) + " y ＝ " + Integer.parseInt(subY));
					Point p = new Point(x, y);
					points.add(p);
					Log.i(TAG, i + "个关键点---反转后,  x = " + p.x + " y = " +p.y);
					}
				}
		} catch (Exception e) {
		}
		mDraw.setVisibility(View.VISIBLE);
		infoShow = mDraw.drawPalmRoi(points.get(0), points.get(1), points.get(2));  		
		isDisOk = true;		

	}

	// 获取拍照时的宽度与高度;
	private int getPreviewWidth(){
	    Size size = myCamera.getParameters().getPreviewSize();
	    return size.width;
	}

	private int getPreviewHeight(){
	    Size size = myCamera.getParameters().getPreviewSize();
	    return size.height;
	}

//	/****判断文件夹是否存在，如果不存在则创建****/
//	public boolean isFolderExist(String folderPath){
//		boolean result = false;
//		File f = new File(folderPath);
//		if(!f.exists()){
//			if(f.mkdirs()){
//				result = true;
//			}
//			else
//				result = false;
//		}
//		else
//			result = true;
//		return result;
//	}

	@Override
	public void onBackPressed() {
		Intent backIntent = new Intent(PhotoActivity.this,
				MePalmListActivity.class);
		Bundle bundle = new Bundle();
		if(cntSave >= 1 && !currFile.isEmpty())
			bundle.putString("send", currFile);
		else
			bundle.putString("send", "ERROR");
		backIntent.putExtras(bundle);
		PhotoActivity.this.setResult(1, backIntent);
		PhotoActivity.this.finish();
		//super.onBackPressed();
	}

    private void goToMainActivity(String path){
        Intent intent = new Intent();
        intent.putExtra(KEY_IMAGE_PATH, path);
        setResult(RESULT_OK, intent);
        finish();
    }

	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		if(mPalmTask != null){
			switch(mPalmTask.getStatus()){
			case RUNNING:
				return;
			case PENDING:
				mPalmTask.cancel(false);
				break;
			}
		}
		mPalmTask = new PalmTask(data);
		mPalmTask.execute((Void)null);
	}

	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub
		if(myCamera != null){
			if(success){
				myCamera.setOneShotPreviewCallback(this);
			}
			else{
				myCamera.autoFocus(this);
			}
		}
	}
	
//	/*截屏*/
//	public void saveScreenShot(){
//		Bitmap b = Bitmap.createBitmap(PhotoActivity.this.getWindow(), getHeight(), Bitmap.Config.ARGB_8888);
//		
//				}

	/*为了使图片按钮按下和弹起状态不同，采用过滤颜色的方法.按下的时候让图片颜色变淡*/
	public static class MyOnTouchListener implements OnTouchListener {
		final  float[] BT_SELECTED=new float[]
				{ 2, 0, 0, 0, 2,
				  0, 2, 0, 0, 2,
				  0, 0, 2, 0, 2,
				  0, 0, 0, 1, 0 };

		final float[] BT_NOT_SELECTED=new float[]
				{ 1, 0, 0, 0, 0,
				  0, 1, 0, 0, 0,
				  0, 0, 1, 0, 0,
				  0, 0, 0, 1, 0 };

		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if(event.getAction() == MotionEvent.ACTION_DOWN){
//				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
//				v.setBackgroundDrawable(v.getBackground());
				v.setBackgroundResource(R.mipmap.take_photo_btn_down);
			}
			else if(event.getAction() == MotionEvent.ACTION_UP){
//				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
//				v.setBackgroundDrawable(v.getBackground());
				v.setBackgroundResource(R.mipmap.take_photo_btn_up);
			}
			return false;
		}
	}

	public interface Source{
		int ADD_PALM = 0;
		int CERTIFY_PALM = 1;
	}

    public static void goToPhotoActivity(Activity activity, int source, int requestCode){
        Intent intent = new Intent(activity, PhotoActivity.class);
        intent.putExtra(KEY_FROM, source);
        activity.startActivityForResult(intent, requestCode);
    }
}
