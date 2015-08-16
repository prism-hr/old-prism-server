create table entity_import (
	imported_entity_type varchar(50) not null,
	last_imported_timestamp timestamp,
	primary key (imported_entity_type))
collate = utf8_general_ci
engine = innodb
;
