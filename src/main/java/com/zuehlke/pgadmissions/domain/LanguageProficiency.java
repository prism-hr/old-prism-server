package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.LanguageAptitude;

@Entity(name = "LANGUAGE_PROFICIENCY")
@Access(AccessType.FIELD)
public class LanguageProficiency extends DomainObject<Integer> {


	private static final long serialVersionUID = -4734931727546947050L;
	
	@ManyToOne
	@JoinColumn(name = "language_id")
	private Language language;
		
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.LanguageAptitudeEnumUserType")
	private LanguageAptitude aptitude;

	@Column(name="primary_language")
	private boolean primary;
	
	public void setLanguage(Language language) {
		this.language = language;
	}

	public Language getLanguage() {
		return language;
	}

	public void setAptitude(LanguageAptitude aptitude) {
		this.aptitude = aptitude;
	
		
	}

	public LanguageAptitude getAptitude() {
		return aptitude;
	}

	

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
	public Integer getId() {
		return id;
	}

	public String getAsJson() {
		return  "{\"aptitude\": \"" + this.getAptitude() + "\", \"language\": " + this.getLanguage().getId() + ", \"primary\": \"" + this.isPrimary() + "\"}";
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

}
