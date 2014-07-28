UPDATE ACTION
SET action_category = "VIEW_EDIT_RESOURCE"
WHERE action_category = "VIEW_RESOURCE"
;

UPDATE ACTION
SET action_category = "VIEW_EDIT_RESOURCE"
WHERE action_category = "CONFIGURE_RESOURCE"
;

UPDATE ACTION
SET action_category = "MANAGE_ACCOUNT"
WHERE action_category = "CONFIGURE_ACCOUNT"
;
