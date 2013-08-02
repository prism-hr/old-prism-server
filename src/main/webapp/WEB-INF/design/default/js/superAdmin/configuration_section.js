$(document).ready(function()
{

	// -----------------------------------------------------------------------------
	// Submit button.
	// -----------------------------------------------------------------------------
	$('#submitRUBtn').click(function()
	{
		
		$("#stagesDuration").html('');
		var stageValidationErrors		= appendStagesJSON();
		var stagesInput					= $('[input[name="stagesDuration"]');
		
		$('#reminderIntervals').html('');
		var reminderValidationErrors	= appendReminderIntervalsJSON();
		var reminderIntervalsInput		= $('[input[name="reminderIntervals"]');
		
		if (!stageValidationErrors && !reminderValidationErrors)
		{
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
					url:  "/pgadmissions/configuration/", 
					data:  stagesInput.serialize() + "&" + reminderIntervalsInput.serialize(),
					success: function(data)
					{
						$('#configsection').html(data);
						addToolTips();
					},
					complete: function()
					{
						$('#ajaxloader').fadeOut('fast');
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

function appendReminderIntervalsJSON()
{
	var validationErrors = false;
	$('#availableReminderIntervals option').each(function() {	
		var isDurationError = false;
		var reminderType = $(this).val();
		var duration = $('#reminderIntervalDuration_' + reminderType).val();
		var intervalUnit = $('#reminderIntervalUnit_'+ reminderType).val();
		if (isNaN(duration) || duration == "" || duration <= 0) {
			$("#invalidDurationInterval_" + reminderType + " span").html('You must enter a whole number greater that 0.');
			$("#invalidDurationInterval_" + reminderType).show();
			isDurationError = true;
			validationErrors = true;
		} else {
			$("#invalidDurationInterval_" + reminderType + " span").html('');
			$("#invalidDurationInterval_" + reminderType).hide();
		}
		
		if (intervalUnit == "" &&  !isDurationError) {
			$("#invalidUnitInterval_" + reminderType + " span").html('You must make a selection.');
			$("#invalidUnitInterval_" + reminderType).show();
			validationErrors = true;
		} else {
			$("#invalidUnitInterval_" + reminderType + " span").html('');
			$("#invalidUnitInterval_" + reminderType).hide();
		}
		
		$("#reminderIntervals").append('<input type="hidden" name="reminderIntervals" id= "reminderIntervals"  value=' +"'" + '{"reminderType":"' +  reminderType+ '","duration":"' + duration + '","unit":"' + intervalUnit + '"} ' + "'" + "/>");
	});
	return validationErrors;
}
