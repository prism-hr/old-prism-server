$(document).ready(function() {

	$('#cancelApproved').click(function() {
		window.location.href = "/pgadmissions/applications";
	});	
	
	$('#approveButton').click(function() {
		var application = $('#applicationId').val();	
		var commentType = $('#commentType').val();
		var postData = {
				application: application,
				type: commentType,
				comment: $('#comment').val()
			};
		$.post( 
				"/pgadmissions/progress",
				postData,
				moveToApproved()
		);
	});
});

function moveToApproved(data){
	var application = $('#applicationId').val();	
	$.post("/pgadmissions/approved/move", {
		applicationId: application,
	},
	function(data) {
		window.location.href = "/pgadmissions/applications";
	});
}

