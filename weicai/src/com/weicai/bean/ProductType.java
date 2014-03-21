package com.weicai.bean;

public class ProductType {

	private String type;
	private String type_;
	private String[][] classifies;
	
	public ProductType(String type, String type_, String[][] classifies){
		this.type = type;
		this.type_ = type_;
		this.classifies = classifies;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType_() {
		return type_;
	}

	public void setType_(String type_) {
		this.type_ = type_;
	}

	public String[][] getClassifies() {
		return classifies;
	}

	public void setClassifies(String[][] classifies) {
		this.classifies = classifies;
	}
	
}
