package com.zuehlke.pgadmissions.dto;

import java.util.Date;


public class AdvertDTO {

	    private Integer id;

	    private String title;
	    
	    private String description;

	    private Integer studyDuration;

	    private String funding;
	    
	    private String programCode;

		private Date closingDate;

		private String email;

		private boolean selected;
		
		private String type;
		
		private Integer projectId;
		
		public AdvertDTO(Integer id){
			this.id=id;
		}

		public Integer getId() {
			return id;
		}


		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Integer getStudyDuration() {
			return studyDuration;
		}

		public void setStudyDuration(Integer studyDuration) {
			this.studyDuration = studyDuration;
		}

		public String getFunding() {
			return funding;
		}

		public void setFunding(String funding) {
			this.funding = funding;
		}

		public String getProgramCode() {
			return programCode;
		}

		public void setProgramCode(String programCode) {
			this.programCode = programCode;
		}

		public void setClosingDate(Date closingDate) {
			this.closingDate = closingDate;
		}

		public Date getClosingDate() {
			return closingDate;
		}

		public void setSupervisorEmail(String email) {
			this.email = email;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public boolean isSelected() {
			return selected;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Integer getProjectId() {
			return projectId;
		}

		public void setProjectId(Integer projectId) {
			this.projectId = projectId;
		}
		
		


}
