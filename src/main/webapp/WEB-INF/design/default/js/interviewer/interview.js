$(document).ready(function()
{
	getInterviewersAndDetailsSections();
	getCreateInterviewersSection();



	// -----------------------------------------------------------------------------------------
	// Add interviewer
	// -----------------------------------------------------------------------------------------
	$('#assignInterviewersToAppSection').on('click', '#addInterviewerBtn', function(){
		var selectedReviewers = $('#programInterviewers').val();
		if (selectedReviewers)
		{
			selectedReviewers.forEach(function(id)
			{
				var $option = $("#programInterviewers option[value='" + id + "']");
	
				if (!$option.hasClass('selected'))
				{
					var selText = $option.text();
					var category = $option.attr("category");
					$("#programInterviewers option[value='" + id + "']").addClass('selected')
																															.removeAttr('selected')
																															.attr('disabled', 'disabled');
					$("#applicationInterviewers").append('<option value="'+ id +'" category="' + category + '">'+ selText + '</option>');
				}
			});
			//$('#programInterviewers').attr("size", $('#programInterviewers option').size() + 1);
			$('#applicationInterviewers').attr("size", $('#applicationInterviewers option').size() + 1);
			resetInterviwersErrors();
		}
	});
	
	
	// -----------------------------------------------------------------------------------------
	// Create interviewer button.
	// -----------------------------------------------------------------------------------------
	$('#createinterviewersection').on('click','#createInterviewer', function()
			{
				$('#createinterviewersection').append('<div class="ajax" />');
				var postData = {
					applicationId : $('#applicationId').val(),
					firstName: $('#newInterviewerFirstName').val(),
					lastName: $('#newInterviewerLastName').val(),
					email: $('#newInterviewerEmail').val()				
				}
				$.ajax({
					type: 'POST',
					 statusCode: {
						  401: function() {
							  window.location.reload();
						  }
					  },
					url:"/pgadmissions/interview/createInterviewer",
					data:	$.param(postData),
					success: function(data)
					{	
						var newInterviewer;
						try{
							newInterviewer = jQuery.parseJSON(data);
						}catch(err){
							$('#createinterviewersection').html(data);
							addToolTips();
							return;
						}
						if(newInterviewer.isNew){
							$('#previous').append('<option value="'+ newInterviewer.id + '" category="previous" disabled="disabled">' +
									newInterviewer.firstname + ' ' + newInterviewer.lastname+ '</option>');
							$('#applicationInterviewers').append('<option value="'  + newInterviewer.id + '">' +
									newInterviewer.firstname + ' ' + newInterviewer.lastname+ '</option>');
							$('#applicationInterviewers').attr("size", $('#applicationInterviewers option').size() + 1);
							$('#add-info-bar-div').html('New user ' + newInterviewer.toString + ' added as interviewer');
						}else{
							addExistingUserToInterviewersLists(newInterviewer);
						}
						resetInterviwersErrors();
						getCreateInterviewersSection();			
						
					},
				      complete: function()
				      {
						$('#createinterviewersection div.ajax').remove();
				      }
				});
				
			});
	
	
	// -----------------------------------------------------------------------------------------
	// Remove interviewer
	// -----------------------------------------------------------------------------------------
	$('#assignInterviewersToAppSection').on('click', '#removeInterviewerBtn', function(){

		var selectedReviewers = $('#applicationInterviewers').val();
		if (selectedReviewers)
		{
			selectedReviewers.forEach(function(id)
			{
				var selText = $("#applicationInterviewers option[value='" + id + "']").text();
				$("#applicationInterviewers option[value='" + id + "']").remove();
				//$("#programInterviewers").append('<option value="'+ id +'">'+ selText +'</option>');
				$("#programInterviewers option[value='" + id + "']").removeClass('selected')
																														 .removeAttr('disabled');
			});
			//$('#programInterviewers').attr("size", $('#programInterviewers option').size() + 1);
			$('#applicationInterviewers').attr("size", $('#applicationInterviewers option').size() + 1);
			resetInterviwersErrors();
		}
	});

	
	// -----------------------------------------------------------------------------------------
	// Submit button.
	// -----------------------------------------------------------------------------------------
	$('#moveToInterviewBtn').click(function()
	{
		
		$('#interviewsection').append('<div class="ajax" />');
		var url = "/pgadmissions/interview/move";
		
		$('#applicationInterviewers option').each(function(){	
			$('#postInterviewData').append("<input name='interviewers' type='text' value='" +  $(this).val() + "'/>");
		});
		
		var postData = {
				applicationId : 	$('#applicationId').val(),
				interviewers: 		'',
				interviewTime:	 	$('#hours').val() + ":" + $('#minutes').val(),
				furtherDetails:		$('#furtherDetails').val() ,
				interviewDueDate: 	$('#interviewDate').val(),
				locationURL:		$('#interviewLocation').val() 
		}
		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
			url: url,
			data:	$.param(postData) + "&" + $('input[name="interviewers"]').serialize(),
			success: function(data)
			{	
				if(data == "OK"){	
					window.location.href = '/pgadmissions/applications?messageCode=move.interview&application=' + $('#applicationId').val();
					
				}else{
					$('#temp').html(data);
					$('#assignInterviewersToAppSection').html($('#section_1').html());
					$('#interviewdetailsSection').html($('#section_2').html());
					$('#temp').empty();
					$('#postInterviewData').empty();
					addToolTips();
					$('#interviewDate').attr("readonly", "readonly");	
					$('#interviewDate').datepicker({
						dateFormat: 'dd M yy',
						changeMonth: true,
						changeYear: true,
						yearRange: '1900:c+20' });
				}
			},
		      complete: function()
		      {
		        $('#interviewsection div.ajax').remove();
		      }
		});
	});
		
		

});


