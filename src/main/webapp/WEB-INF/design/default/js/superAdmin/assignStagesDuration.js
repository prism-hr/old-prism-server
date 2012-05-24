$(document).ready(function() {
	if($('#2_regUserId').val() == "") {
		$("span[id='secondRegistryUser']").hide();
	}
	if($('#3_regUserId').val() == "") {
		$("span[id='thirdRegistryUser']").hide();
	}
	
	$('#cancelDurationBtn').click(function() {
		  window.location.href = "/pgadmissions/applications";
	});
	
	$('#cancelReminderBtn').click(function() {
		window.location.href = "/pgadmissions/applications";
	});
	
	$('#cancelRegistryBtn').click(function() {
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
	
	$('#submitRUBtn').click(function() {
		$("#registryUsers").html('');
		var validationErrors = appendRegistryUsersJSON();
		var registryUsers = $('[input[name="registryUsers"]');
		if(!validationErrors){
			$.post("/pgadmissions/assignStagesDuration/submitRegistryUsers", 
				registryUsers.serialize(),
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

function appendRegistryUsersJSON() {
	var validationErrors = false;
	//todo: change check for == "" with isBlank equivalent
	//if first user is filled
	if($('#1_regUserfirstname').val() != "" && $('#1_regUserLastname').val() != "" && validateEmail($('#1_regUserEmail').val())){
		$("span[name='firstuserInvalid']").html('');
		$("span[name='firstuserInvalid']").hide();
		$("#registryUsers").append('<input type="hidden" name="registryUsers" id= "registryUsers"  value=' +"'" + '{"id":"' +  $('#1_regUserId').val() + '","firstname":"' + $('#1_regUserfirstname').val() + '","lastname":"' +  $('#1_regUserLastname').val() + '","email":"' + $('#1_regUserEmail').val()  + '"} ' + "'" + "/>");
		
	}
	else{
		if(!validateEmail($('#1_regUserEmail').val())){
			$("span[name='firstuserInvalid']").html("Email is not valid.");
		}
		if($('#1_regUserfirstname').val() == "" || $('#1_regUserLastname').val() ==""){
			$("span[name='firstuserInvalid']").html('Please specify firstname lastname and email.');
		}
		$("span[name='firstuserInvalid']").show();
		validationErrors = true;
	}
	
	if($("span[id='secondRegistryUser']").is(':visible')) {
		if($('#2_regUserfirstname').val() != "" && $('#2_regUserLastname').val() != "" && validateEmail($('#2_regUserEmail').val())){
			$("span[name='seconduserInvalid']").html('');
			$("span[name='seconduserInvalid']").hide();
			$("#registryUsers").append('<input type="hidden" name="registryUsers" id= "registryUsers"  value=' +"'" + '{"id":"' +  $('#2_regUserId').val() + '","firstname":"' + $('#2_regUserfirstname').val() + '","lastname":"' +  $('#2_regUserLastname').val() + '","email":"' + $('#2_regUserEmail').val()  + '"} ' + "'" + "/>");
		}
		else{
			if(!validateEmail($('#2_regUserEmail').val())){
				$("span[name='seconduserInvalid']").html("Email is not valid.");
			}
			if($('#2_regUserfirstname').val() == "" || $('#2_regUserLastname').val() ==""){
				$("span[name='seconduserInvalid']").html('Please specify firstname lastname and email.');
			}
			$("span[name='seconduserInvalid']").show();
			validationErrors = true;
		}
	}
	//if third user is visible and filled
	if($("span[id='thirdRegistryUser']").is(':visible')) {
		if($('#3_regUserfirstname').val() != "" && $('#3_regUserLastname').val() != "" && validateEmail($('#3_regUserEmail').val())){
			$("span[name='thirduserInvalid']").html('');
			$("span[name='thirduserInvalid']").hide();
			$("#registryUsers").append('<input type="hidden" name="registryUsers" id= "registryUsers"  value=' +"'" + '{"id":"' +  $('#3_regUserId').val() + '","firstname":"' + $('#3_regUserfirstname').val() + '","lastname":"' +  $('#3_regUserLastname').val() + '","email":"' + $('#3_regUserEmail').val()  + '"} ' + "'" + "/>");
		}
		else{
			if(!validateEmail($('#3_regUserEmail').val())){
				$("span[name='thirduserInvalid']").html("Email is not valid.");
			}
			if($('#3_regUserfirstname').val() == "" || $('#3_regUserLastname').val() ==""){
				$("span[name='thirduserInvalid']").html('Please specify firstname lastname and email.');
			}
			$("span[name='thirduserInvalid']").show();
			validationErrors = true;
		}
	}
	return validationErrors;
	
	function validateEmail(email) { 
		var pattern = new RegExp(/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i);
	    var result = pattern.test(email);
		return result;
	} 
	
	
	
	
}
