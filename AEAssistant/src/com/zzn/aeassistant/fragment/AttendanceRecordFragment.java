package com.zzn.aeassistant.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.attendance.SumByProjectActivity;
import com.zzn.aeassistant.activity.attendance.SumByUsersActivity;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.util.ToastUtil;

public class AttendanceRecordFragment extends BaseFragment {
	private Button startDateBtn, endDateBtn, sumByUserBtn, sumByProjectBtn;
	private DatePickerDialog datePicker;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected int layoutResID() {
		return R.layout.activity_attendance_record;
	}

	@Override
	protected void initView(View container) {
		startDateBtn = (Button) container.findViewById(R.id.sum_start_date);
		endDateBtn = (Button) container.findViewById(R.id.sum_end_date);
		sumByUserBtn = (Button) container.findViewById(R.id.sum_by_user);
		sumByProjectBtn = (Button) container.findViewById(R.id.sum_by_project);
		Calendar now = Calendar.getInstance();
		String currentDate = dateFormat.format(now.getTime());
		startDateBtn.setText(currentDate);
		endDateBtn.setText(currentDate);
		startDateBtn.setOnClickListener(this);
		endDateBtn.setOnClickListener(this);
		sumByUserBtn.setOnClickListener(this);
		sumByProjectBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.sum_start_date:
			selectDate(startDateBtn);
			break;
		case R.id.sum_end_date:
			selectDate(endDateBtn);
			break;
		case R.id.sum_by_user:
			if (!checkDate()) {
				break;
			}
			Intent userIntent = new Intent(mContext, SumByUsersActivity.class);
			userIntent.putExtra(CodeConstants.KEY_START_DATE, startDateBtn.getText().toString());
			userIntent.putExtra(CodeConstants.KEY_END_DATE, endDateBtn.getText().toString());
			startActivity(userIntent);
			break;
		case R.id.sum_by_project:
			if (!checkDate()) {
				break;
			}
			Intent proIntent = new Intent(mContext, SumByProjectActivity.class);
			proIntent.putExtra(CodeConstants.KEY_START_DATE, startDateBtn.getText().toString());
			proIntent.putExtra(CodeConstants.KEY_END_DATE, endDateBtn.getText().toString());
			startActivity(proIntent);
			break;
		}
	}
	
	private boolean checkDate() {
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		try {
			startDate.setTime(dateFormat.parse(startDateBtn.getText().toString()));
			endDate.setTime(dateFormat.parse(endDateBtn.getText().toString()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		boolean result = !startDate.after(endDate);
		if (!result) {
			ToastUtil.show(R.string.startdate_after_enddate);
		}
		return result;
	}

	private void selectDate(final Button button) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(button.getText().toString()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (datePicker != null) {
			datePicker.dismiss();
			datePicker = null;
		}
		datePicker = new DatePickerDialog(mContext, new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, monthOfYear, dayOfMonth);
				button.setText(dateFormat.format(calendar.getTime()));
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		datePicker.setTitle(R.string.select_date);
		datePicker.show();
	}
}
