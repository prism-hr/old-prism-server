$(document).ready(function(){
	
	$('#programmeSaveButton').on("click",function(){
		var postData = {
			programmeName: $("#programmeName").val(),
			projectName: $("#projectName").val(), 
			studyOption: $("#studyOption").val(), 
			startDate: $("#startDate").val(),
			referrer: $("#referrer").val(),
			application: $("#appId1").val(),
			programmeDetailsId: $("#programmeDetailsId").val()
		}
		
		$.post( "/pgadmissions/programme" ,$.param(postData),
		function(data) {
			$('#programmeDetailsSection').html(data);
		});
	});
	
});