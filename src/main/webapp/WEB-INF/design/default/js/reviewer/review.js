$(document).ready(function()
{
	getReviewersSection();

		
	$('#createReviewer').click(function()
	{
		$('#applicationReviewers option').each(function()
		{
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
	


	$('#moveToReviewBtn').click(function() {
		
		$('#reviewsection').append('<div class="ajax" />');
		

		$('#applicationReviewers option').each(function(){
			 	var ids = $(this).val();
			 	var user = ids.substring(ids.indexOf("|") + 1);
				$('#postReviewData').append("<input name='pendingReviewer' type='hidden' value='" + user + "'/>");	
				$('#postReviewData').append("<input name='reviewers' type='hidden' value='" +  $(this).val() + "'/>");
		});
		
		var postData = {
				applicationId : $('#applicationId').val()
		}
		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
			url:"/pgadmissions/review/move",
			data:	$.param(postData) + "&" + $('input[name="pendingReviewer"]').serialize()+ "&" + $('input[name="reviewers"]').serialize(),
			success: function(data)
			{	
				if(data == "OK"){
					window.location.href = '/pgadmissions/applications?messageCode=move.review&application=' + $('#applicationId').val();
				}else{
					$('#assignReviewersToAppSection').html(data);
					$('#postReviewData').html('');
					$('#reviewsection div.ajax').remove();
				}
			}
		});
	});
});


function getReviewersSection(){
	$('#reviewsection').append('<div class="ajax" />');
	
	$.ajax({
		type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  }
		  },
		url:"/pgadmissions/review/reviewersSection?applicationId=" + $('#applicationId').val(), 
		success: function(data)
		{
			$('#reviewsection div.ajax').remove();
			$('#assignReviewersToAppSection').html(data);
		}
	});
}