UPDATE APPLICATION_FORM_USER_ROLE
SET assigned_timestamp = NOW()
WHERE assigned_timestamp = "0000-00-00 00:00:00"
;

UPDATE APPLICATION_FORM_ACTION_REQUIRED
SET assigned_timestamp = NOW()
WHERE assigned_timestamp = "0000-00-00 00:00:00"
;