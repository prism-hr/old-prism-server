$(document).ready(function()
{

	// -----------------------------------------------------------------------------
	// Registry users
	// -----------------------------------------------------------------------------
	
	// Delete buttons

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
		}
	});
	
	
	// -----------------------------------------------------------------------------
	// Submit button.
	// -----------------------------------------------------------------------------
	$('#submitRUBtn').click(function()
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
		
		// STAGE DURATION
		$("#stagesDuration").html('');
		var stageValidationErrors			= appendStagesJSON();
		var stages										= $('[input[name="stagesDuration"]');
		var reminderValidationErrors	= validateReminderInterval();
		var postData = {
				id:       $('#reminderIntervalId').val(),
				duration: $('#reminderIntervalDuration').val(),
				unit:     $('#reminderUnit').val()
			};
		
		if (!stageValidationErrors && !reminderValidationErrors)
		{
			// Post the data.
			$('#configForm').append('<div class="ajax" />');
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
					url:  "/pgadmissions/configuration/", 
					data: $.param(postData) +"&" +  $('#regContactData input.registryUsers').serialize()  +"&" +  stages.serialize(),
					success: function(data)
					{
						$('#configsection').html(data);
						addToolTips();
					},
					complete: function()
					{
						$('#configForm div.ajax').remove();
						updateRegistryForm();
					}
				});
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

function validateReminderInterval()
{
	var validationErrors = false;
	var reminderIntervalDuration = $('#reminderIntervalDuration').val();
	var reminderIntervalUnit = $('#reminderUnit').val();
	if (isNaN(reminderIntervalDuration) || reminderIntervalDuration == "" || reminderIntervalDuration <= 0)
	{
		$("#invalidDurationInterval span").html('You must enter a whole number greater that 0.');
		$("#invalidDurationInterval").show();
		validationErrors = true;
	}
	else
	{
	    $("#invalidDurationInterval span").html('');
	    $("#invalidDurationInterval").hide();
	}
	if (reminderIntervalUnit == "" && !validationErrors)
	{
	    $("#invalidUnitInterval span").html('You must make a selection.');
		$("#invalidUnitInterval").show();
		validationErrors = true;
	}
	else
	{
	    $("#invalidUnitInterval span").html('');
	    $("#invalidUnitInterval").hide();
	}
	return validationErrors;
}


function appendStagesJSON()
{
	var validationErrors = false;
	var stages = document.getElementById("stages");
	for (var i=0; i<stages.length; i++)
	{	
		var isStageDurationError = false;
		var stageName = stages.options[i].value;
		var stageDuration = $('#' + stageName + '_duration').val();
		var stageUnit = $('#'+ stageName + '_unit').val();
		if (isNaN(stageDuration) || stageDuration == "" || stageDuration <= 0)
		{
			$("#"+stageName+"_invalidDuration span").html('You must enter a whole number greater that 0.');
			$("#"+stageName+"_invalidDuration").show();
			isStageDurationError = true;
			validationErrors = true;
		}
		else
		{
		    $("#"+stageName+"_invalidDuration span").html('');
		    $("#"+stageName+"_invalidDuration").hide();
		}
		if (stageUnit == "" &&  !isStageDurationError)
		{
		    $("#"+stageName+"_invalidUnit span").html('You must make a selection.');
			$("#"+stageName+"_invalidUnit").show();
			validationErrors = true;
		}
		else
		{
		    $("#"+stageName+"_invalidUnit span").html('');
		    $("#"+stageName+"_invalidUnit").hide();
		}
		$("#stagesDuration").append('<input type="hidden" name="stagesDuration" id= "stagesDuration"  value=' +"'" + '{"stage":"' +  stageName+ '","duration":"' + stageDuration + '","unit":"' + stageUnit + '"} ' + "'" + "/>");
	}
	return validationErrors;
}


