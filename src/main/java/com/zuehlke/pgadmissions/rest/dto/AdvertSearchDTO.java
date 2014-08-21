package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class AdvertSearchDTO {

    @NotNull
    private PrismProgramType programType;

    private String keywords;

    private String location;

    private String institutionId;

    private String[] studyOptions;

}

