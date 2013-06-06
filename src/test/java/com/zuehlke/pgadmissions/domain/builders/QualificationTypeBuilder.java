package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.QualificationType;

public class QualificationTypeBuilder {
	
    private Integer id;
	
    private String name;
	
    private Boolean enabled;
    
    private String code;
	
    public QualificationTypeBuilder code(String code) {
        this.code = code;
        return this;
    }
    
	public QualificationTypeBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public QualificationTypeBuilder name(String name){
		this.name = name;
		return this;
	}
		
	public QualificationTypeBuilder enabled(Boolean enabled){
	    this.enabled = enabled;
	    return this;
	}
	
	public QualificationType build() {
	    QualificationType title = new QualificationType();
	    title.setId(id);
	    title.setName(name);
	    title.setEnabled(enabled);
	    title.setCode(code);
		return title;
	}
}
