package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.dao.SourcesOfInterestDAO;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

@Component
public class SourcesOfInterestPropertyEditor extends PropertyEditorSupport {

    private final SourcesOfInterestDAO sourcesOfInterestDAO;
    private final EncryptionHelper encryptionHelper;
    
    @Autowired
    public SourcesOfInterestPropertyEditor(SourcesOfInterestDAO dao,  EncryptionHelper encryptionHelper) {
        this.sourcesOfInterestDAO = dao;
        this.encryptionHelper = encryptionHelper;
    }
    
    public SourcesOfInterestPropertyEditor() {
        this(null, null);
    }
    
    @Override
    public void setAsText(String strId) throws IllegalArgumentException {
        if (strId == null) {
            setValue(null);
            return;
        }
        setValue(sourcesOfInterestDAO.getSourcesOfInterestById(encryptionHelper.decryptToInteger(strId)));
    }

    @Override
    public String getAsText() {
        if (getValue() == null || ((SourcesOfInterest) getValue()).getId() == null) {
            return null;
        }
        return ((SourcesOfInterest) getValue()).getId().toString();
    }
}
