update application
set application_week = week(created_timestamp, 3)
;

create procedure application_week_sequence()
begin
	set @baseline = (
		select current_date());
		
	set @baseline_year = (
		select year(@baseline));

	set @application_id = (
		select min(id)
		from application);
		
	loop1: while @application_id is not null do 
		set @business_year_start_month = (
			select institution.business_year_start_month
			from application inner join institution   
				on application.institution_id = institution.id
			where application.id = @application_id);
		
		set @business_year_start_date = (
			select date(concat(@baseline_year, "-", @business_year_start_month, - "01")));
			
		if @business_year_start_date > @baseline then
			set @business_year_start_date = (
				select @business_year_start_date - interval 1 year);
		end if;
		
		set @business_year_start_week = (
			select week(@business_year_start_date, 3));
		
		set @last_possible_day_of_week_year = (
			select date(concat(@baseline_year, "-", 12, "-31")) + interval 4 day);
			
		set @maximum_week_of_year = (select null);	
		set @loop_pointer = (select 7);
		loop2: while @loop_pointer > 0 do
			set @maximum_week_of_year = (
				select week(@last_possible_day_of_week_year, 3));
			
			if @maximum_week_of_year > 1 then
				leave loop2;
			end if; 	
				
			set @last_possible_day_of_week_year = (
				select @last_possible_day_of_week_year - interval 1 day); 
			set @loop_pointer = (select @loop_pointer - 1);
		end while;
		
		update application
		set application.application_week_sequence = 
			if (application.application_week > @business_year_start_week,
				application.application_week - (@business_year_start_week - 1),
				application.application_week + (@maximum_week_of_year - (@business_year_start_week - 1)))
		where application.id = @application_id; 
		
		set @application_id = (
			select min(id)
			from application
			where id > @application_id);
		
		if @application_id is null then
			leave loop1;
		end if;
	end while;
end
;

call application_week_sequence()
;

drop procedure application_week_sequence
;
