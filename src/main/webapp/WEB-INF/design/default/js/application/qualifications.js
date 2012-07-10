$(document).ready(function(){
	
	var qualImgCount = 0;
	
	$("#acceptTermsQDValue").val("NO");
	
	// -------------------------------------------------------------------------------
	// Close button.
	// -------------------------------------------------------------------------------
	$('#qualificationsCloseButton').click(function()
	{
		$('#qualifications-H2').trigger('click');
		return false;
	});
	

	// -------------------------------------------------------------------------------
	// Checkbox to mark the qualification as current.
	// -------------------------------------------------------------------------------
	$("input[name*='currentQualificationCB']").click(function()
	{
		$('#qualification-awarddate-error').remove();
		if ($("#currentQualification").val() =='YES')
		{
			// Uncheck the box
			$("#currentQualification").val("NO");
			$("#qualificationAwardDate").val("");
			$("#qualificationAwardDate").attr("disabled", "disabled");
			$("#proofOfAward").val("");
			$("#proofOfAward").attr("disabled", "disabled");
			$("#quali-grad-id em").remove();
			$("#quali-grad-id").text("Expected Grade / Result / GPA").append('<em>*</em>');
			$("#quali-award-date-lb").text("Award Date").addClass("grey-label");
			$("#quali-proof-of-award-lb").text("Proof of award (PDF)").addClass("grey-label");
			
			if ($('#uploadFields').hasClass('uploaded'))
			{
				// Delete any uploaded file.
				ajaxProofOfAwardDelete();
				
				$('#uploadFields').removeClass('uploaded');
				$('#uploadFields a.button-edit').hide();
			}
			
			if ($('#document_PROOF_OF_AWARD').length > 0)
			{
				$('#document_PROOF_OF_AWARD').val('');
			}
		}
		else
		{		
			// Check the box
			$("#currentQualification").val("YES");
			$("#qualificationAwardDate").removeAttr("disabled", "disabled");	
			$("#proofOfAward").removeAttr("disabled");
			$("#quali-grad-id em").remove();
			$("#quali-grad-id").text("Grade / Result / GPA").append('<em>*</em>');
			$("#quali-award-date-lb em").remove();
			$("#quali-award-date-lb").append('<em>*</em>').removeClass("grey-label");
			$("#quali-proof-of-award-lb").removeClass("grey-label");

			if ($('#uploadFields').hasClass('uploaded'))
			{
				$('#uploadFields').removeClass('upload-delete');
				$('#uploadFields a.button-edit').show();
			}
		}
	});

	
	// -------------------------------------------------------------------------------
	// Delete a qualification.
	// -------------------------------------------------------------------------------
	$('a[name="deleteQualificationButton"]').click(function()
	{
		var id = $(this).attr("id").replace("qualification_", "");
		$('#qualificationsSection > div').append('<div class="ajax" />');
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
					  }
				},
			url:"/pgadmissions/deleteentity/qualification",
			data:	{
					id: id	
				}, 
			success:	function(data) {
					$('#qualificationsSection').html(data);
				},
			completed: function()
			{
				$('#qualificationsSection div.ajax').remove();
			}
				
		});
	});
	

	// -------------------------------------------------------------------------------
	// "Accept terms" checkbox.
	// -------------------------------------------------------------------------------
	$("input[name*='acceptTermsQDCB']").click(function()
	{
		if ($("#acceptTermsQDValue").val() =='YES')
		{
			$("#acceptTermsQDValue").val("NO");
		}
		else
		{	
			$("#acceptTermsQDValue").val("YES");

			qualImgCount = 0;
			
		}
	});
	

	// -------------------------------------------------------------------------------
	// Add a qualification.
	// -------------------------------------------------------------------------------
	$('#addQualificationButton').click(function()
	{
		$("span[name='nonAcceptedQD']").html('');
		postQualificationData('add');
	});
	

	// -------------------------------------------------------------------------------
	// Save qualification.
	// -------------------------------------------------------------------------------
	$('#qualificationsSaveButton').click(function()
	{
		$("span[name='nonAcceptedQD']").html('');
		
		// Check for a "dirty" qualification form. If there is data try to submit it.
		if (!isFormEmpty('#qualificationsSection form'))
		{
			postQualificationData('close');
		}
		else
		{
			unmarkSection('#qualificationsSection');
			$('#qualificationsCloseButton').trigger('click');
		}
	});
	

	// -------------------------------------------------------------------------------
	// Edit a qualification.
	// -------------------------------------------------------------------------------
	$('a[name="editQualificationLink"]').click(function()
	{
		var id = this.id;
		id = id.replace('qualification_', '');	
		$('#qualificationsSection > div').append('<div class="ajax" />');
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
				  }
			  },
				url:"/pgadmissions/update/getQualification",
				data:{
					applicationId:  $('#applicationId').val(),
					qualificationId: id,
					message: 'edit',					
					cacheBreaker: new Date().getTime()
				},
				success: function(data)
				{
					$('#qualificationsSection').html(data);
					
					if ($("#currentQualificationCB").is(":checked"))
					{
						$("#qualificationAwardDate").removeAttr("disabled");
						$("#proofOfAward").removeAttr("disabled");
						$("#quali-grad-id em").remove();
						$("#quali-grad-id").text("Grade / Result / GPA").append('<em>*</em>');
						$("#quali-award-date-lb em").remove();
						$("#quali-award-date-lb").append('<em>*</em>').removeClass("grey-label");
						$("#quali-proof-of-award-lb").removeClass("grey-label");
					}
					else
					{	
						$("#qualificationAwardDate").attr("disabled", "disabled");
						$("#proofOfAward").attr("disabled", "disabled");
						$("#quali-grad-id em").remove();
						$("#quali-grad-id").text("Expected Grade / Result / GPA").append('<em>*</em>');
						$("#quali-award-date-lb em").remove();
						$("#quali-award-date-lb").text("Award Date").addClass("grey-label");
						$("#quali-proof-of-award-lb").text("Proof of award (PDF)").addClass("grey-label");
					}
					
					// Cheap way of changing the button text.
					$('#addQualificationButton').html('Update');
				},
				completed: function()
				{
					$('#qualificationsSection div.ajax').remove();
				}
		});
	});
	

	// -------------------------------------------------------------------------------
	// Clear button.
	// -------------------------------------------------------------------------------
	$('#qualificationClearButton').click(function()
	{
		$('#qualificationsSection > div').append('<div class="ajax" />');
		loadQualificationsSection(true);
	});

	
	bindDatePicker('#qualificationStartDate');
	bindDatePicker('#qualificationAwardDate');
	addToolTips();
	
  // Generic file upload solution...
	watchUpload($('#proofOfAward'));	
});

