$(document).ready(function() {
	
	$("span[id='secondRegistryUser']").hide();
	$("span[id='thirdRegistryUser']").hide();
	
	$('#cancelDurationBtn').click(function() {
		  window.location.href = "/pgadmissions/applications";
	});
	
	$('#cancelReminderBtn').click(function() {
		window.location.href = "/pgadmissions/applications";
	});
	
	$('#submitDurationStages').click(function() {
		$("#stagesDuration").html('');
		var validationErrors = appendStagesJSON();
		var stages = $('[input[name="stagesDuration"]');
		if(!validationErrors){
			$.post("/pgadmissions/assignStagesDuration/submit", 
				stages.serialize(),
				function(data) {
					window.location.href = "/pgadmissions/applications";
				}
			);
		}
	});
	
	$('#addAnother').click(function() {
		if(!$("span[id='secondRegistryUser']").is(':visible')) {
			$("span[id='secondRegistryUser']").show();
		}
		else if(!$("span[id='thirdRegistryUser']").is(':visible')) {
			$("span[id='thirdRegistryUser']").show();
		}
		else{
			$("span[id='secondRegistryUser']").show();
			$("span[id='thirdRegistryUser']").show();
			$("span[name='threeMaxMessage']").html("You cannot specify more than three registry users.");
		}
	});
	
	$('#submitRIBtn').click(function() {
		var validationErrors = validateReminderInterval();
		var postData ={ 
				id : $('#reminderIntervalId').val(),
				duration : $('#reminderIntervalDuration').val(),
				unit : $('#reminderUnit').val()
			};
		if(!validationErrors){
			$.post("/pgadmissions/assignStagesDuration/submitReminderInterval", 
					$.param(postData),
					function(data) {
				window.location.href = "/pgadmissions/applications";
			}
			);
		}
	});
});


function validateReminderInterval() {
	var validationErrors = false;
		var reminderIntervalDuration = $('#reminderIntervalDuration').val();
		var reminderIntervalUnit = $('#reminderUnit').val();
		if(isNaN(reminderIntervalDuration) || reminderIntervalDuration == ""){
			$("span[name='invalidDurationInterval']").html('Please specify the duration amount as a number.');
			$("span[name='invalidDurationInterval']").show();
			validationErrors = true;
		}
		else{
			$("span[name='invalidDurationInterval']").html('');
			$("span[name='invalidDurationInterval']").hide();
		}
		if(reminderIntervalUnit == ""){
			$("span[name='invalidUnitInterval']").html('Please select a duration unit from the dropdown.');
			$("span[name='invalidUnitInterval']").show();
			
			validationErrors = true;
		}
		else{
			$("span[name='invalidUnitInterval']").html('');
			$("span[name='invalidUnitInterval']").hide();
		}
	return validationErrors;
}
function appendStagesJSON() {
	var validationErrors = false;
	var stages = document.getElementById("stages");
	for (var i=0; i<stages.length; i++){
		var stageName = stages.options[i].value;
	    var stageDuration = $('#' + stageName + '_duration').val();
	    var stageUnit = $('#'+ stageName + '_unit').val();
	    if(isNaN(stageDuration) || stageDuration == ""){
	    	$("span[name='"+stageName+"_invalidDuration']").html('Please specify the duration amount as a number.');
			$("span[name='"+stageName+"_invalidDuration']").show();
	    	validationErrors = true;
	    }
	    else{
	    	$("span[name='"+stageName+"_invalidDuration']").html('');
	    	$("span[name='"+stageName+"_invalidDuration']").hide();
	    }
	    if(stageUnit == ""){
	    	$("span[name='"+stageName+"_invalidUnit']").html('Please select a duration unit from the dropdown.');
			$("span[name='"+stageName+"_invalidUnit']").show();

	    	validationErrors = true;
	    }
	    else{
	    	$("span[name='"+stageName+"_invalidUnit']").html('');
	    	$("span[name='"+stageName+"_invalidUnit']").hide();
	    }
	    $("#stagesDuration").append('<input type="hidden" name="stagesDuration" id= "stagesDuration"  value=' +"'" + '{"stage":"' +  stageName+ '","duration":"' + stageDuration + '","unit":"' + stageUnit + '"} ' + "'" + "/>");
	}
	return validationErrors;
	
}
