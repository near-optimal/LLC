package com.example.demo.model;

public class Coffees {
	
	private int cId;
	private String productName;
	private int price;
	private int quantity;
	
	public Coffees() {
		super();
	}

	public Coffees(int cId, String productName, int price, int quantity) {
		super();
		this.cId = cId;
		this.productName = productName;
		this.price = price;
		this.quantity = quantity;
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
