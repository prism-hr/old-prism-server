package com.zuehlke.pgadmissions.domain;

import java.util.Date;

public interface ImportedObject extends CodeObject {
    
	void setEnabled(Boolean enabled);
	
	Boolean getEnabled();
	
    public Date getDisabledDate();

    public void setDisabledDate(Date disabledDate);
}
