package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ApplicationFormBuilder {

	private Integer id;
	
	private String title;

	private String gender;

	private String dob;

	private String country_ob;
	
	private String nationality;
	
	private String description_of_research;

	private RegisteredUser registered_user_id = new RegisteredUser();
	
	public ApplicationFormBuilder registeredUser (RegisteredUser user) {
		this.registered_user_id = user;
		return this;
	}
	
	public ApplicationFormBuilder title(String title) {
		this.title = title;
		return this;
	}
	
	public ApplicationFormBuilder gender(String gender) {
		this.gender = gender;
		return this;
	}
	
	public ApplicationFormBuilder dob(String dob) {
		this.dob = dob;
		return this;
	}
	
	public ApplicationFormBuilder nationality(String nationality) {
		this.nationality = nationality;
		return this;
	}
	
	public ApplicationFormBuilder country_ob(String country_ob) {
		this.country_ob = country_ob;
		return this;
	}
	
	public ApplicationFormBuilder description_of_research(String description_of_research) {
		this.description_of_research = description_of_research;
		return this;
	}
	
	public ApplicationFormBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public ApplicationForm toApplicationForm() {
		ApplicationForm application = new ApplicationForm();
		application.setCob(country_ob);
		application.setDob(dob);
		application.setGender(gender);
		application.setId(id);
		application.setNat(nationality);
		application.setRes(description_of_research);
		application.setTitle(title);
		application.setUser(registered_user_id);
		return application;
	}
}
