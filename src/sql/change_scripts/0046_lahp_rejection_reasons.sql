update imported_entity_feed
set location = replace(location, "defaultEntities", "lahpEntities"),
	last_imported_timestamp = null
where institution_id = 6856
	and imported_entity_type = "REJECTION_REASON"
;
