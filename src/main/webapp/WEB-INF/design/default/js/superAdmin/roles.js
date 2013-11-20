$(document).ready(function()
{
	
	autosuggest($("#editUser #firstName"), $("#editUser #lastName"), $("#editUser #email"));
	autosuggest($("#editSuperadmins #firstName"), $("#editSuperadmins #lastName"), $("#editSuperadmins #email"));
	autosuggest($("#reg-firstname"), $("#reg-lastname"), $("#reg-email"));
	
	// ------------------------------------------------------------------------------
	// Load a list of assigned users for a specific programme.
	// ------------------------------------------------------------------------------

	if ($('#programs').val()!="" && $('#userIsAdmin').val()=="true") {
		loadUsersForProgram();
	}

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
	$('#manageUsers .existingUsers, #manageSuperadmins .existingUsers').on('click', 'a.button-delete', function(event)
	{
	  event.preventDefault();		
	 	var user = $(this).attr("id").replace("remove_", "");
	 	$('#deleteFromUser').val(user);
	 	$('#deleteFromProgram').val($('#programs').val());
	 	$('#removeForm').submit();
	});
	
	
	checkTableForm();
	
	function displaytableForm() {
		$('#registryUsers').css('display', 'table');
	}

	
	$('#registryUsers').on('click', '.button-delete', function()
	{
		var redirectToAppList = $(this).closest('tr').children()[1].textContent.indexOf($('#currentUserEmail').val())!=-1;
		var $row = $(this).closest('tr');
		$row.remove();
		checkTableForm();
		submitRegistryUsers(redirectToAppList);
	});
	
	/* Add button. */
	$('#registryUserAdd').click(function()
	{
		displaytableForm();
		var errors = false;
		$('#addRemoveRegistryUsers div.alert-error').remove();
		
		// no duplicate users.
		if ($('#registryUsers:contains('+$('#reg-email').val()+')').length > 0) {
			$('#reg-email').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> This user already exists.</div>');
			errors = true;
		}
		// Validation on any entered details.
		if (!validateEmail($('#reg-email').val()))
		{
			$('#reg-email').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> You must enter a valid email address.</div>');
			errors = true;
		}
		if ($('#reg-firstname').val() == "")
		{
			$('#reg-firstname').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> You must make an entry.</div>');
			errors = true;
		}
		if ( $('#reg-lastname').val() == "")
		{
			$('#reg-lastname').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> You must make an entry.</div>');
			errors = true;
		}

		if (!errors)
		{
			$('#registryUsers tbody').append('<tr>'
				+ '	<td> <span class="arrow"></span> </td>'
				+ '<td>'
				+ $('#reg-firstname').val() + ' ' + $('#reg-lastname').val() + ' (' + $('#reg-email').val() + ')'
				+ '</td>'
				+ '<td>'
				+ '<button class="button-delete" type="button" data-desc="Remove">Remove</button>'
				+ '<input type="hidden" name="firstname" value="' + $('#reg-firstname').val() + '" />'
				+ '<input type="hidden" name="lastname" value="' + $('#reg-lastname').val() + '" />'
				+ '<input type="hidden" name="email" value="' + $('#reg-email').val() + '" />'
				+ '<input type="hidden" name="id" value="" />'
				+ '</td>'
				+ '</tr>');
			$('#reg-firstname, #reg-lastname, #reg-email').val('');
			submitRegistryUsers();
		}
	});
	
	generalTabing();
});

function checkTableForm() {
	var rowCount = $('#registryUsers tr').length;
	if (rowCount == 0) {
		$('#registryUsers').css('display', 'none');
	} else {
		$('#registryUsers').css('display', 'table');
	}
}


function loadUsersForProgram()
{

	var visibleTabPage = '#'+$('.tab-page:visible').attr('id');
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
				$(visibleTabPage+' .existingUsers').html(data)
				                   .css({ height: 'auto' });
				if ($(data).find('div.scroll tr').length == 0)
				{
					$(visibleTabPage+' .existingUsers table').hide();
				}
				else
				{
					$(visibleTabPage+' .existingUsers table').show();
				}
				addToolTips();
			},
			complete: function()
			{
				$('#ajaxloader').fadeOut('fast');
			}
	});	
	
	/* Tabs */
}

function validateEmail(email)
{ 
    var re = /^[A-Za-z0-9\!\#-'\*\+\-\=\?\^_`\{-~]+(\.[A-Za-z0-9\!\#-'\*\+\-\=\?\^_`\{-~]+)*@[A-Za-z0-9\!\#-'\*\+\-\=\?\^_`\{-~]+(\.[A-Za-z0-9\!\#-'\*\+\-\=\?\^_`\{-~]+)*$/;
    var pattern = new RegExp(re);
    var result = pattern.test(email);
    return result;
} 

function submitRegistryUsers(redirect)
{
	// REGISTRY USERS
	// Remove the hidden fields generated when posting registry user info.
	$('#regContactData input.registryUsers').remove();
	
	// Grab the hidden field values from the table.
	$('#registryUsers tbody tr').each(function()
	{
		var $row			= $(this);
		var id				= $('input[name="id"]', $row).val();
		var firstname	= $('input[name="firstname"]', $row).val();
		var lastname	= $('input[name="lastname"]', $row).val();
		var email			= $('input[name="email"]', $row).val();
		var obj				= '{"id": "' + id + '","firstname": "' + firstname + '", "lastname": "' + lastname + '", "email": "' + email + '"}';
		$('#regContactData').append("<input type='hidden' class='registryUsers' name='registryUsers' value='" + obj + "' />");
	});
		// Post the data.
	$('#ajaxloader').show();
	$.ajax({
			type: 'POST',
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
			url:  "/pgadmissions/manageUsers/edit/saveRegistryUsers", 
			data: $('#regContactData input.registryUsers').serialize(),
			success: function(data)
			{
				$('#configsection').html(data);
				addToolTips();
				if (redirect) {
					window.location="/pgadmissions/applications";
				}
			},
			complete: function()
			{
				$('#ajaxloader').fadeOut('fast');
			}
		});
}