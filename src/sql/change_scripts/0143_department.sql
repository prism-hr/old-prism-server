alter table department
	add unique index (code),
	add column sequence_identifier varchar(23),
	add unique index (sequence_identifier),
	drop index user_id,
	add index (user_id, sequence_identifier),
	add index (institution_id, sequence_identifier),
	add index (title, sequence_identifier),
	add column advert_id int(10) unsigned after institution_id,
	add index (advert_id, sequence_identifier),
	add column application_rating_count int(10) unsigned,
	add index (application_rating_count, sequence_identifier),
	add column application_rating_frequency decimal(10, 2) unsigned,
	add index (application_rating_frequency, sequence_identifier),
	add column application_rating_average decimal(3, 2) unsigned,
	add index (application_rating_average, sequence_identifier), 
	add column state_id varchar(100),
	add index (state_id, sequence_identifier),
	add foreign key (state_id) references state (id),
	add column previous_state_id varchar(100),
	add index (previous_state_id, sequence_identifier),
	add foreign key (previous_state_id) references state (id),
	add column due_date date,
	add index (due_date),
	add column created_timestamp datetime not null,
	add index (created_timestamp, sequence_identifier),
	add column updated_timestamp datetime not null,
	add index (updated_timestamp, sequence_identifier),
	add column updated_timestamp_sitemap datetime not null,
	add index (updated_timestamp_sitemap),
	add column last_reminded_request_individual date,
	add index (last_reminded_request_individual),
	add column last_reminded_request_syndicated date,
	add index (last_reminded_request_syndicated),
	add column last_notified_update_syndicated date,
	add index (last_notified_update_syndicated),
	add column workflow_property_configuration_version int(10) unsigned	
;

alter table department
	modify column sequence_identifier varchar(23) after workflow_property_configuration_version
;

alter table institution
	drop foreign key institution_ibfk_6
;

alter table institution
	drop index institution_domicile_id_2,
	drop column institution_domicile_id
;


alter table project
	modify column advert_id int(10) unsigned not null
;

alter table program
	modify column advert_id int(10) unsigned not null
;

alter table institution
	modify column advert_id int(10) unsigned not null,
	modify column system_id int(10) unsigned not null
;

alter table program
	modify column system_id int(10) unsigned not null,
	modify column institution_id int(10) unsigned not null
;

alter table department
	modify column user_id int(10) unsigned not null after imported_code,
	add column system_id int(10) unsigned after imported_code,
	add index (system_id, sequence_identifier),
	add foreign key (system_id) references system (id)
;

update department
set system_id = (
	select id
	from system)
;

alter table department
	modify column system_id int(10) unsigned not null
;

update program
inner join department
	on program.department_id = department.id
set program.department_id = null
where department.id in (
	select id
	from department
	where title in ("Che", "medical Physic"))
;


update project
inner join department
	on project.department_id = department.id
set project.department_id = null
where department.id in (
	select id
	from department
	where title in ("Che", "medical Physic"))
;

delete
from department
where title in ("Che", "medical Physic")
;

alter table advert
	change column institution_address_id advert_address_id int(10) unsigned
;

alter table advert
	add column department_id int(10) unsigned
;

insert into advert (title, department_id)
	select title, id
	from department
;

update department inner join advert
	on department.id = advert.department_id
set department.advert_id = advert.id
;

alter table advert
	drop column department_id
;

alter table project
	modify column user_id int(10) unsigned not null after imported_code
;

alter table department
	modify column user_id int(10) unsigned not null after imported_code
;

update department
set code = concat("PRiSM-DT-", lpad(id, 10, "0"))
;

update department
set created_timestamp = current_timestamp(),
	updated_timestamp = created_timestamp,
	sequence_identifier = concat(unix_timestamp(updated_timestamp), lpad(id, 10, "0"))
;

update department
set updated_timestamp_sitemap = updated_timestamp
;

update department inner join (
	select application.department_id as department_id, 
		sum(application.application_rating_count) as application_rating_count,
		round(avg(application.application_rating_count), 2) as application_rating_frequency,
		round(avg(application.application_rating_average), 2) as application_rating_average
	from application
	where application.department_id is not null
	group by application.department_id) as application_rating
	on department.id = application_rating.department_id
set department.application_rating_count = application_rating.application_rating_count,
	department.application_rating_frequency = application_rating.application_rating_frequency,
	department.application_rating_average = application_rating.application_rating_average
;

update program
set department_id = (
	select id
	from department
	where title = "Medical Physics and Biomedical Engineering")
where department_id = (
	select id
	from department
	where title = "Medical Physics and Bioengineering")
;

delete
from department
where title = "Medical Physics and Bioengineering"
;

create procedure department_address()
begin
	set @department_id = (
		select min(id)
		from department);
		
	iteration: while @department_id is not null do
		insert into advert_address(imported_advert_domicile_id, address_line_1, address_line_2, 
			address_town, address_region, address_code, google_id, location_x, location_y)
			select advert_address.imported_advert_domicile_id, advert_address.address_line_1,
				advert_address.address_line_2, advert_address.address_town, advert_address.address_region,
				advert_address.address_code, advert_address.google_id, advert_address.location_x,
				advert_address.location_y
			from advert_address inner join advert
				on advert_address.id = advert.advert_address_id
			inner join institution
				on advert.id = institution.advert_id
			where institution.id = (
				select department.institution_id
				from department
				where department.id = @department_id);
				
		update advert inner join department
			on advert.id = department.advert_id
		set advert.advert_address_id = last_insert_id()
		where department.id = @department_id;
	
		set @department_id = (
			select min(id)
			from department
			where id > @department_id); 
	
		if @department_id is null then
			leave iteration;		
		end if;
	end while;

end
;

call department_address()
;

drop procedure department_address
;
