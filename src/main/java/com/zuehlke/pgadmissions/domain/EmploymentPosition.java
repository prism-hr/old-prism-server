package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_EMPLOYMENT_POSITION")
public class EmploymentPosition implements Serializable, FormSectionObject {

    private static final long serialVersionUID = 4492119755495402951L;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private ApplicationForm application;

    @Column(name = "employer_name")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 150)
    private String employerName;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    @Valid
    private Address employerAddress;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    private String position;

    @Column(name = "is_current")
    private boolean current;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 250)
    private String remit;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;

    @Transient
    private boolean acceptedTerms;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employer) {
        this.employerName = employer;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String title) {
        this.position = title;
    }

    public String getRemit() {
        return remit;
    }

    public void setRemit(String remit) {
        this.remit = remit;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Address getEmployerAddress() {
        return employerAddress;
    }

    public void setEmployerAddress(Address employerAdress) {
        this.employerAddress = employerAdress;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public EmploymentPosition withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public EmploymentPosition withApplication(ApplicationForm application) {
        this.application = application;
        return this;
    }
    
    
    public EmploymentPosition withEmployerName(String employerName) {
        this.employerName = employerName;
        return this;
    }
    
    public EmploymentPosition withEmployerAddress(Address employerAddress) {
        this.employerAddress = employerAddress;
        return this;
    }

    public EmploymentPosition withPosition(String position) {
        this.position = position;
        return this;
    }
    
    public EmploymentPosition withCurrent(boolean current) {
        this.current = current;
        return this;
    }
    
    public EmploymentPosition withRemit(String remit) {
        this.remit = remit;
        return this;
    }
    
    public EmploymentPosition withStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }
    
    public EmploymentPosition withEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }
    
}
