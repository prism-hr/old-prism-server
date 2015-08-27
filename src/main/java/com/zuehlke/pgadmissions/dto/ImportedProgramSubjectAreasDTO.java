package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ImportedProgramSubjectAreasDTO extends HashMap<Integer, ImportedProgramSubjectAreaDTO> {

    private static final long serialVersionUID = -4383230981696954539L;

    public ImportedProgramSubjectAreaDTO add(ImportedProgramSubjectAreaDTO value) {
        return put(value.getId(), value, false);
    }

    @Override
    public ImportedProgramSubjectAreaDTO put(Integer key, ImportedProgramSubjectAreaDTO value) {
        return put(key, value, false);
    }

    @Override
    public ImportedProgramSubjectAreaDTO putIfAbsent(Integer key, ImportedProgramSubjectAreaDTO value) {
        if (!containsKey(key)) {
            return put(key, value, true);
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends ImportedProgramSubjectAreaDTO> values) {
        for (Map.Entry<? extends Integer, ? extends ImportedProgramSubjectAreaDTO> value : values.entrySet()) {
            put(value.getKey(), value.getValue(), false);
        }
    }

    private ImportedProgramSubjectAreaDTO put(Integer key, ImportedProgramSubjectAreaDTO value, boolean onlyIfAbsent) {
        ImportedProgramSubjectAreaDTO persistentValue = get(value.getId());
        if (persistentValue == null) {
            super.put(key, value);
            return null;
        } else if (!onlyIfAbsent) {
            BigDecimal confidence = value.getConfidence();
            BigDecimal persistentConfidence = persistentValue.getConfidence();
            if (confidence.compareTo(persistentConfidence) > 0) {
                persistentValue.setMatchType(value.getMatchType());
                persistentValue.setConfidence(value.getConfidence());
            }
        }
        return persistentValue;
    }

}
