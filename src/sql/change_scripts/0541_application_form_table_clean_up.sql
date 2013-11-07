ALTER TABLE APPLICATION_FORM
	DROP FOREIGN KEY admin_requested_fk,
	DROP FOREIGN KEY approver_requested_fk,

	DROP COLUMN admin_requested_registry_id,
	DROP COLUMN pending_approval_restart,
	DROP COLUMN approver_requested_restart_id,
	DROP COLUMN registry_users_notified
;

DROP TABLE APPLICATION_FORM_UPDATE
;

DROP TABLE APPLICATION_FORM_LAST_ACCESS
;
