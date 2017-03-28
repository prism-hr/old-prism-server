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
WHERE notification_definition_id LIKE '%SPONSOR%';

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
  DROP COLUMN sponsorship_rejection_id;

# Drop the comment columns
