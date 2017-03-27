DELETE
FROM resource_condition
WHERE action_condition LIKE '%SPONSOR%'
;

DELETE
FROM user_role
WHERE role_id LIKE '%SPONSOR%'
;

ALTER TABLE advert
  DROP COLUMN sponsorship_purpose,
  DROP COLUMN sponsorship_target,
  DROP COLUMN sponsorship_secured
;

# Drop the comment columns
