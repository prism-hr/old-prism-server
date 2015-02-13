delete 
from program_study_option_instance
;

update imported_entity_feed
set last_imported_timestamp = null
where imported_entity_type = "PROGRAM"
	and institution_id = 5243
;
