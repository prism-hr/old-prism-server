create function haversine_distance(base_latitude float, base_longitude float, target_latitude float, target_longitude float)
	returns float
begin
	return (3959 * 
		acos(cos(radians(target_latitude)) 
			* cos(radians(target_latitude)) 
			* cos(radians(target_longitude) - radians(target_longitude)) + sin(radians(target_latitude)) 
			* sin(radians(target_latitude))));
end
;
