create table entity_import (
	imported_entity_type varchar(50) not null unique,
	last_imported_timestamp timestamp)
collate = utf8_general_ci
engine = innodb
;
