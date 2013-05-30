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
		var $row = $(this).closest('tr');
		$row.remove();
		checkTableForm();
		updateRegistryForm();
		submitRegistryUsers();
	});
	
	/* Add button. */
	$('#registryUserAdd').click(function()
	{
		displaytableForm();
		var errors = false;
		$('#section-registryusers div.alert-error').remove();
		
		// Allow a maximum of three users.
		var user_count = $('#registryUsers tr').length;
		if (user_count >= 3)
		{
			$('#reg-email').after('<div class="alert alert-error"> <i class="icon-warning-sign"> Only three registry users can be specified.</div>');
			return;
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
			updateRegistryForm();
			submitRegistryUsers();
		}
	});
	
	
});

function checkTableForm() {
	var rowCount = $('#registryUsers tr').length;
	if (rowCount == 0) {
		$('#registryUsers').css('display', 'none');
	} else {
		$('#registryUsers').css('display', 'table');
	}
}
function updateRegistryForm()
{
var user_count = $('#registryUsers tr').length;
if (user_count >= 3)
{
	$('#reg-firstname').attr('disabled', 'disabled');
	$('#reg-lastname').attr('disabled', 'disabled');
	$('#reg-email').attr('disabled', 'disabled');
	
	$('#reg-firstname').parent().parent().children('.plain-label').addClass('grey-label');
	$('#reg-lastname').parent().parent().children('.plain-label').addClass('grey-label');
	$('#reg-email').parent().parent().children('.plain-label').addClass('grey-label');
	$('#registryUserAdd').attr('disabled', 'disabled');
}
else
{
	$('#reg-firstname').removeAttr('disabled');
	$('#reg-lastname').removeAttr('disabled');
	$('#reg-email').removeAttr('disabled');
	$('#registryUserAdd').removeAttr('disabled');
	$('#reg-firstname').parent().parent().children('.plain-label').removeClass('grey-label');
	$('#reg-lastname').parent().parent().children('.plain-label').removeClass('grey-label');
	$('#reg-email').parent().parent().children('.plain-label').removeClass('grey-label');
}
checkTableForm();
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
	generalTabing();
}

function validateEmail(email)
{ 
    var re = /^[A-Za-z0-9\!\#-'\*\+\-\=\?\^_`\{-~]+(\.[A-Za-z0-9\!\#-'\*\+\-\=\?\^_`\{-~]+)*@[A-Za-z0-9\!\#-'\*\+\-\=\?\^_`\{-~]+(\.[A-Za-z0-9\!\#-'\*\+\-\=\?\^_`\{-~]+)*$/;
    var pattern = new RegExp(re);
    var result = pattern.test(email);
    return result;
} 

function submitRegistryUsers()
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
			},
			complete: function()
			{
				$('#ajaxloader').fadeOut('fast');
				updateRegistryForm();
			}
		});
	}
