$(document).ready(function()
{
	
	if($('#2_regUserId').val() == "") {	$("div[id='secondRegistryUser']").hide();	}
	if($('#3_regUserId').val() == "") {	$("div[id='thirdRegistryUser']").hide(); }
	
	
	// -----------------------------------------------------------------------------
	// Clear buttons
	// -----------------------------------------------------------------------------
	$('#cancelDurationBtn, #cancelReminderBtn, #cancelRegistryBtn').click(function()
	{
		var $form = $(this).closest('form');
		clearForm($form);
	});
	
	
	// -----------------------------------------------------------------------------
	// Service Level Commitments
	// -----------------------------------------------------------------------------
	$('#submitDurationStages').click(function()
	{
		$("#stagesDuration").html('');
		var validationErrors = appendStagesJSON();
		var stages = $('[input[name="stagesDuration"]');
		if (!validationErrors)
		{
			$('#section-slc > div').append('<div class="ajax" />');
			$.ajax({
				type: 'POST',
				 statusCode: {
					  401: function() {
						  window.location.reload();
					  }
				  },
				url:"/pgadmissions/configuration/submit", 
				data:stages.serialize(),
				success:function(data)
				{
					$('#section-slc div.ajax').remove();
					//window.location.href = "/pgadmissions/applications";
					addToolTips();
				}
			});
		}
	});
	
	$('#addAnother').click(function()
	{
		if (!$("div[id='secondRegistryUser']").is(':visible'))
		{
			$("div[id='secondRegistryUser']").show();
		}
		else if(!$("div[id='thirdRegistryUser']").is(':visible'))
		{
			$("div[id='thirdRegistryUser']").show();
		}
		else
		{
			$("div[id='secondRegistryUser']").show();
			$("div[id='thirdRegistryUser']").show();
			$("span[name='threeMaxMessage']").html("You cannot specify more than three registry users.");
		}
	});
	

	// -----------------------------------------------------------------------------
	// Task Notifications
	// -----------------------------------------------------------------------------
	$('#submitRIBtn').click(function()
	{
		var validationErrors = validateReminderInterval();
		var postData = {
				id : $('#reminderIntervalId').val(),
				duration : $('#reminderIntervalDuration').val(),
				unit : $('#reminderUnit').val()
			};
		if (!validationErrors)
		{
			$('#section-reminder > div').append('<div class="ajax" />');
			$.ajax({
				type: 'POST',
				 statusCode: {
					  401: function() {
						  window.location.reload();
					  }
				  },
				url:"/pgadmissions/configuration/submitReminderInterval", 
				data:$.param(postData),
				success:function(data)
				{
					$('#section-reminder div.ajax').remove();
					//window.location.href = "/pgadmissions/applications";
					addToolTips();
				}
			});
		}
	});
	

	// -----------------------------------------------------------------------------
	// Registry users
	// -----------------------------------------------------------------------------
	$('#submitRUBtn').click(function()
	{
		$("#registryUsers").html('');
		var validationErrors = appendRegistryUsersJSON();
		var registryUsers = $('[input[name="registryUsers"]');
		if (!validationErrors)
		{
			$('#section-registry > div').append('<div class="ajax" />');
			$.ajax({
				type: 'POST',
				 statusCode: {
					  401: function() {
						  window.location.reload();
					  }
				  },
				url:"/pgadmissions/configuration/submitRegistryUsers", 
				data:registryUsers.serialize(),
				success:function(data)
				{
					$('#section-registry div.ajax').remove();
					//window.location.href = "/pgadmissions/applications";
					addToolTips();
				}
			});
		}
	});
});


function validateReminderInterval()
{
	var validationErrors = false;
	var reminderIntervalDuration = $('#reminderIntervalDuration').val();
	var reminderIntervalUnit = $('#reminderUnit').val();
	if (isNaN(reminderIntervalDuration) || reminderIntervalDuration == "")
	{
		$("span[name='invalidDurationInterval']").html('You must enter a whole number greater that 0.');
		$("span[name='invalidDurationInterval']").show();
		validationErrors = true;
	}
	else
	{
		$("span[name='invalidDurationInterval']").html('');
		$("span[name='invalidDurationInterval']").hide();
	}
	if (reminderIntervalUnit == "")
	{
		$("span[name='invalidUnitInterval']").html('You must make a selection.');
		$("span[name='invalidUnitInterval']").show();
		validationErrors = true;
	}
	else
	{
		$("span[name='invalidUnitInterval']").html('');
		$("span[name='invalidUnitInterval']").hide();
	}
	return validationErrors;
}


function appendStagesJSON()
{
	var validationErrors = false;
	var stages = document.getElementById("stages");
	for (var i=0; i<stages.length; i++)
	{
		var stageName = stages.options[i].value;
		var stageDuration = $('#' + stageName + '_duration').val();
		var stageUnit = $('#'+ stageName + '_unit').val();
		if (isNaN(stageDuration) || stageDuration == "")
		{
			$("span[name='"+stageName+"_invalidDuration']").html('You must enter a whole number greater that 0.');
			$("span[name='"+stageName+"_invalidDuration']").show();
			validationErrors = true;
		}
		else
		{
			$("span[name='"+stageName+"_invalidDuration']").html('');
			$("span[name='"+stageName+"_invalidDuration']").hide();
		}
		if (stageUnit == "")
		{
			$("span[name='"+stageName+"_invalidUnit']").html('You must make a selection.');
			$("span[name='"+stageName+"_invalidUnit']").show();
			validationErrors = true;
		}
		else
		{
			$("span[name='"+stageName+"_invalidUnit']").html('');
			$("span[name='"+stageName+"_invalidUnit']").hide();
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
	
	function validateEmail(email)
	{ 
		var pattern = new RegExp(/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i);
		var result = pattern.test(email);
		return result;
	} 
	
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
