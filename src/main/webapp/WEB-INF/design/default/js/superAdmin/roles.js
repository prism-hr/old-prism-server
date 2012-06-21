$(document).ready(function(){
	 loadUsersForProgram();
	$('#programs').change(function(){
		 loadUsersForProgram()
	});
	
	$('#clear').click(function(){
		 window.location.href="/pgadmissions/manageUsers/edit";
	});
	
	$('#existingUsers').on('click', 'a[name="removeuser"]', function(event){
	    event.preventDefault();		
	 	var user = $(this).attr("id").replace("remove_", "");
	 	$('#deleteFromUser').val(user);
	 	$('#deleteFromProgram').val($('#programs').val());
	 	$('#removeForm').submit();
		
	});
});

function loadUsersForProgram(){
	$.get(
	 		"/pgadmissions/manageUsers/program",
			{
				programCode : $('#programs').val(),						
				cacheBreaker: new Date().getTime() 
			},
			function(data)
			{
				$('#existingUsers').html(data);
			}
		);	
}