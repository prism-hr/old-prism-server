DELETE
FROM resource_condition
WHERE action_condition LIKE '%SPONSOR%'
;

DELETE
FROM user_role
WHERE role_id LIKE '%SPONSOR%'
;

DELETE
FROM user_notification
WHERE notification_definition_id LIKE '%SPONSOR%'
;

DELETE
FROM role_transition
WHERE role_id like '%SPONSOR%'
;

DELETE
FROM state_action_assignment
WHERE role_id like '%SPONSOR%'
;

DELETE
FROM state_action_notification
WHERE role_id like '%SPONSOR%'
;

DELETE
FROM comment_assigned_user
WHERE role_id like '%SPONSOR%'
;

DELETE
FROM role
WHERE role_category = 'SPONSOR'
;

DELETE
FROM state_action_notification
WHERE state_action_id IN (
  SELECT id
  FROM state_action
  WHERE action_id like '%SPONSOR%'
)
;

DELETE
FROM state_transition
WHERE state_action_id IN (
  SELECT id
  FROM state_action
  WHERE action_id like '%SPONSOR%'
)
;

DELETE
FROM state_action
WHERE action_id like '%SPONSOR%'
;

DELETE
FROM action
WHERE id like '%SPONSOR%'
;

DELETE
FROM notification_configuration
WHERE notification_definition_id like '%SPONSOR%'
;

DELETE
FROM notification_definition
WHERE id like '%SPONSOR%'
;

ALTER TABLE advert
  DROP COLUMN sponsorship_purpose,
  DROP COLUMN sponsorship_target,
  DROP COLUMN sponsorship_secured
;

ALTER TABLE COMMENT
  DROP FOREIGN KEY comment_ibfk_18,
  DROP INDEX institution_sponsor_id,
  DROP FOREIGN KEY comment_ibfk_19,
  DROP INDEX sponsorship_rejection_id,
  DROP COLUMN sponsorship_currency_specified,
  DROP COLUMN sponsorship_currency_converted,
  DROP COLUMN sponsorship_amount_specified,
  DROP COLUMN sponsorship_amount_converted,
  DROP COLUMN sponsorship_target_fulfilled,
  DROP COLUMN sponsorship_rejection_id
;
