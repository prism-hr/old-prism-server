package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.zuehlke.pgadmissions.rest.validation.annotation.DateNotFuture;
import com.zuehlke.pgadmissions.rest.validation.annotation.DatePast;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.validation.annotation.DateNotAfterDate;

@DateNotAfterDate(startDate = "startDate", endDate = "endDate")
public class ApplicationEmploymentPositionDTO {

    private Integer id;

    @NotEmpty
    @Size(max = 200)
    private String employerName;

    @NotNull
    private AddressDTO employerAddress;

    @NotEmpty
    @Size(max = 200)
    private String position;

    private Boolean current;

    @NotEmpty
    @Size(max = 2000)
    private String remit;

    @NotNull
    @DateNotFuture
    private LocalDate startDate;

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
