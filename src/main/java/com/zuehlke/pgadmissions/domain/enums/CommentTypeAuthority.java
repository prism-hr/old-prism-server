package com.zuehlke.pgadmissions.domain.enums;

import org.apache.commons.lang.ArrayUtils;

public enum CommentTypeAuthority {
	
	GENERIC(AuthorityGroup.COMMENTVIEWER.authorities()), 
	ADMITTER_COMMENT(AuthorityGroup.COMMENTVIEWER.authorities()), 
	VALIDATION(AuthorityGroup.COMMENTVIEWER.authorities()), 
	REVIEW(AuthorityGroup.COMMENTVIEWER.authorities()), 
	REVIEW_EVALUATION((AuthorityGroup.COMMENTVIEWER.authorities())), 
	INTERVIEW((AuthorityGroup.COMMENTVIEWER.authorities())), 
	INTERVIEW_EVALUATION((AuthorityGroup.COMMENTVIEWER.authorities())), 
	APPROVAL((AuthorityGroup.COMMENTVIEWER.authorities())), 
	APPROVAL_EVALUATION((AuthorityGroup.COMMENTVIEWER.authorities())), 
	REFERENCE((AuthorityGroup.COMMENTVIEWER.authorities())), 
	REQUEST_RESTART((AuthorityGroup.COMMENTVIEWER.authorities())), 
	SUPERVISION_CONFIRMATION((AuthorityGroup.COMMENTVIEWER.authorities())), 
	INTERVIEW_VOTE((AuthorityGroup.COMMENTVIEWER.authorities())), 
	INTERVIEW_SCHEDULE((Authority[]) ArrayUtils.add(AuthorityGroup.COMMENTVIEWER.authorities().clone(), Authority.APPLICANT)), 
	STATE_CHANGE_SUGGESTION((AuthorityGroup.COMMENTVIEWER.authorities())),
	OFFER_RECOMMENDED_COMMENT((AuthorityGroup.COMMENTVIEWER.authorities()));
	
	private final Authority[] authorities;
	
	private CommentTypeAuthority(Authority... authorities) {
		this.authorities = authorities;
	}

	public Authority[] authorities() {
		return authorities;
	}
	
}