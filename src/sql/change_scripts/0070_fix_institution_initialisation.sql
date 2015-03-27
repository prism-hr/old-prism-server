alter table user 
	drop foreign key user_ibfk_3,
	drop column latest_creation_scope_id,
	modify column email_bounced_message text after email
;
