ALTER TABLE COMMENT
	DROP COLUMN application_export_response,
	DROP COLUMN application_export_request,
	CHANGE COLUMN application_export_error application_export_exception TEXT
;
