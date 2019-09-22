package com.jac.picplace.controller.viewstate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ThumbnailDisplaySettings {

	
	@Value("${thumbnail.display.height}") private int height;
	@Value("${thumbnail.display.width}") private int width;

	public ThumbnailDisplaySettings() {
		// TODO Auto-generated constructor stub
	}
	
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}


}
