package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.enums.ProgramTypeId;
import com.zuehlke.pgadmissions.services.ProgramsService;

@Component
public class ProgramTypePropertyEditor extends PropertyEditorSupport {

    private final ProgramsService programsService;

    public ProgramTypePropertyEditor() {
        this(null);
    }

    @Autowired
    public ProgramTypePropertyEditor(ProgramsService programsService) {
        this.programsService = programsService;
    }

    @Override
    public void setAsText(String strId) throws IllegalArgumentException {
        // TODO implement
        if (strId == null || StringUtils.isBlank(strId)) {
            setValue(null);
            return;
        }
        setValue(programsService.getProgramTypeById(ProgramTypeId.valueOf(strId)));
    }

    @Override
    public String getAsText() {
        if (getValue() == null) {
            return null;
        }
        return ((ProgramType) getValue()).getId().name();
    }
}
