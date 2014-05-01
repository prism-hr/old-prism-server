package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.SourcesOfInterestService;

@Component
public class SourcesOfInterestPropertyEditor extends PropertyEditorSupport {

    private final SourcesOfInterestService service;
    private final EncryptionHelper encryptionHelper;
    
    @Autowired
    public SourcesOfInterestPropertyEditor(SourcesOfInterestService service,  EncryptionHelper encryptionHelper) {
        this.service = service;
        this.encryptionHelper = encryptionHelper;
    }
    
    public SourcesOfInterestPropertyEditor() {
        this(null, null);
    }
    
    @Override
    public void setAsText(String strId) throws IllegalArgumentException {
        if (StringUtils.isBlank(strId)) {
            setValue(null);
            return;
        }
        setValue(service.getSourcesOfInterestById(encryptionHelper.decryptToInteger(strId)));
    }

    @Override
    public String getAsText() {
        if (getValue() == null || ((SourcesOfInterest) getValue()).getId() == null) {
            return null;
        }
        return ((SourcesOfInterest) getValue()).getId().toString();
    }
}
