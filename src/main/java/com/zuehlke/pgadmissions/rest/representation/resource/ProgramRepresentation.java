package com.zuehlke.pgadmissions.rest.representation.resource;


import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;

public class ProgramRepresentation {

    private Integer id;

    private String title;

    private PrismLocale locale;

    private InstitutionRepresentation institution;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PrismLocale getLocale() {
        return locale;
    }

    public void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    public InstitutionRepresentation getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionRepresentation institution) {
        this.institution = institution;
    }
}
