update registered_user u set primary_account_id = null
where u.id = u.primary_account_id
;