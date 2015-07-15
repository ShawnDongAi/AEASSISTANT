package com.zzn.aenote.http.youtu;

import org.json.JSONObject;

import com.youtu.Youtu;

public class YoutuUtil {
	public static final String APP_ID = "10001571";
	public static final String SECRET_ID = "AKIDQkIoJtcHAqMyPxfAncyh5vzPbKEPNdOE";
	public static final String SECRET_KEY = "fkuUqEUtzmfpDAMj62B0t0OVj1epVNux";

	public static boolean detectFace(String photoPath) {
		try {
			Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY);
			JSONObject respose = faceYoutu.DetectFace(photoPath);
			// get respose
			System.out.println(respose);
			// get detail info
			if (respose.getInt("errorcode") == 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
