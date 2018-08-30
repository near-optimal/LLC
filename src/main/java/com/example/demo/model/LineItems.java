package com.example.demo.model;

public class LineItems {
	
	private int liId;
	private String oId;
	private int cId;
	private String productName;
	private int price;
	private int quantity;
	
	public LineItems() {
		super();
	}

	public LineItems(int liId, String oId, int cId, String productName, int price, int quantity) {
		super();
		this.liId = liId;
		this.oId = oId;
		this.cId = cId;
		this.productName = productName;
		this.price = price;
		this.quantity = quantity;
	}

	public int getLiId() {
		return liId;
	}

	public void setLiId(int liId) {
		this.liId = liId;
	}

	public String getoId() {
		return oId;
	}

	public void setoId(String oId) {
		this.oId = oId;
	}

	public int getcId() {
		return cId;
	}

	public void setcId(int cId) {
		this.cId = cId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
