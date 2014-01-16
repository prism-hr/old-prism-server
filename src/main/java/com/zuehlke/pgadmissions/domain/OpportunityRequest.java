package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "OPPORTUNITY_REQUEST")
public class OpportunityRequest {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_domicile_id")
    private Domicile institutionCountry;

    @Column(name = "institution_code")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 10)
    private String institutionCode;

    @Column(name = "other_institution_name")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
    private String otherInstitution;

    @Column(name = "title")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
    private String programTitle;

    @Column(name = "description")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 3000)
    private String programDescription;

    @Transient
    private RegisteredUser author;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Domicile getInstitutionCountry() {
        return institutionCountry;
    }

    public void setInstitutionCountry(Domicile institutionCountry) {
        this.institutionCountry = institutionCountry;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getOtherInstitution() {
        return otherInstitution;
    }

    public void setOtherInstitution(String otherInstitution) {
        this.otherInstitution = otherInstitution;
    }

    public String getProgramTitle() {
        return programTitle;
    }

    public void setProgramTitle(String programTitle) {
        this.programTitle = programTitle;
    }

    public String getProgramDescription() {
        return programDescription;
    }

    public void setProgramDescription(String programDescription) {
        this.programDescription = programDescription;
    }

    public RegisteredUser getAuthor() {
        return author;
    }

    public void setAuthor(RegisteredUser author) {
        this.author = author;
    }

}
