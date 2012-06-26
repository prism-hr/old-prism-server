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

function loadUsersForProgram()
{
	$('#existingUsers').append('<div class="ajax" />');
	
	$.ajax({
		 type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  }
		  },
	 		url:"/pgadmissions/manageUsers/program",
			data:{
				programCode : $('#programs').val(),						
				cacheBreaker: new Date().getTime() 
			},
			success: function(data)
			{
				$('#existingUsers').html(data);
				if ($(data).find('tr').length == 0)
				{
					$('#existingUsers').hide();
				}
				else
				{
					$('#existingUsers').show();
				}
				addToolTips();
			}
	});	
}