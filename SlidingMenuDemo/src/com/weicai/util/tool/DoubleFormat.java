package com.weicai.util.tool;

import java.text.DecimalFormat;

public class DoubleFormat {

	public static String format(double data) {
		DecimalFormat df = new DecimalFormat();
		String style = "0.00";//定义要显示的数字的格式
		df.applyPattern(style);// 将格式应用于格式化器
		return df.format(data);
	}
}
