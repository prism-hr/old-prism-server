$(document).ready(function(){
	
	$('#addReviewerBtn').click(function() {
		var selectedReviewers = $('#programReviewers').val();
		selectedReviewers.forEach(function(id) {			
			var selText = $("#programReviewers option[value='" + id + "']").text();
			var category = $("#programReviewers option[value='" + id + "']").attr("category");
			$("#programReviewers option[value='" + id + "']").remove();
			$("#applicationReviewers").append('<option value="'+ id +'" category="' + category + '">'+ selText + ' (*)</option>');
		});
		$('#programReviewers').attr("size", $('#programReviewers option, #programReviewers optgroup').size() + 1);
		$('#applicationReviewers').attr("size", $('#applicationReviewers option, #programReviewers optgroup').size() + 1);
		
		
	});
	
	
	$('#createReviewer').click(function() {

		$('#applicationReviewers option').each(function(){
			var ids = $(this).val();
		 	var user = ids.substring(ids.indexOf("|") + 1);
			$('#postReviewerForm').append("<input name='pendingReviewer' type='hidden' value='" + user + "'/>");	
		});
		$('#postReviewerForm').append("<input name='applicationId' type='hidden' value='" +  $('#applicationId').val() + "'/>");		
		$('#postReviewerForm').append("<input name='firstName' type='hidden' value='" +  $('#newReviewerFirstName').val() + "'/>");
		$('#postReviewerForm').append("<input name='lastName' type='hidden' value='" +  $('#newReviewerLastName').val() + "'/>");
		$('#postReviewerForm').append("<input name='email' type='hidden' value='" +  $('#newReviewerEmail').val() + "'/>");		
		
		$('#postReviewerForm').submit();
		
	});
	
	$('#removeReviewerBtn').click(function() {
		var selectedReviewers = $('#applicationReviewers').val();
		selectedReviewers.forEach(function(id) {
			var selText = $("#applicationReviewers option[value='" + id + "']").text().replace(' (*)', '');
			$("#applicationReviewers option[value='" + id + "']").remove();
			$("#programReviewers").append('<option value="'+ id +'">'+ selText +'</option>');
		});
		$('#programReviewers').attr("size", $('#programReviewers option, #programReviewers optgroup').size() + 1);
		$('#applicationReviewers').attr("size", $('#applicationReviewers option, #programReviewers optgroup').size() + 1);
	});

	
	
	$('#moveToReviewBtn').click(function() {
		
		$('#applicationReviewers option').each(function(){
			 	var ids = $(this).val();
			 	var user = ids.substring(ids.indexOf("|") + 1);
				$('#postReviewForm').append("<input name='pendingReviewer' type='hidden' value='" + user + "'/>");	
				$('#postReviewForm').append("<input name='reviewers' type='hidden' value='" +  $(this).val() + "'/>");
		});
		$('#postReviewForm').append("<input name='applicationId' type='hidden' value='" +  $('#applicationId').val() + "'/>");				
		$('#postReviewForm').submit();
	});
		
		
	
});


