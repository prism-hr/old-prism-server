update imported_entity_feed
set last_imported_timestamp = null
where institution_id = 6856
	and imported_entity_type = "REJECTION_REASON"
;

alter table imported_entity
	modify column name TEXT not null
;

alter table imported_institution
	modify column name TEXT not null
;

alter table imported_language_qualification_type
	modify column name TEXT not null
;
