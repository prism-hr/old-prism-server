package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.Person;

public class ProjectAdvertDTO extends AdvertDTO {

	private boolean secondarySupervisorSpecified;
	private Person secondarySupervisor;
	public ProjectAdvertDTO(Integer id) {
		super(id);
	}
	public boolean isSecondarySupervisorSpecified() {
		return secondarySupervisorSpecified;
	}
	public void setSecondarySupervisorSpecified(boolean secondarySupervisorSpecified) {
		this.secondarySupervisorSpecified = secondarySupervisorSpecified;
	}
	public Person getSecondarySupervisor() {
		return secondarySupervisor;
	}
	public void setSecondarySupervisor(Person secondarySupervisor) {
		this.secondarySupervisor = secondarySupervisor;
		setSecondarySupervisorSpecified(secondarySupervisor!=null);
	}

}
