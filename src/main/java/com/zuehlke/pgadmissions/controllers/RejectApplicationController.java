package com.zuehlke.pgadmissions.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RejectService;

@Controller
@RequestMapping(value = { "/rejectApplication" })
public class RejectApplicationController {

	private static final String REJECT_VIEW_NAME = "private/staff/approver/reject_page";
	private static final String NEXT_VIEW_NAME = "redirect:/applications";
	private final RejectService rejectService;
	private final ApplicationsService applicationService;

	CollectionContainsPredicate predicate = new CollectionContainsPredicate();

	RejectApplicationController() {
		this(null, null);
	}

	@Autowired
	public RejectApplicationController(ApplicationsService applicationsService, RejectService rejectService) {
		this.applicationService = applicationsService;
		this.rejectService = rejectService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getRejectPage() {
		return REJECT_VIEW_NAME;
	}

	@RequestMapping(value = "/moveApplicationToReject", method = RequestMethod.POST)
	public String moveApplicationToReject(//
			@ModelAttribute("applicationForm") ApplicationForm application,// 
			@RequestParam(value = "rejectReasonIds[]") Integer[] rejectReasonIds) {

		checkPermissionForApplication(application);
		checkApplicationStatus(application);
		if (rejectReasonIds == null || rejectReasonIds.length == 0) {
			throw new IllegalArgumentException("no rejected reasons set!");
		}
		Collection<RejectReason> rejectReasons = getRejectReason(rejectReasonIds);
		rejectService.moveApplicationToReject(application, getCurrentUser(), rejectReasons);
		return NEXT_VIEW_NAME;
	}

	@ModelAttribute("availableReasons")
	public List<RejectReason> getAvailableReasons() {
		return rejectService.getAllRejectionReasons();
	}

	private Collection<RejectReason> getRejectReason(final Integer[] rejectReasonIds) {
		List<RejectReason> allRejectionReasons = new ArrayList<RejectReason>(rejectService.getAllRejectionReasons());
		predicate.setReasonIds(rejectReasonIds);
		CollectionUtils.filter(allRejectionReasons, predicate);
		return allRejectionReasons;
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {
		ApplicationForm application = applicationService.getApplicationById(applicationId);
		checkPermissionForApplication(application);
		checkApplicationStatus(application);
		return application;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return getCurrentUser();
	}

	private void checkApplicationStatus(ApplicationForm application) {
		switch (application.getStatus()) {
		case REVIEW:
		case VALIDATION:
		case APPROVAL:
		case INTERVIEW:
			break;
		default:
			throw new CannotUpdateApplicationException();
		}
	}

	private void checkPermissionForApplication(ApplicationForm application) {
		RegisteredUser currentUser = getCurrentUser();
		if (application == null || // 
				!(application.getProgram().isApprover(currentUser)
				|| currentUser.isAdminInProgramme(application.getProgram()))) {
			throw new ResourceNotFoundException();
		}
	}

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

	class CollectionContainsPredicate implements Predicate {
		private Integer[] reasonIds;

		public void setReasonIds(Integer[] reasonIds) {
			this.reasonIds = reasonIds;
		}

		@Override
		public boolean evaluate(Object object) {
			RejectReason reason = (RejectReason) object;
			return ArrayUtils.contains(reasonIds, reason.getId());
		}
	}
}
