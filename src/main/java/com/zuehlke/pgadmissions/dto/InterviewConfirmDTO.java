package com.zuehlke.pgadmissions.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

public class InterviewConfirmDTO {

    private Integer timeslotId;

    private String furtherDetails;

    private String furtherInterviewerDetails;

    public Integer getTimeslotId() {
        return timeslotId;
    }

    public void setTimeslotId(Integer timeslotId) {
        this.timeslotId = timeslotId;
    }

    public String getFurtherDetails() {
        return furtherDetails;
    }

    public void setFurtherDetails(String furtherDetails) {
        this.furtherDetails = furtherDetails;
    }

    public String getFurtherInterviewerDetails() {
        return furtherInterviewerDetails;
    }

    public void setFurtherInterviewerDetails(String furtherInterviewerDetails) {
        this.furtherInterviewerDetails = furtherInterviewerDetails;
    }

}