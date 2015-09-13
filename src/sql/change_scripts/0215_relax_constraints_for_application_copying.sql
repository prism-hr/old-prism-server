alter table application_program_detail
	modify column imported_referral_source_id int(10) unsigned
;

alter table application_language_qualification
	modify column imported_language_qualification_type_id int(10) unsigned
;

alter table application_qualification
	modify column imported_program_id int(10) unsigned
;
