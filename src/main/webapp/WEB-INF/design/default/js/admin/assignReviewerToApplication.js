$(document).ready(function() {

	$('#addReviewerBtn').click(function() {
		var selectedReviewers = $('#reviewers').val();
		selectedReviewers.forEach(function(id) {
			var selText = $("#reviewers option[value='" + id + "']").text();
			$("#reviewers option[value='" + id + "']").remove();
			$("#assignedReviewers").append('<option value="'+ id +'">'+ selText +'</option>');
		});
	});

	$('#createReviewer').click(function() {
		var idString = getAssignedReviewerIdString(); 
		var postData ={ 
			applicationId : $('#applicationId').val(),
			firstName : $('#newReviewerFirstName').val(),
			lastName : $('#newReviewerLastName').val(),
			email : $('#newReviewerEmail').val(),
			unsavedReviewersRaw : idString
		};
		
		$.post("/pgadmissions/assignReviewers/createReviewer", 
			$.param(postData),
			function(data) {
				$('#assignReviewersToAppSection').html(data);
			}
		);
	});
	
	$('#moveToReviewBtn').click(function() {
		var idString = getAssignedReviewerIdString(); 
		var postData = {
				applicationId : $('#applicationId').val(),
				unsavedReviewersRaw : idString
			};
			
		$.post( "/pgadmissions/assignReviewers/moveApplicationToReview" , $.param(postData),
			function(data) {
				$('#assignReviewersToAppSection').html(data);
			}
		);
	});
});

function getAssignedReviewerIdString() {
	var assignedReviewers = document.getElementById("assignedReviewers").options;
	var revIds = "";
	for(i = 0; i < assignedReviewers.length; i = i + 1) {
		if( i != 0) {
			revIds += "|";
		}
		revIds += assignedReviewers.item(i).value;
	}
	return revIds;
}