function getInterviewersAndDetailsSections(){
	$('#interviewsection').append('<div class="ajax" />');
	
	var url  = "/pgadmissions/interview/interviewers_section";

	$.ajax({
		type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  }
		  },
		url: url +"?applicationId=" + $('#applicationId').val(), 
		success: function(data)
		{
			$('#temp').html(data);
			$('#assignInterviewersToAppSection').html($('#section_1').html());
			$('#interviewdetailsSection').html($('#section_2').html());
			$('#temp').empty();
			
			addToolTips();
			$('#interviewDate').attr("readonly", "readonly");	
			$('#interviewDate').datepicker({
				dateFormat: 'dd M yy',
				changeMonth: true,
				changeYear: true,
				yearRange: '1900:c+20' });
		},
	    complete: function()
	    {
				$('#interviewsection div.ajax').remove();
	    }
	});
}


function getCreateInterviewersSection(){
	$('#createinterviewersection').append('<div class="ajax" />');
	
	$.ajax({
		type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  }
		  },
		url:"/pgadmissions/interview/create_interviewer_section?applicationId=" + $('#applicationId').val(), 
		success: function(data)
		{
			$('#createinterviewersection').html(data);
		},
	    complete: function()
	    {
				$('#createinterviewersection div.ajax').remove();
	    }
	});
}


function addExistingUserToInterviewersLists(newInterviewer){
	
	if($('#applicationInterviewers option[value="' + newInterviewer.id + '"]').length > 0){
		$('#add-info-bar-div').html(newInterviewer.toString + ' is already selected as interviewer');
		return;
	}
	

	if($('#default option[value="' + newInterviewer.id + '"]').length > 0){		
		$('#default option[value="' + newInterviewer.id + '"]').attr("selected", 'selected');		
		$('#addInterviewerBtn').trigger('click');
		$('#add-info-bar-div').html('Default interviewer ' + newInterviewer.toString + ' selected as interviewer');
		return;
	}	

	if($('#previous option[value="' + newInterviewer.id + '"]').length > 0){
		$('#previous option[value="'+ newInterviewer.id + '"]').attr("selected", 'selected');		
		$('#addInterviewerBtn').trigger('click');
		$('#add-info-bar-div').html('Previous interviewer ' + newInterviewer.toString + ' selected as interviewer');
		return;
	}
	
	$('#previous').append('<option value="'  + newInterviewer.id + '" category="previous" disabled="disabled">' +
			newInterviewer.firstname + ' ' + newInterviewer.lastname+ '</option>');
	$('#applicationInterviewers').append('<option value="'  + newInterviewer.id + '">' +
			newInterviewer.firstname + ' ' + newInterviewer.lastname+ '</option>');
	$('#applicationInterviewers').attr("size", $('#applicationInterviewers option').size() + 1);
	$('#add-info-bar-div').html('Existing user ' + newInterviewer.toString + ' added as interviewer');
}

function resetInterviwersErrors(){
	if( $('#applicationInterviewers option').size() > 0){
		$('#interviewersErrorSpan').remove();
	}
}