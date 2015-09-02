rename table entity_import to imported_entity_type
;

alter table imported_entity_type
	change column imported_entity_type id varchar(50) not null
;

alter table imported_program
	drop column homepage
;
