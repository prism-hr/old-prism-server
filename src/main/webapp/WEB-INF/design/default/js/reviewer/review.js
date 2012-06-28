$(document).ready(function()
{
	getReviewersSection();
	getCreateReviewersSection();
	
	$('#createreviewersection').on('click','#createReviewer', function()
	{
		$('#createreviewersection').append('<div class="ajax" />');
		var postData = {
			applicationId : $('#applicationId').val(),
			firstName: $('#newReviewerFirstName').val(),
			lastName: $('#newReviewerLastName').val(),
			email: $('#newReviewerEmail').val()				
		}
		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
			url:"/pgadmissions/review/createReviewer",
			data:	$.param(postData),
			success: function(data)
			{	
				var newReviewer = jQuery.parseJSON(data);	
				if(newReviewer.isNew){
					$('#previous').append('<option value="' + $('#applicationId').val() + '|' + newReviewer.id + '" category="previous" disabled="disabled">' +
							newReviewer.firstname + ' ' + newReviewer.lastname+ '</option>');
					$('#applicationReviewers').append('<option value="' + $('#applicationId').val() + '|' + newReviewer.id + '">' +
							newReviewer.firstname + ' ' + newReviewer.lastname+ '</option>');
					$('#add-info-bar-div').html('New user ' + newReviewer.toString + ' added as reviewer');
				}else{
					addExistingUserToReviewersLists(newReviewer);
				}
				
				$('#newReviewerFirstName').val('');
				$('#newReviewerLastName').val('');
				$('#newReviewerEmail').val('');				
				
				$('#reviewsection div.ajax').remove();
			}
		});
		
	});
	


	$('#moveToReviewBtn').click(function() {
		
		$('#reviewsection').append('<div class="ajax" />');
		

		$('#applicationReviewers option').each(function(){	
			$('#postReviewData').append("<input name='reviewers' type='hidden' value='" +  $(this).val() + "'/>");
		});
		
		var postData = {
				applicationId : $('#applicationId').val(),
				reviewers: ''
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
				addToolTips();
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
			addToolTips();
		}
	});
}

function getCreateReviewersSection(){
	$('#createreviewersection').append('<div class="ajax" />');
	
	$.ajax({
		type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  }
		  },
		url:"/pgadmissions/review/create_reviewer_section?applicationId=" + $('#applicationId').val(), 
		success: function(data)
		{
			$('#createreviewersection div.ajax').remove();
			$('#createreviewersection').html(data);
		}
	});
}

function addExistingUserToReviewersLists(newReviewer){
	
	if($('#applicationReviewers option[value="' + $('#applicationId').val() + '|' + newReviewer.id + '"]').length > 0){
		$('#add-info-bar-div').html(newReviewer.toString + ' is already selected as reviewer');
		return;
	}
	

	if($('#default option[value="' + $('#applicationId').val() + '|' + newReviewer.id + '"]').length > 0){		
		$('#default option[value="' + $('#applicationId').val() + '|' + newReviewer.id + '"]').attr("selected", 'selected');		
		$('#addReviewerBtn').trigger('click');
		$('#add-info-bar-div').html('Default reviewer ' + newReviewer.toString + ' selected as reviewer');
		return;
	}	

	if($('#previous option[value="' + $('#applicationId').val() + '|' + newReviewer.id + '"]').length > 0){
		$('#previous option[value="' + $('#applicationId').val() + '|' + newReviewer.id + '"]').attr("selected", 'selected');		
		$('#addReviewerBtn').trigger('click');
		$('#add-info-bar-div').html('Previous reviewer ' + newReviewer.toString + ' selected as reviewer');
		return;
	}
	
	$('#previous').append('<option value="' + $('#applicationId').val() + '|' + newReviewer.id + '" category="previous" disabled="disabled">' +
			newReviewer.firstname + ' ' + newReviewer.lastname+ '</option>');
	$('#applicationReviewers').append('<option value="' + $('#applicationId').val() + '|' + newReviewer.id + '">' +
			newReviewer.firstname + ' ' + newReviewer.lastname+ '</option>');
	$('#add-info-bar-div').html('Existing user ' + newReviewer.toString + ' added as reviewer');
}