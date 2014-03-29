package com.weicai.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class PhoneTextWatcher implements TextWatcher {
	EditText editText;
	public PhoneTextWatcher(EditText editText){
		this.editText = editText;
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	private int phone_length;

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String phone = editText.getText().toString();
		
		// 如果当前长度小于数组长度,认为使用退格
		if (s.length() < phone_length) {

			if (phone.length() == 0) {
				return;
			}

			// 光标所在位置
			editText.setSelection(phone.length());
			int index = editText.getSelectionStart();
			// 前一个字符
			char beforeStr = phone.charAt(index - 1);
			// 如果是分隔符,删除分隔符前一个
			if (beforeStr == ' ') {
				StringBuffer phone_ = new StringBuffer(phone);
				phone_.deleteCharAt(index - 1);
				editText.setText(phone_.toString());
			}
		}

		phone = editText.getText().toString().replace(" ", "");
		StringBuffer phone_ = new StringBuffer(phone);
		if (phone_.length() > 3) {
			phone_.insert(3, ' ');
		}
		if (phone_.length() > 8) {
			phone_.insert(8, ' ');
		}
		if (!phone_.toString().equals(editText.getText().toString())) {
			editText.setText(phone_.toString());
			editText.setSelection(phone_.length());
		}

		phone_length = editText.getText().toString().length();
	}
}