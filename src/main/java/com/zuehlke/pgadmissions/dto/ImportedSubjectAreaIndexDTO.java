package com.zuehlke.pgadmissions.dto;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;

public class ImportedSubjectAreaIndexDTO {
    
    Map<Integer, ImportedSubjectAreaDTO> byId = Maps.newHashMap();

    Map<String, ImportedSubjectAreaDTO> byJacsCode = Maps.newHashMap();

    Map<String, ImportedSubjectAreaDTO> byJacsCodeOld = Maps.newHashMap();

    HashMultimap<Integer, ImportedSubjectAreaDTO> byUcasSubject = HashMultimap.create();
    
    public ImportedSubjectAreaDTO getById(Integer id) {
        return byId.get(id);
    }
    
    public ImportedSubjectAreaDTO getByJacsCode(String jacsCode) {
        return byJacsCode.get(jacsCode);
    }

    public ImportedSubjectAreaDTO getByJacsCodeOld(String jacsCodeOld) {
        return byJacsCodeOld.get(jacsCodeOld);
    }

    public Set<ImportedSubjectAreaDTO> getByUcasSubject(Integer ucasSubject) {
        return byUcasSubject.get(ucasSubject);
    }
    
    public ImportedSubjectAreaIndexDTO addById(Integer id, ImportedSubjectAreaDTO subjectArea) {
        byId.put(id, subjectArea);
        return this;
    }

    public ImportedSubjectAreaIndexDTO addByJacsCode(String jacsCode, ImportedSubjectAreaDTO subjectArea) {
        byJacsCode.put(jacsCode, subjectArea);
        return this;
    }

    public ImportedSubjectAreaIndexDTO addByJacsCodeOld(String jacsCodeOld, ImportedSubjectAreaDTO subjectArea) {
        byJacsCodeOld.put(jacsCodeOld, subjectArea);
        return this;
    }

    public ImportedSubjectAreaIndexDTO addByUcasSubject(Integer ucasSubject, ImportedSubjectAreaDTO subjectArea) {
        byUcasSubject.put(ucasSubject, subjectArea);
        return this;
    }

}
