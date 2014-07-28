package com.zuehlke.pgadmissions.rest.dto.application;

import com.zuehlke.pgadmissions.rest.representation.application.AddressRepresentation;
import org.joda.time.DateTime;

public class ApplicationEmploymentPositionDTO {

    private Integer id;

    private String employerName;

    private AddressDTO employerAddress;

    private String position;

    private Boolean current;

    private String remit;

    private DateTime startDate;

    private DateTime endDate;

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

    public AddressDTO getEmployerAddress() {
        return employerAddress;
    }

    public void setEmployerAddress(AddressDTO employerAddress) {
        this.employerAddress = employerAddress;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public String getRemit() {
        return remit;
    }

    public void setRemit(String remit) {
        this.remit = remit;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }
}
