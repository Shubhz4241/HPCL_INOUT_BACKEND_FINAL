package com.hpcl.inout.dto;

import com.hpcl.inout.entity.Visitor;

public class VisitorRequest 
{
	private Visitor visitor;
	private String imgData;

	public Visitor getVisitor() {
		return visitor;
	}

	public void setVisitor(Visitor visitor) {
		this.visitor = visitor;
	}

	public String getImgData() {
		return imgData;
	}

	public void setImgData(String imgData) {
		this.imgData = imgData;
	}
}

