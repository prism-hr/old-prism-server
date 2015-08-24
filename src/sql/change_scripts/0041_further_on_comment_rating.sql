update comment inner join action
	 on comment.action_id = action.id
set comment.application_rating = 3.00
where comment.application_rating is null
	and action.rating_action is true
;