function postQualificationData(message)
{
	$('#qualificationsSection > div').append('<div class="ajax" />');
	var acceptedTheTerms;
	if ($("#acceptTermsQDValue").val() == 'NO'){
		acceptedTheTerms = false;
	}
	else{
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
			  }
		  },
		url: "/pgadmissions/update/editQualification",
		data: {  
			qualificationSubject: $("#qualificationSubject").val(), 
			qualificationInstitution: $("#qualificationInstitution").val(), 
			qualificationType: $("#qualificationType").val(),
			qualificationGrade: $("#qualificationGrade").val(),
			qualificationScore: $("#qualificationScore").val(),
			qualificationStartDate: $("#qualificationStartDate").val(),
			qualificationLanguage: $("#qualificationLanguage").val(),
			qualificationAwardDate: $("#qualificationAwardDate").val(),
			completed: $("#currentQualification").val(),			
			qualificationId: $("#qualificationId").val(),
			applicationId:  $('#applicationId').val(),
			application:  $('#applicationId').val(),
			institutionCountry: $('#institutionCountry').val(),
			proofOfAward: $('#document_PROOF_OF_AWARD').val(),
			message:message,
			acceptedTerms: acceptedTheTerms
		},
		success: function(data)
		{
			$('#qualificationsSection').html(data);
			var errorCount = $('#qualificationsSection .invalid:visible').length;
	
			if (errorCount == 0 && message == 'close')
			{
				// Close the section only if there are no errors.
			
					$('#qualifications-H2').trigger('click');
						
			}
			if (errorCount > 0)
			{
				markSectionError('#qualificationsSection');
			}
		},
    complete: function()
    {
			$('#qualificationsSection div.ajax').remove();
    }
	});
}

function ajaxProofOfAwardDelete()
{
	if ($('#profOfAwardId') && $('#profOfAwardId').val() && $('#profOfAwardId').val() != '')
	{
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
				  }
			  },
			url: "/pgadmissions/delete/asyncdelete",
			data: {
				documentId: $('#profOfAwardId').val()				
			}				
		});

	}
}

function ajaxProofOfAwardUpload()
{	
	// Showing/hiding progress bar when we're uploading the file via AJAX.	
	$("#progress").ajaxStart(function()
		{
			$(this).show();
		})
		.ajaxComplete(function()
		{
			$(this).hide();
			$('#progress').html("");
		}
	);

	// Remove any previous error messages.
	$('#qualUploadedDocument').find('span.invalid').remove();

	// Functionality for uploading files via AJAX (immediately on selection).
	$.ajaxFileUpload(
	{
		url:            '/pgadmissions/documents/async',
		secureuri:      false,
		fileElementId:  'proofOfAward',	
		dataType:       'text',
		data:           { type: 'PROOF_OF_AWARD' },
		success: function(data)
		{	
			if ($(data).find('span.invalid').length == 0)
			{
				// i.e. if there are no uploading errors, which would be indicated by the presence of a SPAN.invalid tag.
				$('#qualUploadedDocument').html(data);
				$('#qualUploadedDocument').show();
				$('#uploadFields').addClass('uploaded');
				$('span[name="supportingDocumentSpan"] a.button-edit').attr({'id':'editQualiPOA','data-desc':'Edit Proof Of Award'});
			}
			else
			{
				$('#qualUploadedDocument').append(data);
			}
		}
	});

}