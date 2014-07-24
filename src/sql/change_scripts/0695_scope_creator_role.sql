ALTER TABLE ROLE
	CHANGE COLUMN is_scope_owner is_scope_creator INT(1) UNSIGNED NOT NULL
;

UPDATE ACTION
SET action_category = "PROPAGATE_RESOURCE"
WHERE action_type = "SYSTEM_PROPAGATION"
;

UPDATE ACTION
SET action_category = "EXPEDITE_RESOURCE"
WHERE action_category = "ESCALATE_RESOURCE"
;

UPDATE ACTION
SET action_category = "IMPORT_RESOURCE"
WHERE action_type = "SYSTEM_IMPORT"
;

UPDATE ACTION
SET action_type = "SYSTEM_INVOCATION"
WHERE action_type != "USER_INVOCATION"
;
