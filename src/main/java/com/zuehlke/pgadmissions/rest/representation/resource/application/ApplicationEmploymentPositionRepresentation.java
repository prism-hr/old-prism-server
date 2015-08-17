package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.address.AddressApplicationRepresentation;

public class ApplicationEmploymentPositionRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private String employerName;

    private AddressApplicationRepresentation employerAddress;

    private String position;

    private String remit;

    private LocalDate startDate;

    private Boolean current;
    
    private LocalDate endDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public AddressApplicationRepresentation getEmployerAddress() {
        return employerAddress;
    }

    public void setEmployerAddress(AddressApplicationRepresentation employerAddress) {
        this.employerAddress = employerAddress;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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
    
    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public ApplicationEmploymentPositionRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public ApplicationEmploymentPositionRepresentation withEmployerName(String employerName) {
        this.employerName = employerName;
        return this;
    }
    
    public ApplicationEmploymentPositionRepresentation withEmployerAddress(AddressApplicationRepresentation employerAddress) {
        this.employerAddress = employerAddress;
        return this;
    }
    
    public ApplicationEmploymentPositionRepresentation withPosition(String position) {
        this.position = position;
        return this;
    }    
    
    public ApplicationEmploymentPositionRepresentation withRemit(String remit) {
        this.remit = remit;
        return this;
    }
    
    public ApplicationEmploymentPositionRepresentation withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ApplicationEmploymentPositionRepresentation withCurrent(Boolean current) {
        this.current = current;
        return this;
    }
    
    public ApplicationEmploymentPositionRepresentation withEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }
    
}
