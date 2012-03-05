package com.zuehlke.pgadmissions.dto;

public class QualificationDTO {

	private String degree;
	private String date_taken;
	private String institution;
	private String grade;
	
	public String getDegree() {
		return degree;
	}
	public void setDegree(String degree) {
		this.degree = degree;
	}
	public String getDate_taken() {
		return date_taken;
	}
	public void setDate_taken(String date_taken) {
		this.date_taken = date_taken;
	}
	public String getInstitution() {
		return institution;
	}
	public void setInstitution(String institution) {
		this.institution = institution;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
}
