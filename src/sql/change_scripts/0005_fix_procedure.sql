create procedure procedure_clear_schema()
begin

	create temporary table prism_table (
		id int(10) unsigned not null auto_increment,
		name varchar(255) not null,
		primary key (id),
		unique index (name))
	collate = utf8_general_ci
		engine = memory;

	insert into prism_table (name)
		select information_schema.tables.table_name
		from information_schema.tables
		where information_schema.tables.table_schema = "prism"
			and information_schema.tables.table_name != "changelog";

	set session foreign_key_checks = 0;

	set @current_table = (
		select id
		from prism_table
		order by id
		limit 1);

	iteration: while @current_table is not null do
		set @current_table_name = (
			select name
			from prism_table
			where id = @current_table);

		set @truncate_table = concat("truncate table ", @current_table_name);
		prepare truncate_table_statement from @truncate_table;
		execute truncate_table_statement;
		deallocate prepare truncate_table_statement;

		set @current_table = (
			select id
			from prism_table
			where id > @current_table
			order by id
			limit 1);

		if @current_table is null then
			leave iteration;
		end if;
	end while;

	drop table prism_table;

	set session foreign_key_checks = 1;

end
;
