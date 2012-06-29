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
		}
	});
	
	
	$('#createInterviewer').click(function() {
		$('#applicationInterviewers option').each(function(){
			$('#postInterviewerForm').append("<input name='pendingInterviewer' type='hidden' value='" +  $(this).val() + "'/>");	
		});
		$('#postInterviewerForm').append("<input name='applicationId' type='hidden' value='" +  $('#applicationId').val() + "'/>");
		$('#postInterviewerForm').append("<input name='encryptedInterviewId' type='hidden' value='" +  $('#interviewId').val() + "'/>");
		$('#postInterviewerForm').append("<input name='firstName' type='hidden' value='" +  $('#newInterviewerFirstName').val() + "'/>");
		$('#postInterviewerForm').append("<input name='lastName' type='hidden' value='" +  $('#newInterviewerLastName').val() + "'/>");
		$('#postInterviewerForm').append("<input name='email' type='hidden' value='" +  $('#newInterviewerEmail').val() + "'/>");		
		$('#postInterviewerForm').submit();
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
		}
	});

	
	
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
						dateFormat: 'dd-M-yy',
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
				dateFormat: 'dd-M-yy',
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
