$(document).ready(function() {

	$('input:checkbox[name=rejectReasons]').click(function() {
		var reasonIds = getReasonIds();
		
		if( reasonIds.length == 0) {
			return false;
		}	
		var postData ={ 
				applicationId : $('#applicationId').val(),
				rejectReasonIds : reasonIds
		};
		$.post("/pgadmissions/rejectApplication/rejectionText", 
				$.param(postData),
				function(data) {
					$('#emailText').html(data);
				}
		);
	});
	
	
	$('#rejectButton').click(function() {
		var reasonIds = getReasonIds();
		
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

function getReasonIds() {
	var reasonIds = [];
	$("input:checkbox[name=rejectReasons]:checked").each(function() {
	   reasonIds.push($(this).val());
	});
	return reasonIds;
}