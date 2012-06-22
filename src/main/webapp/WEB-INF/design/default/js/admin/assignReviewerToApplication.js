$(document).ready(function() {
	$('#addReviewerBtn').click(function() {
		var selectedReviewers = $('#reviewers').val();
		selectedReviewers.forEach(function(id) {
			var selText = $("#reviewers option[value='" + id + "']").text();
			$("#reviewers option[value='" + id + "']").remove();
			$("#assignedReviewers").append('<option value="'+ id +'">'+ selText +'</option>');
		});
		return false;
	});
	
	$('#removeReviewerBtn').click(function() {
		var selectedReviewers = $('#assignedReviewers').val();
		selectedReviewers.forEach(function(id) {
			var selText = $("#assignedReviewers option[value='" + id + "']").text();
			$("#assignedReviewers option[value='" + id + "']").remove();
			$("#reviewers").append('<option value="'+ id +'">'+ selText +'</option>');
		});
		$('#programInterviewers').attr("size", $('#programInterviewers option').size() + 1);
		$('#applicationInterviewers').attr("size", $('#applicationInterviewers option').size() + 1);
		return false;
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
		
		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
			url:"/pgadmissions/review/createReviewer", 
			data:$.param(postData),
			success:function(data) {
				$('#assignReviewersToAppSection').html(data);
			}
		});
		return false;
	});
	
	$('#moveToReviewBtn').click(function() {
		var idString = getAssignedReviewerIdString();
		
		if(!idString) {
			alert("Please select a reviewer to assign.");
			return false;
		}
		var postData = {
				applicationId : $('#applicationId').val(),
				unsavedReviewersRaw : idString
			};
			
		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
			  url:"/pgadmissions/review/moveApplicationToReview" , 
			  data:$.param(postData),
			  success: function(data) {
				   window.location.href = "/pgadmissions/applications";
				}
		});
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