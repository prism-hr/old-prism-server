package com.zuehlke.pgadmissions.rest.dto.imported;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import uk.co.alumeni.prism.api.model.imported.request.ImportedSubjectAreaRequest;

public class ImportedSubjectAreaImportDTO extends ImportedSubjectAreaRequest {
    
    @NotNull
    @Size(min = 1, max = 99999999)
    private Integer id;
    
    @Size(max = 100)
    private String jacsCodeOld;

    @NotNull
    @Size(min = 1, max = 99999999)
    private Integer ucasSubject;
  
    @Size(max = 99999999)
    private Integer parent;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJacsCodeOld() {
        return jacsCodeOld;
    }

    public void setJacsCodeOld(String jacsCodeOld) {
        this.jacsCodeOld = jacsCodeOld;
    }

    public Integer getUcasSubject() {
        return ucasSubject;
    }

    public void setUcasSubject(Integer ucasSubject) {
        this.ucasSubject = ucasSubject;
    }
    
    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public ImportedSubjectAreaImportDTO withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public ImportedSubjectAreaImportDTO withJacsCode(String jacsCode) {
        setJacsCode(jacsCode);
        return this;
    }
    
    public ImportedSubjectAreaImportDTO withJacsCodeOld(String jacsCodeOld) {
        this.jacsCodeOld = jacsCodeOld;
        return this;
    }

    public ImportedSubjectAreaImportDTO withName(String name) {
        setName(name);
        return this;
    }

    public ImportedSubjectAreaImportDTO withDescription(String description) {
        setDescription(description);
        return this;
    }
    
    public ImportedSubjectAreaImportDTO withUcasSubject(Integer ucasSubject) {
        this.ucasSubject = ucasSubject;
        return this;
    }

    public ImportedSubjectAreaImportDTO withParent(Integer parent) {
        this.parent = parent;
        return this;
    }

}
