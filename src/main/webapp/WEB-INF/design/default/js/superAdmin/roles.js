$(document).ready(function()
{
	
	autosuggest($("#firstName"), $("#lastName"), $("#email"));

	// ------------------------------------------------------------------------------
	// Load a list of assigned users for a specific programme.
	// ------------------------------------------------------------------------------
	loadUsersForProgram();
	$('#programs').change(function()
	{
		$('#editRoles .alert-error').remove();
		loadUsersForProgram();
	});
	
	// ------------------------------------------------------------------------------
	// Clear button.
	// ------------------------------------------------------------------------------
	$('#clear').click(function()
	{
		var $form = $(this).closest('form');
		clearForm($form);
	});

	
	// ------------------------------------------------------------------------------
	// Deleting a user's assignments.
	// ------------------------------------------------------------------------------
	$('#existingUsers').on('click', 'a.button-delete', function(event)
	{
	  event.preventDefault();		
	 	var user = $(this).attr("id").replace("remove_", "");
	 	$('#deleteFromUser').val(user);
	 	$('#deleteFromProgram').val($('#programs').val());
	 	$('#removeForm').submit();
	});
	
});


function loadUsersForProgram()
{
	$('#ajaxloader').show();
	
	$.ajax({
		 type: 'GET',
		 statusCode: {
			  401: function()
				{
				  window.location.reload();
			  },
			  500: function() {
				  window.location.href = "/pgadmissions/error";
			  },
			  404: function() {
				  window.location.href = "/pgadmissions/404";
			  },
			  400: function() {
				  window.location.href = "/pgadmissions/400";
			  },				  
			  403: function() {
				  window.location.href = "/pgadmissions/404";
			  }
		  },
	 		url:"/pgadmissions/manageUsers/program",
			data:{
				programCode : $('#programs').val(),						
				cacheBreaker: new Date().getTime() 
			},
			success: function(data)
			{
				$('#existingUsers').html(data)
				                   .css({ height: 'auto' });
				if ($(data).find('div.scroll tr').length == 0)
				{
					$('#existingUsers table').hide();
				}
				else
				{
					$('#existingUsers table').show();
				}
				addToolTips();
			},
			complete: function()
			{
				$('#ajaxloader').fadeOut('fast');
			}
	});	
}