$(document).ready(function() {

	$('#addReviewerBtn').click(function() {
		var selectedReviewers = $('#reviewers').val();
		selectedReviewers.forEach(function(id) {
			var selText = $("#reviewers option[value='" + id + "']").text();
			$("#reviewers option[value='" + id + "']").remove();
			$("#assignedReviewers").append('<option value="'+ id +'">'+ selText +'</option>');
			$("#appReviewers").append('<input type="hidden" name="reviewers" value=' +"'" + '{"id":"' +  id + '"} ' + "'" + "/>");
			$("#assRev").append('<input type="hidden" name="assignedReviewers" value=' +"'" + '{"id":"' +  id + '"} ' + "'" + "/>");
		});
	});

	$('#createReviewer').click(function() {
		var assignedReviewerOptions = document.getElementById('assignedReviewers').options;
		var assignedReviewersr = new Array(assignedReviewerOptions.length);
		for(i = 0; i < assignedReviewerOptions.length; i++) {
			assignedReviewersr[i] = assignedReviewerOptions[i].value;
		}
		var assignedReviewers = $('[input[name="assignedReviewers"]');
		var postData ={ 
			applicationId : $('#applicationId').val(),
			firstName : $('#newReviewerFirstName').val(),
			lastName : $('#newReviewerLastName').val(),
			email : $('#newReviewerEmail').val(),
		};
		
		$.post("/pgadmissions/assignReviewers/createReviewer", 
			$.param(postData)+"&" + assignedReviewers.serialize(),
			function(data) {
				$('#assignReviewersToAppSection').html(data);
			}
		);
	});
	
	$('#moveToReviewBtn').click(function() {
		var postData = {
				applicationId : $('#applicationId').val()
			};
			
			$.post( "/pgadmissions/assignReviewers/moveApplicationToReview" ,$.param(postData) +"&" + $('[input[name="reviewers"]').serialize(),
			function(data) {
				$('#assignReviewersToAppSection').html(data);
			});
		
		
	});
});