alter table application
	modify column application_month_sequence int(2) unsigned,
	modify column application_week_sequence int(2) unsigned
;

update application
set application_month_sequence = null,
	application_week_sequence = null
;

alter table application
	modify column application_month_sequence date,
	modify column application_week_sequence date
;

update application
set application_month_sequence = date(concat(year(created_timestamp), "-", month(created_timestamp), "-01"))
;

create procedure application_week_monday()
begin
	set @application_id = (
		select id
		from application
		order by id
		limit 1);
		
	loop1: while @application_id is not null do 
		set @created_timestamp = (
			select created_timestamp
			from application
			where id = @application_id);
			
		set @created_timestamp_day_of_week = (
			select dayofweek(@created_timestamp));
			
		set @created_timestamp_day_of_week_shift = (
			select if(@created_timestamp_day_of_week = 1, 6, (@created_timestamp_day_of_week - 2)));
				
		update application
		set application_week_sequence = @created_timestamp - interval @created_timestamp_day_of_week_shift day;
		
		set @application_id = (
			select id
			from application
			where id > @application_id
			order by id
			limit 1);
			
		if @application_id is null then
			leave loop1;
		end if;		
	end while;
end
;

call application_week_monday()
;

drop procedure application_week_monday
;
