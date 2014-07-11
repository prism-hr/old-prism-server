package com.zuehlke.pgadmissions.rest.representation.application;

import org.joda.time.LocalDate;

public class EmploymentPositionRepresentation {

    private String employerName;

    private AddressRepresentation employerAddress;

    private String position;

    private boolean current;

    private String remit;

    private LocalDate startDate;

    private LocalDate endDate;

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public AddressRepresentation getEmployerAddress() {
        return employerAddress;
    }

    public void setEmployerAddress(AddressRepresentation employerAddress) {
        this.employerAddress = employerAddress;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
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
}
