$(document).ready(function() {

	$('#rejectButton').click(function() {
		var reasonIds = [];
		$("input:checkbox[name=rejectReasons]:checked").each(function() {
		   reasonIds.push($(this).val());
		});
		
		if( reasonIds.length == 0) {
			alert("Please select at least one reason.");
			return false;
		}		
		var postData ={ 
			applicationId : $('#applicationId').val(),
			rejectReasonIds : reasonIds
		};
			
		$.post("/pgadmissions/rejectApplication/moveApplicationToReject", 
			$.param(postData),
			function(data) {
				window.location.href = "/pgadmissions/applications";
			}
		);
	});

});