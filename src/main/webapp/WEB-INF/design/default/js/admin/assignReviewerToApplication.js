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
		$.post("/pgadmissions/assignReviewers/createReviewer", {
			applicationId : $('#applicationId').val(),
			firstName : "hello",
			lastName : "world",
			email : "hello@world.com"
		}, function(data) {
			alert("data: " + data);
		});
	});
	
	$('#moveToReviewBtn').click(function() {
		alert("doesn't work yet...");
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