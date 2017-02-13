package com.trivadis.notes.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

// TODO annotate this class and properties
@Entity
public class Note {
	@Id
	private String id;

	private double latitude;
	private double longitude;

	private String text;

	static int _id = 1000;

	public Note() {
	}

	public Note(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
