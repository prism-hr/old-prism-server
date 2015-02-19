alter table comment
	add column application_export_exception_condition VARCHAR(100) after application_export_exception,
	add index (application_export_exception_condition)
;

alter table comment
	drop column creator_ip_address
;
