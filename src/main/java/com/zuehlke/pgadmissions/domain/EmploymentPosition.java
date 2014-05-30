package com.zuehlke.pgadmissions.domain;

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
import javax.validation.Valid;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_EMPLOYMENT_POSITION")
public class EmploymentPosition {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "employer_name", nullable = false)
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 150)
    private String employerName;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", nullable = false)
    @Valid
    private Address employerAddress;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    @Column(name = "position", nullable = false)
    private String position;

    @Column(name = "is_current", nullable = false)
    private boolean current;

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

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public EmploymentPosition withId(Integer id) {
        this.id = id;
        return this;
    }

    public EmploymentPosition withApplication(Application application) {
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

    public EmploymentPosition withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public EmploymentPosition withEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

}
