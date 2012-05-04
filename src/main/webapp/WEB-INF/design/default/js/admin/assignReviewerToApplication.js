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
		var postData ={ 
			applicationId : $('#applicationId').val(),
			firstName : "",
			lastName : "world",
			email : "hello@world.com"
		};
		
		$.post("/pgadmissions/assignReviewers/createReviewer", 
			$.param(postData),
			function(data) {
				alert("data: " + data);
			},
			function(data) {
				alert("error: " + data);
			}
		);
	});
	
	$('#moveToReviewBtn').click(function() {
		alert("doesn't work yet...\n(reviewerids need to be assigned to post request");
		var selectedReviewers = $('#assignedReviewers').val();
		alert("items: " + selectedReviewer.length);
		$.post("/pgadmissions/assignReviewers/moveApplicationToReview", {
			applicationId : $('#applicationId').val(),
			reviewerIds : selectedReviewers
		}, function(data) {
			alert("data: " + data);
		});
	});
});