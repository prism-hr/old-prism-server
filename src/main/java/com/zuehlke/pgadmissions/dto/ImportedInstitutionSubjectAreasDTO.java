package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ImportedInstitutionSubjectAreasDTO extends HashMap<Integer, ImportedInstitutionSubjectAreaDTO> {
    
    private static final long serialVersionUID = -4383230981696954539L;
    
    private Integer headLimit;
    
    public ImportedInstitutionSubjectAreasDTO(Integer headLimit) {
        this.headLimit = headLimit;
    }

    public ImportedInstitutionSubjectAreaDTO add(ImportedInstitutionSubjectAreaDTO value) {
        return put(value.getId(), value, false);
    }

    @Override
    public ImportedInstitutionSubjectAreaDTO put(Integer key, ImportedInstitutionSubjectAreaDTO value) {
        return put(key, value, false);
    }

    @Override
    public ImportedInstitutionSubjectAreaDTO putIfAbsent(Integer key, ImportedInstitutionSubjectAreaDTO value) {
        if (!containsKey(key)) {
            return put(key, value, true);
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends ImportedInstitutionSubjectAreaDTO> values) {
        for (Map.Entry<? extends Integer, ? extends ImportedInstitutionSubjectAreaDTO> value : values.entrySet()) {
            put(value.getKey(), value.getValue(), false);
        }
    }

    private ImportedInstitutionSubjectAreaDTO put(Integer key, ImportedInstitutionSubjectAreaDTO value, boolean onlyIfAbsent) {
        ImportedInstitutionSubjectAreaDTO persistentValue = get(value.getId());
        if (persistentValue == null) {
            converge(value, value.getRelationStrength(), value.getRelationCount());
            super.put(key, value);
            return null;
        } else if (!onlyIfAbsent) {
            converge(persistentValue, value.getRelationStrength(), value.getRelationCount());
        }
        return persistentValue;
    }

    private void converge(ImportedInstitutionSubjectAreaDTO value, BigDecimal relationStrength, Long relationCount) {
        value.addRelationCount(relationCount);
        value.addHead(headLimit, relationStrength, relationCount);
    }

}
