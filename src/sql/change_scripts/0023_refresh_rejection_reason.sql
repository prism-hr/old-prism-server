update imported_entity_feed
set last_imported_timestamp = null
where imported_entity_type = "REJECTION_REASON"
;
