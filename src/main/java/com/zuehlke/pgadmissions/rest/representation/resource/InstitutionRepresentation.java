package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;

public class InstitutionRepresentation {

    private Integer id;

    private String code;

    private String title;

    private String domicile;

    private InstitutionAddressRepresentation address;

    private String currency;

    private PrismProgramType defaultProgramType;

    private PrismStudyOption defaultStudyOption;

    private String homepage;

    private FileRepresentation logoDocument;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
    }

    public InstitutionAddressRepresentation getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressRepresentation address) {
        this.address = address;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PrismProgramType getDefaultProgramType() {
        return defaultProgramType;
    }

    public void setDefaultProgramType(PrismProgramType defaultProgramType) {
        this.defaultProgramType = defaultProgramType;
    }

    public PrismStudyOption getDefaultStudyOption() {
        return defaultStudyOption;
    }

    public void setDefaultStudyOption(PrismStudyOption defaultStudyOption) {
        this.defaultStudyOption = defaultStudyOption;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public FileRepresentation getLogoDocument() {
        return logoDocument;
    }

    public void setLogoDocument(FileRepresentation logoDocument) {
        this.logoDocument = logoDocument;
    }
}
