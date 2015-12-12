create table address_location_part (
    id int(10) unsigned not null auto_increment,
    parent_id int(10) unsigned,
    name text not null,
    name_index varchar(255) not null,
    primary key (id),
    index (parent_id),
    index (name_index))
collate = utf8_general_ci
engine = innodb
;

create table address_location (
    address_id int(10) unsigned not null,
    address_location_part_id int(10) unsigned not null,
    primary key (address_id, address_location_part_id),
    index (address_location_part_id),
    foreign key (address_id) references address (id),
    foreign key (address_location_part_id) references address_location_part (id))
collate = utf8_general_ci
engine = innodb
;

alter table address_location_part
    add column address_location_part_type varchar(50) not null after parent_id,
    add index (address_location_part_type)
;

alter table address_location_part
    drop index parent_id,
    add unique index (parent_id, address_location_part_type, name_index)
;
