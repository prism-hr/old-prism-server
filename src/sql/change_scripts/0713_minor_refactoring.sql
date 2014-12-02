ALTER TABLE COMMENT
	ADD COLUMN application_rejection_reason_id INT(10) UNSIGNED,
	ADD INDEX (application_rejection_reason_id),
	ADD FOREIGN KEY (application_rejection_reason_id) REFERENCES IMPORTED_ENTITY (id)
;

UPDATE COMMENT
SET application_rejection_reason_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE code = "APPLICATION_INCOMPLETE")
WHERE content = "We were unable to form a judgement on your suitability based upon the information supplied in your application."
;

UPDATE COMMENT
SET application_rejection_reason_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE code = "UNQUALIFIED_FOR_INSTITUTION")
WHERE content = "Your qualifications and experience are not sufficient to satisfy the entrance requirements for a research degree programme at UCL."
;

UPDATE COMMENT
SET application_rejection_reason_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE code = "UNQUALIFIED_FOR_OPPORTUNITY")
WHERE content = "Your qualifications and experience are not appropriate for the research degree programme that you applied for."
;

UPDATE COMMENT
SET application_rejection_reason_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE code = "UNABLE_TO_FIND_SUPERVISOR")
WHERE content = "At the present time, we are unable to identify academic supervisors to support you in your preferred research programme."
;

UPDATE COMMENT
SET application_rejection_reason_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE code = "DID_NOT_ATTEND_INTERVIEW")
WHERE content = "You failed to present for interview as arranged."
;

UPDATE COMMENT
SET application_rejection_reason_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE code = "OPPORTUNITY_OVERSUBSCRIBED")
WHERE content = "Although you may be suitable for a research degree programme at UCL, the competition for places on the programme that you applied for was such that we were unable to progress your application on this occasion. Subject to the continuation of the programme, you may reapply in the next academic year."
;

UPDATE COMMENT
SET application_rejection_reason_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE code = "OPPORTUNITY_DISCONTINUED")
WHERE content = "We are no longer able to offer the programme that you applied for."
;

UPDATE COMMENT
SET application_rejection_reason_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE code = "WITHDRAWN")
WHERE content = "You have informed us that you no longer wish to be considered."
;

ALTER TABLE DOCUMENT
	DROP COLUMN is_referenced
;

DELETE DOCUMENT.*
FROM DOCUMENT LEFT JOIN APPLICATION_DOCUMENT
	ON DOCUMENT.id = APPLICATION_DOCUMENT.cv_id
LEFT JOIN APPLICATION_DOCUMENT AS APPLICATION_DOCUMENT2
	ON DOCUMENT.id = APPLICATION_DOCUMENT2.personal_statement_id
LEFT JOIN APPLICATION_FUNDING
	ON DOCUMENT.id = APPLICATION_FUNDING.document_id
LEFT JOIN APPLICATION_LANGUAGE_QUALIFICATION
	ON DOCUMENT.id = APPLICATION_LANGUAGE_QUALIFICATION.document_id
LEFT JOIN APPLICATION_QUALIFICATION
	ON DOCUMENT.id = APPLICATION_QUALIFICATION.document_id
WHERE comment_id IS NULL
	AND APPLICATION_DOCUMENT.id IS NULL
	AND APPLICATION_DOCUMENT2.id IS NULL
	AND APPLICATION_FUNDING.id IS NULL
	AND APPLICATION_LANGUAGE_QUALIFICATION.id IS NULL
	AND APPLICATION_QUALIFICATION.id IS NULL
;

ALTER TABLE DOCUMENT
	ADD COLUMN user_id INT(10) UNSIGNED AFTER document_type,
	MODIFY COLUMN comment_id INT(10) UNSIGNED AFTER user_id,
	ADD INDEX (user_id),
	ADD FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE DOCUMENT INNER JOIN COMMENT
	ON DOCUMENT.comment_id = COMMENT.id
SET DOCUMENT.user_id = COMMENT.user_id
;

UPDATE DOCUMENT INNER JOIN APPLICATION_DOCUMENT
	ON DOCUMENT.id = APPLICATION_DOCUMENT.cv_id
INNER JOIN APPLICATION
	ON APPLICATION_DOCUMENT.id = APPLICATION.application_document_id
SET DOCUMENT.user_id = APPLICATION.user_id
;

UPDATE DOCUMENT INNER JOIN APPLICATION_DOCUMENT
	ON DOCUMENT.id = APPLICATION_DOCUMENT.personal_statement_id
INNER JOIN APPLICATION
	ON APPLICATION_DOCUMENT.id = APPLICATION.application_document_id
SET DOCUMENT.user_id = APPLICATION.user_id
;

UPDATE DOCUMENT INNER JOIN APPLICATION_FUNDING
	ON DOCUMENT.id = APPLICATION_FUNDING.document_id
INNER JOIN APPLICATION
	ON APPLICATION_FUNDING.application_id = APPLICATION.id
SET DOCUMENT.user_id = APPLICATION.user_id
;

UPDATE DOCUMENT INNER JOIN APPLICATION_QUALIFICATION
	ON DOCUMENT.id = APPLICATION_QUALIFICATION.document_id
INNER JOIN APPLICATION
	ON APPLICATION_QUALIFICATION.application_id = APPLICATION.id
SET DOCUMENT.user_id = APPLICATION.user_id
;

UPDATE DOCUMENT INNER JOIN APPLICATION_LANGUAGE_QUALIFICATION
	ON DOCUMENT.id = APPLICATION_LANGUAGE_QUALIFICATION.document_id
INNER JOIN APPLICATION_PERSONAL_DETAIL
	ON APPLICATION_LANGUAGE_QUALIFICATION.id = APPLICATION_PERSONAL_DETAIL.application_language_qualification_id
INNER JOIN APPLICATION
	ON APPLICATION_PERSONAL_DETAIL.id = APPLICATION.application_personal_detail_id
SET DOCUMENT.user_id = APPLICATION.user_id
;

ALTER TABLE DOCUMENT
	MODIFY COLUMN user_id INT(10) UNSIGNED NOT NULL
;

DROP TABLE USER_UNUSED_EMAIL
;
