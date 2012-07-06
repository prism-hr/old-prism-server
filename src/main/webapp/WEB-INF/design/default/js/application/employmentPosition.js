$(document).ready(function(){

	var empImgCount = 0;
	
	$("#acceptTermsEPValue").val("NO");
	limitTextArea();
	
	// -------------------------------------------------------------------------------
	// Current employment checkbox.
	// -------------------------------------------------------------------------------
	$('#current').click(function()
	{
		$('#position-enddate-error').remove();
		if ($('#current:checked').val() !== undefined) 
		{
			// checked
			$("#posi-end-date-lb").text("End Date").addClass("grey-label");
			$('#position_endDate').attr("disabled", "disabled")
                            .val(''); // empty date field.
		}
		else
		{
			// unchecked
			$('#position_endDate').removeAttr("disabled");
			$("#posi-end-date-lb").text("End Date").append('<em>*</em>').removeClass("grey-label");
		}
	});
	
	
	// -------------------------------------------------------------------------------
	// Close button.
	// -------------------------------------------------------------------------------
	$('#positionCloseButton').click(function()
	{
		$('#position-H2').trigger('click');
		return false;
	});
	
	
	// -------------------------------------------------------------------------------
	// Delete employment button.
	// -------------------------------------------------------------------------------
	$('a[name="deleteEmploymentButton"]').click( function()
	{	
		var id = $(this).attr("id").replace("position_", "");
		$('#positionSection > div').append('<div class="ajax" />');
		$.ajax({
			type: 'POST',
			statusCode: {
				401: function()
				{
					window.location.reload();
				}
			},
			url:"/pgadmissions/deleteentity/employment",
			data: {
				id: id	
			}, 				
			success: function(data)
			{
				$('#positionSection').html(data);
			},
			completed: function()
			{
				$('#positionSection div.ajax').remove();
			}
		});
	});
	
	
	// -------------------------------------------------------------------------------
	// "Accept terms" checkbox.
	// -------------------------------------------------------------------------------
	$("input[name*='acceptTermsEPCB']").click(function()
	{
		if ($("#acceptTermsEPValue").val() =='YES')
		{
			$("#acceptTermsEPValue").val("NO");
		}
		else
		{	
			$("#acceptTermsEPValue").val("YES");
			empImgCount = 0;
		}
	});

	
	// -------------------------------------------------------------------------------
	// Save button.
	// -------------------------------------------------------------------------------
	$('#positionSaveAndCloseButton').click(function()
	{
		$("span[name='nonAcceptedEP']").html('');

		// Check for a "dirty" employment position form. If there is data try to submit it.
		if (!isFormEmpty('#positionSection'))
		{
			postEmploymentData('close');
		}
		else
		{
			unmarkSection('#positionSection');
			$('#positionCloseButton').trigger('click');
		}
	});

	$('#addPosisionButton').click(function()
	{
		$("span[name='nonAcceptedEP']").html('');
		postEmploymentData('add');
	});


	// -------------------------------------------------------------------------------
	// Edit existing funding.
	// -------------------------------------------------------------------------------
	$('a[name="positionEditButton"]').click(function()
	{
		var id = this.id;
		id = id.replace('position_', '');	
		$('#positionSection > div').append('<div class="ajax" />');
		$.ajax({
		 type: 'GET',
		 statusCode: {
				401: function()
				{
					window.location.reload();
				}
			},
			url: "/pgadmissions/update/getEmploymentPosition",
			data:	{
				applicationId:  $('#applicationId').val(), 
				employmentId: id,
				message: 'edit',					
				cacheBreaker: new Date().getTime()
			}, 
			success: function(data)
			{								
				$('#positionSection').html(data);
				var curruntPos = $('#current').is(':checked');
				if(curruntPos == true){
					$('#position_endDate').attr('disabled','disabled');
					$('#posi-end-date-lb').addClass('grey-label');
					$('#posi-end-date-lb em').hide();
				}
				$('#addPosisionButton').html('Update');
			},
			completed: function()
			{
				$('#positionSection div.ajax').remove();
			}
		});
	});


	// -------------------------------------------------------------------------------
	// Clear button.
	// -------------------------------------------------------------------------------
	$('#positionclearButton').click(function()
	{
		$('#positionSection > div').append('<div class="ajax" />');
		loadEmploymentSection(true);
	});

	bindDatePicker('#position_startDate');
	bindDatePicker('#position_endDate');
	addToolTips();

});


// -------------------------------------------------------------------------------
// Posting employment position data.
// -------------------------------------------------------------------------------
function postEmploymentData(message)
{
	var current = false;
	if ($('#current:checked').val() !== undefined)
	{
		 current = true;
	}

	$('#positionSection > div').append('<div class="ajax" />');
	var acceptedTheTerms;
	if ($("#acceptTermsEPValue").val() == 'NO')
	{
		acceptedTheTerms = false;
	}
	else
	{
		acceptedTheTerms = true;
	}
	$.ajax({
		type: 'POST',
		 statusCode: {
			  401: function()
				{
				  window.location.reload();
			  }
		  },
		url:"/pgadmissions/update/editEmploymentPosition",
		data: { 
			position: $("#position_title").val(),
			startDate: $("#position_startDate").val(), 
			endDate: $("#position_endDate").val(), 
			remit: $("#position_remit").val(), 
			language: $("#position_language").val(), 
			employerCountry: $("#position_country").val(),
			employerName: $("#position_employer_name").val(),
			employerAddress: $("#position_employer_address").val(),
			current: current,
			application: $("#applicationId").val(),
			applicationId: $("#applicationId").val(),		 
			employmentId: $("#positionId").val(), 
			message:message,
			acceptedTerms: acceptedTheTerms
		},
	   success: function(data)
		 {
				$('#positionSection').html(data);
			
				var errorCount = $('#positionSection .invalid:visible').length;
				if (message == 'close' && errorCount == 0)
				{
					$('#position-H2').trigger('click');
				}
				if (errorCount > 0)
				{
					markSectionError('#positionSection');
				}
      },
    complete: function()
    {
      $('#positionSection div.ajax').remove();
    }
	});
}