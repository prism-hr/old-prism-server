$(document).ready(function() {
	
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
});

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
