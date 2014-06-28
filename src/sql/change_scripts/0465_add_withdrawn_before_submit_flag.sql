ALTER TABLE APPLICATION_FORM ADD COLUMN withdrawn_before_submit TINYINT(1) NULL DEFAULT '0' AFTER suppress_state_change_notifications
;
