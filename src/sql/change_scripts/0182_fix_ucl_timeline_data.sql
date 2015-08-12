update comment
set action_id = "SYSTEM_IMPORT_INSTITUTION",
	state_id = "INSTITUTION_APPROVED"
where institution_id = 5243
	and action_id = "INSTITUTION_COMPLETE_APPROVAL_STAGE"
;

update comment
set action_id = "SYSTEM_CREATE_INSTITUTION",
	content = null
where institution_id = 5243
	and action_id = "SYSTEM_IMPORT_INSTITUTION"
;
