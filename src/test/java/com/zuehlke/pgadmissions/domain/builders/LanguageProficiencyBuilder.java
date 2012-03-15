package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageProficiency;
import com.zuehlke.pgadmissions.domain.enums.LanguageAptitude;

public class LanguageProficiencyBuilder {

	private Integer id;
	private Language language;
	private LanguageAptitude aptitude;
	private boolean primary;
	
	
	public LanguageProficiencyBuilder primary(boolean primary){
		this.primary = primary;
		return this;
	}
	
	public LanguageProficiencyBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public LanguageProficiencyBuilder language(Language language){
		this.language = language;
		return this;
	}
	
	public LanguageProficiencyBuilder aptitude(LanguageAptitude aptitude){
		this.aptitude = aptitude;
		return this;
	}
	
	
	public LanguageProficiency toLanguageProficiency(){
		LanguageProficiency languageProficiency = new LanguageProficiency();
		languageProficiency.setPrimary(primary);
		languageProficiency.setId(id);
		languageProficiency.setLanguage(language);
		languageProficiency.setAptitude(aptitude);
		return languageProficiency;
	}
}
