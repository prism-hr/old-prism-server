update imported_entity
set name = upper(name)
where imported_entity_type in ("IMPORTED_OPPORTUNITY_TYPE",
	"IMPORTED_STUDY_OPTION")
;
