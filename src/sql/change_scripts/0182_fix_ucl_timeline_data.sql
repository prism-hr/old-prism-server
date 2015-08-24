update comment
set action_id = "SYSTEM_CREATE_INSTITUTION",
	state_id = "INSTITUTION_APPROVED"
where institution_id = 5243
	and action_id = "INSTITUTION_COMPLETE_APPROVAL_STAGE"
;
