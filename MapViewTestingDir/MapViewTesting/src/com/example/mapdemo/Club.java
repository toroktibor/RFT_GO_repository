package com.example.mapdemo;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

public class Club {
	private static int counter = -1;
	private int id;
	private String name;
	private String type;
	private String description;
	private String address;
	private LatLng latlng;
	private String phonenumber;
	private String email;
	private Date highlight_expire;
	private int approved;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public LatLng getLatlng() {
		return latlng;
	}
	public void setLatlng(LatLng latlng) {
		this.latlng = latlng;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getHighlight_expire() {
		return highlight_expire;
	}
	public void setHighlight_expire(Date highlight_expire) {
		this.highlight_expire = highlight_expire;
	}
	public int getApproved() {
		return approved;
	}
	public void setApproved(int approved) {
		this.approved = approved;
	}
	public Club(String name, LatLng latlng, int approved) {
		super();
		this.id = counter++;
		this.name = name;
		this.latlng = latlng;
		this.approved = approved;
	}
	
}
