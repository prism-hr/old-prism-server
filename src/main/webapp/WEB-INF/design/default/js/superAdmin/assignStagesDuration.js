$(document).ready(function() {
	
	$('#submitDurationStages').click(function() {
		appendStagesJSON();
		var stages = $('[input[name="stagesDuration"]');
		$.post("/pgadmissions/assignStagesDuration/submit", 
			stages.serialize(),
			function(data) {
				$('#assignReviewersToAppSection').html(data);
			}
		);
	});
});

function appendStagesJSON() {
	var stages = document.getElementById("stages");
	for (var i=0; i<stages.length; i++){
		var stageName = stages.options[i].value;
	    var stageDuration = $('#' + stageName + '_duration').val();
//	    var stageUnit = $('#'+ stageName + '_unit' +'option:selected').val();
	    var stageUnit = $('#'+ stageName + '_unit').val();
	    $("#stagesDuration").append('<input type="hidden" name="stagesDuration" id= "stagesDuration"  value=' +"'" + '{"stage":"' +  stageName+ '","duration":"' + stageDuration + '","unit":"' + stageUnit + '"} ' + "'" + "/>");
	}
}
