$(document).ready(function()
{
	getReviewersSection();
	getCreateReviewersSection();
	
	$('#createreviewersection').on('click','#createReviewer', function()
	{
		$('#ajaxloader').show();
		var postData = {
			applicationId : $('#applicationId').val(),
			firstName: $('#newReviewerFirstName').val(),
			lastName: $('#newReviewerLastName').val(),
			email: $('#newReviewerEmail').val()				
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
			data:	$.param(postData),
			success: function(data)
			{	
				var newReviewer;
				try{
					newReviewer = jQuery.parseJSON(data);	
				}catch(err){
					
					$('#createreviewersection').html(data);
					addToolTips();
					return;
				}
				if(newReviewer.isNew){
					$('#previous').append('<option value="' + $('#applicationId').val() + '|' + newReviewer.id + '" category="previous" disabled="disabled">' +
							newReviewer.firstname + ' ' + newReviewer.lastname+ '</option>');
					$('#applicationReviewers').append('<option value="' + $('#applicationId').val() + '|' + newReviewer.id + '">' +
							newReviewer.firstname + ' ' + newReviewer.lastname+ '</option>');
					$('#applicationReviewers').attr("size", $('#applicationReviewers option').size() + 1);					
					
				}else{
					addExistingUserToReviewersLists(newReviewer);
				}
				resetReviewersErrors();
				getCreateReviewersSection();		
				
			},
		      complete: function()
		      {
				$('#ajaxloader').fadeOut('fast');
		      }
		});
		
	});
	


	$('#moveToReviewBtn').click(function() {
		
		$('#ajaxloader').show();
		$('#applicationReviewers option').each(function(){	
			$('#postReviewData').append("<input name='reviewers' type='hidden' value='" +  $(this).val() + "'/>");
		});
		
		var postData = {
				applicationId : $('#applicationId').val(),
				reviewers: ''
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
			url: "/pgadmissions/review/move",
			data:	$.param(postData) + "&" + $('input[name="pendingReviewer"]').serialize()+ "&" + $('input[name="reviewers"]').serialize(),
			success: function(data)
			{	
				if(data == "OK") {
					window.location.href = '/pgadmissions/applications?messageCode=move.review&application=' + $('#applicationId').val();
				} else {
					$('#assignReviewersToAppSection').html(data);
					$('#postReviewData').html('');
				}
				addToolTips();
			},
		      complete: function()
		      {
				
				$('#ajaxloader').fadeOut('fast');
				checkIfErrors();
		      }
		});
	});
});


function getReviewersSection(){
	$('#ajaxloader').show();
	
	$.ajax({
		type: 'GET',
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
		url: "/pgadmissions/review/reviewersSection?applicationId=" + $('#applicationId').val(), 
		success: function(data)
		{
			$('#assignReviewersToAppSection').html(data);
			addToolTips();
		},
		complete: function()
    	{
	      $('#ajaxloader').fadeOut('fast');
	    }
	});
}

function getCreateReviewersSection(){
	$('#ajaxloader').show();
	
	$.ajax({
		type: 'GET',
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
		url:"/pgadmissions/review/create_reviewer_section?applicationId=" + $('#applicationId').val(), 
		success: function(data)
		{
			$('#ajaxloader').fadeOut('fast');
			$('#createreviewersection').html(data);
		}
	});
}

function addExistingUserToReviewersLists(newReviewer){
	
	if($('#applicationReviewers option[value="' + $('#applicationId').val() + '|' + newReviewer.id + '"]').length > 0){
		
		
		return;
	}
	

	if($('#default option[value="' + $('#applicationId').val() + '|' + newReviewer.id + '"]').length > 0){		
		$('#default option[value="' + $('#applicationId').val() + '|' + newReviewer.id + '"]').attr("selected", 'selected');		
		$('#addReviewerBtn').trigger('click');
	
		return;
	}	

	if($('#previous option[value="' + $('#applicationId').val() + '|' + newReviewer.id + '"]').length > 0){
		$('#previous option[value="' + $('#applicationId').val() + '|' + newReviewer.id + '"]').attr("selected", 'selected');		
		$('#addReviewerBtn').trigger('click');
	
		return;
	}
	
	$('#previous').append('<option value="' + $('#applicationId').val() + '|' + newReviewer.id + '" category="previous" disabled="disabled">' +
			newReviewer.firstname + ' ' + newReviewer.lastname+ '</option>');
	$('#applicationReviewers').append('<option value="' + $('#applicationId').val() + '|' + newReviewer.id + '">' +
			newReviewer.firstname + ' ' + newReviewer.lastname+ '</option>');
	$('#applicationReviewers').attr("size", $('#applicationReviewers option').size() + 1);

}

function resetReviewersErrors(){
	if( $('#applicationReviewers option').size() > 0){
		$('#reviwersErrorSpan').remove();
	}
}