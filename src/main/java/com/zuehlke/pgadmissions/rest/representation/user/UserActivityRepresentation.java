package com.zuehlke.pgadmissions.rest.representation.user;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.ScopeActionSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ScopeUpdateSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationAppointmentRepresentation;

public class UserActivityRepresentation {

    private List<ScopeActionSummaryRepresentation> urgentSummaries;

    private List<ScopeUpdateSummaryRepresentation> updateSummaries;

    private List<ApplicationAppointmentRepresentation> appointmentSummaries;

    public List<ScopeActionSummaryRepresentation> getUrgentSummaries() {
        return urgentSummaries;
    }

    public void setUrgentSummaries(List<ScopeActionSummaryRepresentation> urgentSummaries) {
        this.urgentSummaries = urgentSummaries;
    }

    public List<ScopeUpdateSummaryRepresentation> getUpdateSummaries() {
        return updateSummaries;
    }

    public void setUpdateSummaries(List<ScopeUpdateSummaryRepresentation> updateSummaries) {
        this.updateSummaries = updateSummaries;
    }

    public List<ApplicationAppointmentRepresentation> getAppointmentSummaries() {
        return appointmentSummaries;
    }

    public void setAppointmentSummaries(List<ApplicationAppointmentRepresentation> appointmentSummaries) {
        this.appointmentSummaries = appointmentSummaries;
    }

}
