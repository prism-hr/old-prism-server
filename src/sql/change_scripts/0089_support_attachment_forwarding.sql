alter table message_document
	drop index message_id,
	add unique index (message_id, document_id),
	drop index document_id,
	add index (document_id)
;
