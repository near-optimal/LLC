package com.example.demo.model;

import java.util.List;

public class Orders {
	
	private String oId;
	private int uId;
	private int totalPrice;
	private List<LineItems> lineItems;
	private String date;
	private String arrivalTime;

	public Orders() {
		super();
	}

	public Orders(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Orders(String oId, int uId, int totalPrice, List<LineItems> lineItems, String date, String arrivalTime) {
		super();
		this.oId = oId;
		this.uId = uId;
		this.totalPrice = totalPrice;
		this.lineItems = lineItems;
		this.date = date;
		this.arrivalTime = arrivalTime;
	}

	public String getoId() {
		return oId;
	}

	public void setoId(String oId) {
		this.oId = oId;
	}

	public int getuId() {
		return uId;
	}

	public void setuId(int uId) {
		this.uId = uId;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	public List<LineItems> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<LineItems> lineItems) {
		this.lineItems = lineItems;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		date = date.substring(0, 19);
		this.date = date;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	
}
