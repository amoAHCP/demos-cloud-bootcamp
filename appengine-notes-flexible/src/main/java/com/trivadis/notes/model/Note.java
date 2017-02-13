package com.trivadis.notes.model;

public class Note {
	private String id;

	private double latitude;
	private double longitude;

	private String text;

	static int _id = 1000;

	public Note() {
	}

	public Note(String text) {
		this.id = generateId();
		this.text = text;
	}

	private static synchronized String generateId() {
		String s = "" + System.currentTimeMillis();
		while (s.length() < 15)
			s = "0" + s;
		return s + (_id++);
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
