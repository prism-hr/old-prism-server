package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;

@Service
@Transactional
public class SupervisorsProvider {

    @Inject
    private UserService userService;

    @Inject
    private ApplicationsService applicationsService;

    public List<RegisteredUser> getNominatedSupervisors(@RequestParam String applicationId) {
        ArrayList<RegisteredUser> nominatedSupervisors = new ArrayList<RegisteredUser>();
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        ApprovalRound latestApprovalRound = applicationForm.getLatestApprovalRound();
        if (latestApprovalRound == null) {
            List<SuggestedSupervisor> suggestedSupervisors = applicationForm.getProgrammeDetails().getSuggestedSupervisors();
            for (SuggestedSupervisor suggestedSupervisor : suggestedSupervisors) {
                nominatedSupervisors.add(findOrCreateRegisterUserFromSuggestedSupervisorForForm(suggestedSupervisor, applicationForm));
            }
        }
        return nominatedSupervisors;
    }

    public List<RegisteredUser> getPreviousSupervisorsAndInterviewersWillingToSupervise(@RequestParam String applicationId) {
        List<RegisteredUser> availablePreviousSupervisors = new ArrayList<RegisteredUser>();
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        availablePreviousSupervisors.addAll(userService.getAllPreviousSupervisorsOfProgram(applicationForm.getProgram()));

        List<RegisteredUser> interviewersWillingToSupervise = applicationForm.getUsersWillingToSupervise();
        for (RegisteredUser registeredUser : interviewersWillingToSupervise) {
            if (!listContainsId(registeredUser, availablePreviousSupervisors)) {
                availablePreviousSupervisors.add(registeredUser);
            }
        }

        availablePreviousSupervisors.removeAll(getNominatedSupervisors(applicationId));
        return availablePreviousSupervisors;
    }

    private RegisteredUser findOrCreateRegisterUserFromSuggestedSupervisorForForm(SuggestedSupervisor suggestedSupervisor, ApplicationForm applicationForm) {
        String supervisorEmail = suggestedSupervisor.getEmail();
        RegisteredUser possibleUser = userService.getUserByEmailIncludingDisabledAccounts(supervisorEmail);
        if (possibleUser == null) {
            possibleUser = userService.createNewUserInRole(suggestedSupervisor.getFirstname(), suggestedSupervisor.getLastname(), supervisorEmail,
                    DirectURLsEnum.VIEW_APPLIATION_AS_SUPERVISOR, applicationForm, Authority.SUPERVISOR);
        }
        return possibleUser;
    }

    private ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (application == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return application;
    }

    private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
            if (entry.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

}
