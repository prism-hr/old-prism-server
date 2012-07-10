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
				 $('#createreviewersection div.ajax').remove();
		      }
		});
		
	});
	


	$('#moveToReviewBtn').click(function() {
		
		$('#reviewsection').append('<div class="ajax" />');
		var url = null;
		if($('#assign').val() == 'true'){
			url = "/pgadmissions/review/assign";
		}else{
			url = "/pgadmissions/review/move";
		}
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
			url: url,
			data:	$.param(postData) + "&" + $('input[name="pendingReviewer"]').serialize()+ "&" + $('input[name="reviewers"]').serialize(),
			success: function(data)
			{	
				if(data == "OK"){
					var url = null;
					if($('#assign').val() == 'true'){
						window.location.href = '/pgadmissions/applications?messageCode=assign.review&application=' + $('#applicationId').val();
					}else{
						window.location.href = '/pgadmissions/applications?messageCode=move.review&application=' + $('#applicationId').val();
					}
				
				}else{
					$('#assignReviewersToAppSection').html(data);
					$('#postReviewData').html('');
				}
				addToolTips();
			},
		      complete: function()
		      {
						$('#reviewsection div.ajax').remove();
		      }
		});
	});
});


function getReviewersSection(){
	$('#reviewsection').append('<div class="ajax" />');
	
	var url = null;
	if($('#assign').val() == 'true'){
		url = "/pgadmissions/review/assignReviewersSection";
	}else{
		url = "/pgadmissions/review/reviewersSection";
	}
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
		url: url +"?applicationId=" + $('#applicationId').val(), 
		success: function(data)
		{
			$('#assignReviewersToAppSection').html(data);
			addToolTips();
		},
		complete: function()
    	{
	      $('#reviewsection div.ajax').remove();
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
			$('#createreviewersection div.ajax').remove();
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