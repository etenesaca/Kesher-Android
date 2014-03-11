package com.kemas.item.adapters;

public class AreasItem {
	private String logo;
	private String title;

	public AreasItem() {
		super();
	}
	
	

	public AreasItem(String image, String title) {
		super();
		this.logo = image;
		this.title = title;
	}

	public String getImage() {
		return logo;
	}

	public void setImage(String image) {
		this.logo = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
