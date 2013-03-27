$(document).ready(function(){
	addCounter();
	var empImgCount = 0;
	var numberOfSavedPositions = 0;
	
	$("#acceptTermsEPValue").val("NO");
	//limitTextArea();
	showOrHideAdPosisionButtonOnly();
	
	// -------------------------------------------------------------------------------
	// Show or hide the AdPosisionButton
	// -------------------------------------------------------------------------------
	function showOrHideAdPosisionButton() {
		numberOfSavedPositions = $("#positionSection .existing .button-edit").size();
		if (numberOfSavedPositions >= 5) {
			$("#addPosisionButton").removeClass("blue");
            $("#positionSaveAndCloseButton").addClass("clear");
            $('#position-H2').trigger('click');
		} else {
			$("#addPosisionButton").show();
			$("#positionSaveAndCloseButton").addClass("blue");
            $("#positionSaveAndCloseButton").removeClass("clear");
		}
	}
	
	// -------------------------------------------------------------------------------
	// Show or hide the AdPosisionButton Without minimizing 
	// -------------------------------------------------------------------------------
	function showOrHideAdPosisionButtonOnly() {
		numberOfSavedPositions = $("#positionSection .existing .button-edit").size();
		if (numberOfSavedPositions >= 5 && $("#position_title").val() == "") {
			$("#addPosisionButton").removeClass("blue");
			$("#addPosisionButton").attr('disabled','true');
            $("#positionSaveAndCloseButton").addClass("clear");
		} else {
			$("#addPosisionButton").show();
			$("#addPosisionButton").removeAttr('disabled');
			$("#positionSaveAndCloseButton").addClass("blue");
            $("#positionSaveAndCloseButton").removeClass("clear");
		}
	}
	
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
				showOrHideAdPosisionButton();
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
	    if (numberOfSavedPositions >= 5 && $("#position_title").val() == "") {
	    	 $('#position-H2').trigger('click');
	        return;
	    }
	    
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
				if (curruntPos == true)
				{
					$('#position_endDate').attr('disabled','disabled');
					$('#posi-end-date-lb').addClass('grey-label');
					$('#posi-end-date-lb em').hide();
				}
				$('#addPosisionButton').html('Update');
				$("#addPosisionButton").show();
			},
			completed: function()
			{
				$('#positionSection div.ajax').remove();
//				showOrHideAdPosisionButton();
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
		url:"/pgadmissions/update/editEmploymentPosition",
		data: { 
			position: $("#position_title").val(),
			startDate: $("#position_startDate").val(), 
			endDate: $("#position_endDate").val(), 
			remit: $("#position_remit").val(), 
			language: $("#position_language").val(), 
			'employerAddress.country': $("#position_country").val(),
			employerName: $("#position_employer_name").val(),
			'employerAddress.address1': $("#position_employer_address1").val(),
			'employerAddress.address2': $("#position_employer_address2").val(),
			'employerAddress.address3': $("#position_employer_address3").val(),
			'employerAddress.address4': $("#position_employer_address4").val(),
			'employerAddress.address5': $("#position_employer_address5").val(),
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
      showOrHideAdPosisionButton();
    }
	});
}