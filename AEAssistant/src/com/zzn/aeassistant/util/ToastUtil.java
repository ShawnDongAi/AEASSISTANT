package com.zzn.aeassistant.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.app.AEApp;

/**
 * Toast提示类
 * 
 * @author Shawn
 */
public class ToastUtil {
	private static Toast toast = null;
	
	public static void showImp(Activity activity, String msg) {
		new AlertDialog.Builder(activity).setTitle(R.string.warning)
				.setMessage(msg).setPositiveButton(R.string.confirm, null)
				.create().show();
	}
	
	public static void showImp(Activity activity, int msg) {
		new AlertDialog.Builder(activity).setTitle(R.string.warning)
				.setMessage(msg).setPositiveButton(R.string.confirm, null)
				.create().show();
	}

	public static void show(String msg) {
		cancelToast();
//		toast = Toast.makeText(AEApp.getInstance(), msg, Toast.LENGTH_SHORT);
		toast = createToast(msg);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}

	public static void show(int msg) {
		cancelToast();
//		toast = Toast.makeText(AEApp.getInstance(), msg, Toast.LENGTH_SHORT);
		toast = createToast(msg);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}

	public static void showLong(String msg) {
		cancelToast();
//		toast = Toast.makeText(AEApp.getInstance(), msg, Toast.LENGTH_LONG);
		toast = createToast(msg);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}

	public static void showLong(int msg) {
		cancelToast();
//		toast = Toast.makeText(AEApp.getInstance(), msg, Toast.LENGTH_LONG);
		toast = createToast(msg);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}

	public static void cancelToast() {
		if (toast != null) {
			toast.cancel();
			toast = null;
		}
	}

	private static Toast createToast(String msg) {
		View view = View.inflate(AEApp.getInstance(), R.layout.loading, null);
		ImageView spaceshipImage = (ImageView) view.findViewById(R.id.img);
		spaceshipImage.setVisibility(View.GONE);
		TextView tipTextView = (TextView) view.findViewById(R.id.tipTextView);
		tipTextView.setText(msg);
		Toast toast = new Toast(AEApp.getInstance());
		toast.setGravity(Gravity.CENTER, 0,
				0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(view);
		return toast;
	}
	
	private static Toast createToast(int msg) {
		View view = View.inflate(AEApp.getInstance(), R.layout.loading, null);
		ImageView spaceshipImage = (ImageView) view.findViewById(R.id.img);
		spaceshipImage.setVisibility(View.GONE);
		TextView tipTextView = (TextView) view.findViewById(R.id.tipTextView);
		tipTextView.setText(msg);
		Toast toast = new Toast(AEApp.getInstance());
		toast.setGravity(Gravity.CENTER, 0,
				0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(view);
		return toast;
	}
}
