package com.zuehlke.pgadmissions.rest.representation.application;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import org.joda.time.LocalDate;

import java.util.List;

public class AppointmentPreferenceAnswersRepresentation {

    private UserRepresentation user;

    private List<Boolean> answers;

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public List<Boolean> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Boolean> answers) {
        this.answers = answers;
    }
}
