create procedure admin_delete_institution(in in_institution_id INT)
begin

	delete
	from advert_resource_selected
	where advert_id in (
		select id
		from advert
		where institution_id = in_institution_id);
	
	delete
	from advert_resource
	where advert_id in (
		select id
		from advert
		where institution_id = in_institution_id);
	
	delete
	from advert_subject_area
	where advert_id in (
		select id
		from advert
		where institution_id = in_institution_id);
	
	update advert
	set institution_id = null
	where institution_id = in_institution_id;
	
	delete
	from advert
	where institution_id = in_institution_id;
	
	delete
	from comment_transition_state
	where comment_id in (
		select id
		from comment
		where institution_id = in_institution_id);
	
	delete
	from comment_state
	where comment_id in (
		select id
		from comment
		where institution_id = in_institution_id);
	
	delete
	from comment_assigned_user
	where comment_id in (
		select id
		from comment
		where institution_id = in_institution_id);
	
	delete
	from comment
	where institution_id = in_institution_id;
	
	delete
	from resource_previous_state
	where institution_id = in_institution_id;
	
	delete
	from user_role
	where institution_id = in_institution_id;
	
	delete
	from resource_state
	where institution_id = in_institution_id;
	
	delete
	from resource_condition
	where institution_id = in_institution_id;
	
	delete
	from institution
	where id = in_institution_id;

end
;

create procedure admin_reindex_subject_area()
begin
	
	update imported_subject_area
	set index_score = null;
	
	update imported_program
	set indexed = false;
	
	update imported_institution
	set indexed = false;
	
end
