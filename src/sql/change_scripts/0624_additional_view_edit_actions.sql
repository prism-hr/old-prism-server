UPDATE ACTION
SET precedence = precedence + 2
WHERE action_type_id = "VIEW_EDIT"
ORDER BY precedence DESC
;

SET foreign_key_checks = 0
;

UPDATE ACTION
SET id = "VIEW_AS_APPLICANT"
WHERE id = "VIEW"
;

INSERT INTO ACTION (id, action_type_id, precedence)
VALUES("VIEW_AS_RECRUITER", "VIEW_EDIT", 1),
	("VIEW_AS_REFEREE", "VIEW_EDIT", 0)
;

UPDATE APPLICATION_FORM_ACTION_OPTIONAL
SET action_id = "VIEW_AS_APPLICANT"
WHERE action_id = "VIEW"
	AND application_role_id = "APPLICANT"
;

UPDATE APPLICATION_FORM_ACTION_OPTIONAL
SET action_id = "VIEW_AS_REFEREE"
WHERE action_id = "VIEW"
	AND application_role_id = "REFEREE"
;

UPDATE APPLICATION_FORM_ACTION_OPTIONAL
SET action_id = "VIEW_AS_RECRUITER"
WHERE action_id = "VIEW"
;

SET foreign_key_checks = 1
;
