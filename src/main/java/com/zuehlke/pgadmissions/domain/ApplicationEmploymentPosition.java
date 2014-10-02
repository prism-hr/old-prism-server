package com.zuehlke.pgadmissions.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.validation.annotation.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_EMPLOYMENT_POSITION")
public class ApplicationEmploymentPosition {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false, insertable = false, updatable = false)
    private Application application;

    @Column(name = "employer_name", nullable = false)
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 150)
    private String employerName;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", nullable = false)
    private Address employerAddress;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    @Column(name = "position", nullable = false)
    private String position;

    @Column(name = "is_current", nullable = false)
    private Boolean current;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 250)
    @Column(name = "remit", nullable = false)
    private String remit;

    @Column(name = "start_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate startDate;

    @Column(name = "end_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate endDate;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Address getEmployerAddress() {
        return employerAddress;
    }

    public void setEmployerAddress(Address employerAdress) {
        this.employerAddress = employerAdress;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public ApplicationEmploymentPosition withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationEmploymentPosition withApplication(Application application) {
        this.application = application;
        return this;
    }

    public ApplicationEmploymentPosition withEmployerName(String employerName) {
        this.employerName = employerName;
        return this;
    }

    public ApplicationEmploymentPosition withEmployerAddress(Address employerAddress) {
        this.employerAddress = employerAddress;
        return this;
    }

    public ApplicationEmploymentPosition withPosition(String position) {
        this.position = position;
        return this;
    }

    public ApplicationEmploymentPosition withCurrent(boolean current) {
        this.current = current;
        return this;
    }

    public ApplicationEmploymentPosition withRemit(String remit) {
        this.remit = remit;
        return this;
    }

    public ApplicationEmploymentPosition withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ApplicationEmploymentPosition withEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }
    
    public String getEmployerAddressLocation() {
        return employerAddress == null ? null : employerAddress.getLocationString();
    }
    
    public String getStartDateDisplay(String dateFormat) {
        return startDate == null ? null : startDate.toString(dateFormat);
    }
    
    public String getEndDateDisplay(String dateFormat) {
        return endDate == null ? null : endDate.toString(dateFormat);
    }

}
