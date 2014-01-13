$(document).ready(function() {
	$('#addReviewerBtn').click(function() {
		$('#reviewers').each(function(index) {
			var id = $(this).attr("value");
			var selText = $("#reviewers option[value='" + id + "']").text();
			$("#reviewers option[value='" + id + "']").remove();
			$("#assignedReviewers").append('<option value="'+ id +'">'+ selText +'</option>');
		});
		return false;
	});
	
	$('#removeReviewerBtn').click(function() {
		$('#assignedReviewers').each(function(index) {
			var id = $(this).attr("value");
			var selText = $("#assignedReviewers option[value='" + id + "']").text();
			$("#assignedReviewers option[value='" + id + "']").remove();
			$("#reviewers").append('<option value="'+ id +'">'+ selText +'</option>');
		});
		$('#programInterviewers').attr("size", $('#programInterviewers option').length + 1);
		$('#applicationInterviewers').attr("size", $('#applicationInterviewers option').length + 1);
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
				  },
				  500: function() {
					  window.location.href = "/pgadmissions/error";
				  },
				  404: function() {
					  window.location.href = "/pgadmissions/404";
				  },
				  400: function() {
					  window.location.href = "/pgadmissions/400";
				  },				  
				  403: function() {
					  window.location.href = "/pgadmissions/404";
				  }
			  },
			url:"/pgadmissions/review/createReviewer", 
			data:$.param(postData),
			success:function(data) {
				$('#assignReviewersToAppSection').html(data);
				addToolTips();
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
				  },
				  500: function() {
					  window.location.href = "/pgadmissions/error";
				  },
				  404: function() {
					  window.location.href = "/pgadmissions/404";
				  },
				  400: function() {
					  window.location.href = "/pgadmissions/400";
				  },				  
				  403: function() {
					  window.location.href = "/pgadmissions/404";
				  }
			  },
			  url:"/pgadmissions/review/moveApplicationToReview" , 
			  data:$.param(postData),
			  success: function(data) {
				   window.location.href = "/pgadmissions/applications";
				   addToolTips();
				}
		});
	});
});

function getAssignedReviewerIdString() {
	var assignedReviewers = document.getElementById("assignedReviewers").options;
	var revIds = "";
	for(var i = 0; i < assignedReviewers.length; i = i + 1) {
		if( i != 0) {
			revIds += "|";
		}
		revIds += assignedReviewers.item(i).value;
	}
	return revIds;
}