alter table document
	modify column file_content longblob
;

alter table document
	add column exported int(1) unsigned default 0 after content_type
;

alter table document
	modify column exported int(1) not null
;

alter table document
	add index (exported)
;

alter table system
	add column amazon_access_key varchar(50) after cipher_salt,
	add column amazon_secret_key varchar(50) after amazon_access_key
;

alter table system
	add column last_amazon_cleanup_date DATE after amazon_secret_key
;
