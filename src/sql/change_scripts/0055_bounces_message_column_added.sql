alter table user
  drop column email_valid,
  add column email_bounced_message text
;