function appendRegistryUsersJSON()
{
	var validationErrors = false;
	//todo: change check for == "" with isBlank equivalent
	//if first user is filled
	if ($('#1_regUserfirstname').val() != "" && $('#1_regUserLastname').val() != "" && validateEmail($('#1_regUserEmail').val()))
	{
		$("span[name='firstuserInvalid']").html('');
		$("span[name='firstuserInvalid']").hide();
		$("#registryUsers").append('<input type="hidden" name="registryUsers" id= "registryUsers"  value=' +"'" + '{"id":"' +  $('#1_regUserId').val() + '","firstname":"' + $('#1_regUserfirstname').val() + '","lastname":"' +  $('#1_regUserLastname').val() + '","email":"' + $('#1_regUserEmail').val()  + '"} ' + "'" + "/>");
	}
	else
	{
		if (!validateEmail($('#1_regUserEmail').val()))
		{
			$("span[name='firstuserInvalid']").html("You must enter a valid email address.");
		}
		if ($('#1_regUserfirstname').val() == "" || $('#1_regUserLastname').val() =="")
		{
			$("span[name='firstuserInvalid']").html('You must make an entry.');
		}
		$("span[name='firstuserInvalid']").show();
		validationErrors = true;
	}
	
	if ($("div[id='secondRegistryUser']").is(':visible'))
	{
		if ($('#2_regUserfirstname').val() != "" && $('#2_regUserLastname').val() != "" && validateEmail($('#2_regUserEmail').val()))
		{
			$("span[name='seconduserInvalid']").html('');
			$("span[name='seconduserInvalid']").hide();
			$("#registryUsers").append('<input type="hidden" name="registryUsers" id= "registryUsers"  value=' +"'" + '{"id":"' +  $('#2_regUserId').val() + '","firstname":"' + $('#2_regUserfirstname').val() + '","lastname":"' +  $('#2_regUserLastname').val() + '","email":"' + $('#2_regUserEmail').val()  + '"} ' + "'" + "/>");
		}
		else
		{
			if (!validateEmail($('#2_regUserEmail').val()))
			{
				$("span[name='seconduserInvalid']").html("You must enter a valid email address.");
			}
			if ($('#2_regUserfirstname').val() == "" || $('#2_regUserLastname').val() =="")
			{
				$("span[name='seconduserInvalid']").html('You must make an entry.');
			}
			$("span[name='seconduserInvalid']").show();
			validationErrors = true;
		}
	}
	//if third user is visible and filled
	if ($("div[id='thirdRegistryUser']").is(':visible'))
	{
		if ($('#3_regUserfirstname').val() != "" && $('#3_regUserLastname').val() != "" && validateEmail($('#3_regUserEmail').val()))
		{
			$("span[name='thirduserInvalid']").html('');
			$("span[name='thirduserInvalid']").hide();
			$("#registryUsers").append('<input type="hidden" name="registryUsers" id= "registryUsers"  value=' +"'" + '{"id":"' +  $('#3_regUserId').val() + '","firstname":"' + $('#3_regUserfirstname').val() + '","lastname":"' +  $('#3_regUserLastname').val() + '","email":"' + $('#3_regUserEmail').val()  + '"} ' + "'" + "/>");
		}
		else
		{
			if (!validateEmail($('#3_regUserEmail').val()))
			{
				$("span[name='thirduserInvalid']").html("You must enter a valid email address.");
			}
			if ($('#3_regUserfirstname').val() == "" || $('#3_regUserLastname').val() =="")
			{
				$("span[name='thirduserInvalid']").html('Please specify all entries. ');
			}
			$("span[name='thirduserInvalid']").show();
			validationErrors = true;
		}
	}
	return validationErrors;
	
	$('button.apply').click(function()
	{
		$('#program').val(this.id);
		if ($('#'+this.id+'_deadline').html() == "12-Dec-2012")
		{
			$('#program').val("12-Dec-2012");
		}
		$('#applyForm').submit();
	});
	
}


function validateEmail(email)
{ 
    var re = /^[A-Za-z0-9\!\#-'\*\+\-\=\?\^_`\{-~]+(\.[A-Za-z0-9\!\#-'\*\+\-\=\?\^_`\{-~]+)*@[A-Za-z0-9\!\#-'\*\+\-\=\?\^_`\{-~]+(\.[A-Za-z0-9\!\#-'\*\+\-\=\?\^_`\{-~]+)*$/;
    var pattern = new RegExp(re);
    var result = pattern.test(email);
    return result;
} 

function getStageDurationData(){
	$("#stagesDuration").html('');
	var validationErrors = appendStagesJSON();
	var stages = $('[input[name="stagesDuration"]');
	if (!validationErrors)
	{
		return stages.serialize();
	}
}
