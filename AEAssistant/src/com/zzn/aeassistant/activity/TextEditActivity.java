package com.zzn.aeassistant.activity;

import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.util.ToolsUtil;

public class TextEditActivity extends BaseActivity {
	private EditText editText;

	@Override
	protected int layoutResID() {
		return R.layout.activity_text_edit;
	}

	@Override
	protected int titleStringID() {
		return R.string.modify_user_name;
	}

	@Override
	protected void initView() {
		save.setVisibility(View.VISIBLE);
		save.setText(R.string.save);
		editText = (EditText) findViewById(R.id.edit_text);
		setTitle(getIntent().getStringExtra(CodeConstants.KEY_TITLE));
		editText.getText().clear();
		editText.setText(getIntent().getStringExtra(
				CodeConstants.KEY_DEFAULT_TEXT));
		editText.setHint(getIntent()
				.getStringExtra(CodeConstants.KEY_HINT_TEXT));
		boolean singleLine = getIntent().getBooleanExtra(
				CodeConstants.KEY_SINGLELINE, true);
		if (singleLine) {
			editText.setSingleLine(true);
			ToolsUtil.setTextMaxLength(editText, 50);
		} else {
			editText.setMinLines(8);
			ToolsUtil.setTextMaxLength(editText, 1000);
		}
		if (getIntent().hasExtra(CodeConstants.KEY_INPUT_TYPE)) {
			int inputType = getIntent().getIntExtra(
					CodeConstants.KEY_INPUT_TYPE,
					InputType.TYPE_TEXT_VARIATION_PERSON_NAME
							| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			editText.setInputType(inputType);
		}
		editText.setSelection(editText.getText().toString().length());
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	@Override
	protected void onSaveClick() {
		super.onSaveClick();
		String result = editText.getText().toString().trim();
		if (StringUtil.isEmpty(result)) {
			ToastUtil.show(R.string.null_input);
			return;
		}
		Intent intent = new Intent();
		intent.putExtra(CodeConstants.KEY_TEXT_RESULT, result);
		setResult(RESULT_OK, intent);
		finish();
	}

}
