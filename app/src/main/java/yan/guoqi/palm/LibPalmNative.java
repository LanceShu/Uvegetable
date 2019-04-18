package yan.guoqi.palm;

public class LibPalmNative {
	
	static {
		System.loadLibrary("PalmNative");
	}
	
	public static native float PalmIdentify(String train, String test);
	public static native void CreateEncodeXml(String p);
	public static native int PalmTrain(int nPerson);
	public static native int shibie(String perScop, String testpath);
	public static native void DetectKey(String p);
	public static native String getRoi(String path);
	//public static native int testGetRoi(String path);

}
