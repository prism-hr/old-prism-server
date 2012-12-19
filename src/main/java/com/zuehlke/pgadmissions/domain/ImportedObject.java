package com.zuehlke.pgadmissions.domain;

public interface ImportedObject extends CodeObject {
    
	void setEnabled(Boolean enabled);
	
	Boolean getEnabled();
}
