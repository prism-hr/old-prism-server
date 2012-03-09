$(document).ready(function(){
	$('#programmeSaveButton').click(function(){
		$.post("/pgadmissions/update/editProgramme", { 
			programmeDetailsProgrammeName: $("#programmeDetailsProgrammeName").val(),
			programmeDetailsProjectName: $("#programmeDetailsProjectName").val(), 
			programmeDetailsStudyOption: $("#programmeDetailsStudyOption").val(), 
			programmeDetailsStartDate: $("#programmeDetailsStartDate").val(),
			programmeDetailsReferrer: $("#programmeDetailsReferrer").val(), 
			id1: $("#id1").val(), 
			appId1: $("#appId1").val()
		},
		function(data) {
			$('#programmeDetailsSection').html(data);
		});
	});
});