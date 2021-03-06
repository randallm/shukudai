package com.randallma.shukudai;

public class Assignment {
	private long id;
	private String description;
	private String dateDue;
	private String dateAssigned;
	private String imageUri;
	private String schoolClass;
	private long schoolClassId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDateDue() {
		return dateDue;
	}

	public void setDateDue(String dateDue) {
		this.dateDue = dateDue;
	}

	public String getDateAssigned() {
		return dateAssigned;
	}

	public void setDateAssigned(String dateAssigned) {
		this.dateAssigned = dateAssigned;
	}

	public String getImageUri() {
		return imageUri;
	}

	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}

	public String getSchoolClass() {
		return schoolClass;
	}

	public void setSchoolClass(String schoolClass) {
		this.schoolClass = schoolClass;
	}

	public long getSchoolClassId() {
		return schoolClassId;
	}

	public void setSchoolClassId(long schoolClassId) {
		this.schoolClassId = schoolClassId;
	}
}
