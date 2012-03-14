$(document).ready(function(){

	
	$('a[name="deleteButton"]').click( function(){	
		$(this).parent("form").submit();
	});

	
});
