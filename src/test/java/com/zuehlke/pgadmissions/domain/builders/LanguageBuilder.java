package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Language;

public class LanguageBuilder {
	private String name;
	private Integer id;
	
	public LanguageBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public LanguageBuilder name(String name){
		this.name = name;
		return this;
	}
	
	public Language toLanguage(){
		Language language = new Language();
		language.setId(id);
		language.setName(name);
		return language;
	}
}
